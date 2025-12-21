/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package glz.hawk.j4sql.mybatis.translator;

import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.extension.column.ColumnFunctionConstant;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.dao.function.ColumnDescriptor;
import glz.hawkframework.dao.function.ColumnFunction;
import glz.hawkframework.dao.function.IRecord;
import glz.hawkframework.core.support.ArgumentSupport;
import glz.hawk.codepoet.java.ClassSpec;
import glz.hawk.codepoet.java.FieldSpec;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import glz.hawk.codepoet.java.type.WildcardTypeName;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class PersistObjectCustomizer implements BiConsumer<ClassSpec.Builder, Table> {

    private final Translator<Column, String> fieldNameTranslator;
    private final Translator<Column, String> getterNameTranslator;
    private final Translator<Column, String> setterNameTranslator;
    private final Translator<Column, String> updatedGetterNameTranslator;
    private final Map<Class<?>, Class<? extends ColumnFunction>> functionMap = new HashMap<>();

    public PersistObjectCustomizer(Translator<Column, String> fieldNameTranslator, Translator<Column, String> getterNameTranslator,
                                   Translator<Column, String> setterNameTranslator, Translator<Column, String> updatedGetterNameTranslator,
                                   Map<Class<? extends Annotation>, Class<? extends ColumnFunction>> functionMap) {
        this.fieldNameTranslator = argNotNull(fieldNameTranslator, "fieldNameTranslator");
        this.getterNameTranslator = argNotNull(getterNameTranslator, "getterNameTranslator");
        this.setterNameTranslator = argNotNull(setterNameTranslator, "setterNameTranslator");
        this.updatedGetterNameTranslator = updatedGetterNameTranslator;
        if (functionMap != null) {
            this.functionMap.putAll(functionMap);
        }
    }

    @Override
    public void accept(ClassSpec.Builder builder, Table table) {
        final String functionMapFieldName = "functionMap";
        final boolean[] flag = new boolean[]{false};
        Arrays.stream(table.getColumns()).forEach(column -> {
            column.getExtension(ColumnFunctionConstant.class.getCanonicalName()).ifPresent(extension -> {
                functionMap.keySet().forEach(c -> {
                    Class<?> extensionClass = extension instanceof Annotation ? ((Annotation) extension).annotationType() : extension.getClass();
                    if (c.isAssignableFrom(extensionClass)) {
                        Class<?> functionClass = functionMap.get(extensionClass);
                        if (functionClass == null) {
                            throw new IllegalStateException(String.format("Found no matched functionClass of %s", extensionClass.getCanonicalName()));
                        }
                        if (updatedGetterNameTranslator == null) {
                            builder.beginInstanceInitializer().addStatement("$L.put($T.class, new $T<>($S, $S, this::$L, null, this::$L))", functionMapFieldName, functionClass, ColumnDescriptor.class, column.getName(), fieldNameTranslator.translate(column), getterNameTranslator.translate(column), setterNameTranslator.translate(column)).end();
                        } else {
                            builder.beginInstanceInitializer().addStatement("$L.put($T.class, new $T<>($S, $S, this::$L, this::$L, this::$L))", functionMapFieldName, functionClass, ColumnDescriptor.class, column.getName(), fieldNameTranslator.translate(column), getterNameTranslator.translate(column), updatedGetterNameTranslator.translate(column), setterNameTranslator.translate(column)).end();
                        }
                        flag[0] = true;
                    }
                });
            });
        });
        if (flag[0]) {
            builder.addSuperInterface(IRecord.class);
            builder.addField(
                FieldSpec.builder(ParameterizedTypeName.of(
                        Map.class,
                        ParameterizedTypeName.of(Class.class, WildcardTypeName.ofUpper(ColumnFunction.class)),
                        ParameterizedTypeName.of(ColumnDescriptor.class, WildcardTypeName.of())
                    ), functionMapFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .setInitializer("new $T<>()", HashMap.class)
                    .setJavadoc("This map holds the relations between function class and column")
                    .build()
            );

            String columnFunctionClassParamName = "columnFunctionClass";
            builder.addMethod(
                MethodSpec.builder(ParameterizedTypeName.of(Optional.class, ParameterizedTypeName.of(ColumnDescriptor.class, WildcardTypeName.of())), "findColumnDescriptor")
                    .addModifier(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(ParameterizedTypeName.of(Class.class, WildcardTypeName.ofUpper(ColumnFunction.class)), columnFunctionClassParamName)
                    .beginMethodBody()
                    .addStatement("return $T.ofNullable($L.get(argNotNull($L, $S)))", Optional.class, functionMapFieldName, columnFunctionClassParamName, columnFunctionClassParamName)
                    .end()
                    .build()
            );
            builder.addStaticImport(ArgumentSupport.class, "*");
        }
    }


}
