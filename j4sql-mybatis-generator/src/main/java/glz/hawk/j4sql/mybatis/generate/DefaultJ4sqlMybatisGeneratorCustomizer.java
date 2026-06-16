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

package glz.hawk.j4sql.mybatis.generate;

import glz.hawk.codepoet.ddl.DataTypeSpec;
import glz.hawk.codepoet.java.AnnotationInstanceSpec;
import glz.hawk.codepoet.java.ClassSpec;
import glz.hawk.codepoet.java.javadoc.FieldJavadoc;
import glz.hawk.codepoet.java.javadoc.MethodJavadoc;
import glz.hawk.codepoet.java.javadoc.TypeJavadoc;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.j4sql.mybatis.translator.ColumnFinder;
import glz.hawk.j4sql.mybatis.translator.DefaultTableToSupportFactory;
import glz.hawk.j4sql.mybatis.translator.TableToSupportFactory;
import glz.hawk.jdesigner.generator.ModelFilter;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Index;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.jdesigner.translator.database.*;
import glz.hawk.jdesigner.translator.java.UniversalTypeToTypeName;
import glz.hawk.jdesigner.translator.meta.ColumnToTypeName;
import glz.hawkframework.dao.column.annotation.RecordVersion;
import org.apache.ibatis.type.JdbcType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static glz.hawk.jdesigner.translator.convert.NameConverter.UPPER_UNDERSCORE_TO_LOWER_CAMEL;
import static glz.hawk.jdesigner.translator.convert.NameConverter.UPPER_UNDERSCORE_TO_UPPER_CAMEL;
import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class DefaultJ4sqlMybatisGeneratorCustomizer implements J4sqlMybatisGeneratorCustomizer {

    private final String dialect;

    public DefaultJ4sqlMybatisGeneratorCustomizer(String dialect) {
        this.dialect = argNotBlank(dialect, "dialect");
    }

    public String getDialect() {
        return dialect;
    }

    @Override
    public Translator<Table, String> getPoClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Po";
    }

    @Override
    public Translator<Table, List<AnnotationInstanceSpec>> getPoClassAnnotationsTranslator() {
        return new TableToClassAnnotations(new UniversalTypeToTypeName());
    }

    @Override
    public Translator<Table, TypeJavadoc> getPoJavadocTranslator() {
        return new TableToPojoJavadoc(getTableNameTranslator());
    }

    @Override
    public Translator<Column, String> getFieldNameTranslator() {
        return column -> UPPER_UNDERSCORE_TO_LOWER_CAMEL.convertTo(column.getName().toUpperCase());
    }

    @Override
    public Translator<Column, TypeName> getFieldTypeNameTranslator() {
        return new ColumnToTypeName();
    }

    @Override
    public Translator<Column, FieldJavadoc> getFieldJavadocTranslator() {
        return new ColumnToFieldJavadoc(getColumnNameTranslator());
    }

    @Override
    public Translator<Column, List<AnnotationInstanceSpec>> getFieldAnnotationsTranslator() {
        return new ColumnToFieldAnnotations(new UniversalTypeToTypeName());
    }

    @Override
    public Translator<Column, String> getGetterNameTranslator() {
        return column -> "get" + UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(column.getName().toUpperCase());
    }

    @Override
    public Translator<Column, MethodJavadoc> getGetterJavadocTranslator() {
        return new ColumnToGetterJavadoc(getColumnNameTranslator());
    }

    @Override
    public Translator<Column, String> getSetterNameTranslator() {
        return column -> "set" + UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(column.getName().toUpperCase());
    }

    @Override
    public Translator<Column, MethodJavadoc> getSetterJavadocTranslator() {
        return new ColumnToSetterJavadoc(getColumnNameTranslator());
    }

    @Override
    public List<BiConsumer<ClassSpec.Builder, Table>> getPoCustomizers() {
        return Collections.emptyList();
    }

    @Override
    public Translator<Table, List<AnnotationInstanceSpec>> getUpdateClassAnnotationsTranslator() {
        return t -> Collections.emptyList();
    }

    @Override
    public Translator<Table, String> getUpdateClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Update";
    }

    @Override
    public Translator<Column, String> getFieldUpdatedNameTranslator() {
        return column -> UPPER_UNDERSCORE_TO_LOWER_CAMEL.convertTo(column.getName().toUpperCase()) + "Updated";
    }

    @Override
    public Translator<Column, String> getGetterUpdatedNameTranslator() {
        return column -> "is" + UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(column.getName().toUpperCase()) + "Updated";
    }

    @Override
    public List<BiConsumer<ClassSpec.Builder, Table>> getUpdateCustomizers() {
        return Collections.emptyList();
    }

    @Override
    public Translator<Table, List<AnnotationInstanceSpec>> getColumnUpdateClassAnnotationsTranslator() {
        return t -> Collections.emptyList();
    }

    @Override
    public Translator<Table, String> getColumnUpdateClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "ColumnUpdate";
    }

    @Override
    public List<BiConsumer<ClassSpec.Builder, Table>> getColumnUpdateCustomizers() {
        return Collections.emptyList();
    }

    @Override
    public Translator<Table, String> getSupportClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Support";
    }

    @Override
    public Translator<Table, String> getFieldTableNameTranslator() {
        return table -> table.getName().toUpperCase();
    }

    @Override
    public Translator<Table, String> getTableNameTranslator() {
        return table -> table.getName().toUpperCase();
    }

    @Override
    public Translator<Column, String> getFieldColumnNameTranslator() {
        return column -> column.getName().toUpperCase();
    }

    @Override
    public Translator<Column, String> getColumnNameTranslator() {
        return column -> column.getName().toUpperCase();
    }

    @Override
    public Translator<Column, String> getJdbcTypeNameTranslator() {
        return column -> JdbcType.forCode(column.getDataType().getType()).name();
    }

    @Override
    public Translator<Table, String> getMapperClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Mapper";
    }

    @Override
    public Translator<Table, String> getSqlProviderClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "SqlProvider";
    }

    @Override
    public Translator<Column, String> getColumnParamNameTranslator() {
        return column -> UPPER_UNDERSCORE_TO_LOWER_CAMEL.convertTo(column.getName().toUpperCase());
    }

    @Override
    public ColumnFinder getRecordVersionColumnFinder() {
        return table -> TableHelper.findColumn(table, RecordVersion.class);
    }

    @Override
    public Translator<Table, String> getRepositoryClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Repository";
    }

    @Override
    public Translator<Table, String> getAbstractRepositoryClassNameTranslator() {
        return table -> "Abstract" + UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Repository";
    }

    @Override
    public Translator<Table, String> getRepositoryImplClassNameTranslator() {
        return table -> UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "RepositoryImpl";
    }

    @Override
    public Translator<Index, String> getIndexNameTranslator() {
        return index -> index.getName().toUpperCase();
    }

    @Override
    public Translator<Table, String> getPrimaryKeyNameTranslator() {
        return table -> String.format("PK_%s", getTableNameTranslator().translate(table));
    }

    @Override
    public Translator<Table, List<String>> getTableSuffixesTranslator() {
        return table -> Collections.emptyList();
    }

    @Override
    public Translator<Column, DataTypeSpec> getDataTypeSpecTranslator() {
        return new ColumnToDataTypeSpec();
    }

    @Override
    public Translator<Table, Optional<String>> getTableCommentTranslator() {
        return new TableToComment();
    }

    @Override
    public Translator<Column, Optional<String>> getColumnCommentTranslator() {
        return new ColumnToComment();
    }

    @Override
    public ModelFilter<Table> getTableFilter() {
        return table -> true;
    }

    @Override
    public TableToSupportFactory getTableToSupportFactory() {
        return new DefaultTableToSupportFactory();
    }
}
