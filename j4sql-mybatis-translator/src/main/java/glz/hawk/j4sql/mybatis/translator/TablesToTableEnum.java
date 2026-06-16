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

import glz.hawk.codepoet.java.*;
import glz.hawk.codepoet.java.type.ArrayTypeName;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import glz.hawk.j4sql.support.PhysicalTable;
import glz.hawk.j4sql.support.TableDescriptor;
import glz.hawk.j4sql.support.impl.DefaultAliasedNamedColumn;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.core.helper.MapHelper;
import glz.hawkframework.core.support.ArgumentSupport;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static glz.hawkframework.core.support.ArgumentSupport.argNotEmpty;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

public class TablesToTableEnum implements Translator<List<Table>, JavaFile> {
    private final Translator<Table, String> supportClassPackageTranslator;
    private final Translator<Table, String> supportClassNameTranslator;
    private final Translator<Table, String> fieldTableNameTranslator;
    private final String tableEnumClassPackage;
    private final String tableEnumClassName;

    public TablesToTableEnum(Translator<Table, String> supportClassPackageTranslator, Translator<Table, String> supportClassNameTranslator, Translator<Table, String> fieldTableNameTranslator, String tableEnumClassPackage, String tableEnumClassName) {
        this.supportClassPackageTranslator = argNotNull(supportClassPackageTranslator, "supportClassPackageTranslator");
        this.supportClassNameTranslator = argNotNull(supportClassNameTranslator, "supportClassNameTranslator");
        this.fieldTableNameTranslator = argNotNull(fieldTableNameTranslator, "fieldTableNameTranslator");
        this.tableEnumClassPackage = argNotNull(tableEnumClassPackage, "tableEnumClassPackage");
        this.tableEnumClassName = argNotNull(tableEnumClassName, "tableEnumClassName");
    }

    @Override
    public @NonNull JavaFile translate(@NonNull List<Table> tables) {
        return JavaFile.builder(tableEnumClassPackage,
            EnumSpec.builder(tableEnumClassName, Modifier.PUBLIC).addSuperInterface(TableDescriptor.class)
                .add(b -> {
                    for (Table table : argNotEmpty(tables, "tables")) {
                        ClassName supportClassName = ClassName.of(supportClassPackageTranslator.translate(table), supportClassNameTranslator.translate(table));
                        ClassSpec.Builder anonymousBuilder = ClassSpec.anonymousBuilder();
                        table.getComment().ifPresent(anonymousBuilder::setJavadoc);
                        anonymousBuilder.addMethod(MethodSpec.builder(ArrayTypeName.ofClass(DefaultAliasedNamedColumn.class), "getColumns", Modifier.PUBLIC).addAnnotation(Nonnull.class).beginMethodBody().addStatement("return $T.COLUMNS", supportClassName).end().build());
                        anonymousBuilder.addMethod(MethodSpec.builder(ArrayTypeName.ofClass(DefaultAliasedNamedColumn.class), "getPrimaryKeyColumns", Modifier.PUBLIC).addAnnotation(Nonnull.class).beginMethodBody().addStatement("return $T.PRIMARY_KEY_COLUMNS", supportClassName).end().build());
                        anonymousBuilder.addMethod(MethodSpec.builder(PhysicalTable.class, "getTable", Modifier.PUBLIC).addAnnotation(Nonnull.class).beginMethodBody().addStatement("return $T.$L", supportClassName, fieldTableNameTranslator.translate(table)).end().build());
                        b.addEnumConstant(enumConstantName(table), anonymousBuilder.build());
                    }
                })
                .addField(buildNameMapField(tables))
                .addMethod(buildFromNameMethod())
                .addMethod(buildParseByNameMethod())
                .build()
        ).build();
    }

    protected String enumConstantName(Table table) {
        return table.getName().toUpperCase();
    }

    protected ClassName enumClassName() {
        return ClassName.of(tableEnumClassPackage, tableEnumClassName);
    }

    protected MethodSpec buildParseByNameMethod() {
        return MethodSpec.builder(enumClassName(),"parseByName",Modifier.PUBLIC,Modifier.STATIC)
            .addAnnotation(Nonnull.class)
            .addParameter(ParameterSpec.builder(String.class, "name").addAnnotation(Nonnull.class).build())
            .beginMethodBody()
            .addStatement("return $T.ofNullable(nameMap.get($T.argNotBlank(name, \"name\"))).orElseThrow(() -> new IllegalStateException(String.format(\"Found no $T named: %s\", name)))",
                Optional.class, ArgumentSupport.class,enumClassName())
            .end()
            .build();
    }

    protected MethodSpec buildFromNameMethod() {
        return MethodSpec.builder(enumClassName(), "fromName", Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Nullable.class)
            .addParameter(ParameterSpec.builder(String.class, "name").addAnnotation(Nullable.class).build())
            .beginMethodBody()
            .addStatement("return name == null ? null : nameMap.get(name)")
            .end()
            .build();
    }

    protected FieldSpec buildNameMapField(List<Table> tables) {
        return FieldSpec.builder(ParameterizedTypeName.of(Map.class, ClassName.ofClass(String.class), enumClassName()), "nameMap", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
            .setInitializer(JavaCodeBlock.builder()
                .add("$T.<String, $T>builder()", MapHelper.class, enumClassName())
                .newLine()
                .indent()
                .add(b -> {
                    tables.forEach(table -> {
                        b.add(".put($L.name(), $L)", enumConstantName(table), enumConstantName(table)).newLine();
                    });
                })
                .add(".buildHashMap()")
                .unindent()
                .build())
            .build();
    }
}
