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
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.j4sql.support.impl.DefaultValueColumn;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.javadoc.HtmlTag;
import glz.hawk.codepoet.java.javadoc.MethodJavadoc;
import glz.hawk.codepoet.java.type.TypeName;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ColumnToSqlFieldUpdatedSetter implements Translator<Column, MethodSpec> {

    private final Translator<Column, String> methodNameTranslator;
    private final Translator<Column, String> fieldNameTranslator;
    private final Translator<Column, TypeName> typeNameTranslator;
    private final Translator<Column, String> fieldUpdatedNameTranslator;

    public ColumnToSqlFieldUpdatedSetter(Translator<Column, String> methodNameTranslator, Translator<Column, String> fieldNameTranslator,
                                         Translator<Column, TypeName> typeNameTranslator, Translator<Column, String> fieldUpdatedNameTranslator) {
        this.methodNameTranslator = argNotNull(methodNameTranslator, "methodNameTranslator");
        this.fieldNameTranslator = argNotNull(fieldNameTranslator, "fieldNameTranslator");
        this.typeNameTranslator = argNotNull(typeNameTranslator, "typeNameTranslator");
        this.fieldUpdatedNameTranslator = argNotNull(fieldUpdatedNameTranslator, "fieldUpdatedNameTranslator");
    }

    @Nonnull
    @Override
    public MethodSpec translate(@Nonnull Column column) {
        argNotNull(column, "column");
        String fieldName = fieldNameTranslator.translate(column);
        String fieldUpdatedName = fieldUpdatedNameTranslator.translate(column);
        String methodName = methodNameTranslator.translate(column);
        TypeName typeName = typeNameTranslator.translate(column);
        return MethodSpec.builder(VOID, methodName, Modifier.PUBLIC)
            .setJavadoc(buildJavadoc((column)))
            .addParameter(typeName, fieldName)
            .beginMethodBody()
            .addStatement("this.$L = $L == null ? null : new $T($L)", fieldName, fieldName, DefaultValueColumn.class, fieldName)
            .addStatement("this.$L = $L", fieldUpdatedName, true)
            .end()
            .build();
    }

    protected MethodJavadoc buildJavadoc(Column column) {
        MethodJavadoc.Builder javadocBuilder = MethodJavadoc.builder();
        if (column.getComment().isPresent()) {
            javadocBuilder.beginJavadoc().addDocument("Sets $L", column.getComment().get());
            javadocBuilder.beginJavadoc().addInlineHtml(HtmlTag.P, "Map to column: $L", column.getName());
        } else {
            javadocBuilder.beginJavadoc().addDocument("Map to column: $L", column.getName());
        }
        return javadocBuilder.build();
    }
}
