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
import glz.hawk.j4sql.mybatis.translator.TableToSupportFactory;
import glz.hawk.jdesigner.generator.ModelFilter;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Index;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * This interface is responsible for
 *
 * @author Zhang Peng
 */
public interface J4sqlMybatisGeneratorCustomizer {
    /**
     * Name转换类：用来经Table转换为Po Class的name
     */
    Translator<Table, String> getPoClassNameTranslator();

    /**
     * AnnotationSpec转换类，用来将Table转换为Po Class的Annotations
     */
    Translator<Table, List<AnnotationInstanceSpec>> getPoClassAnnotationsTranslator();

    /**
     * Javadoc转换类：用来经Table转换为Po Javadoc
     */
    Translator<Table, TypeJavadoc> getPoJavadocTranslator();

    /**
     * Name转换类：用来将Column转换为Field Name.
     */
    Translator<Column, String> getFieldNameTranslator();

    /**
     * TypeName转换类：用来将Column转换为TypeName.
     */
    Translator<Column, TypeName> getFieldTypeNameTranslator();

    /**
     * Javadoc转换类：用来将Column转换为Field Javadoc
     */
    Translator<Column, FieldJavadoc> getFieldJavadocTranslator();

    /**
     * Annotation转换类：用来将Column转换为Field annotations
     */
    Translator<Column, List<AnnotationInstanceSpec>> getFieldAnnotationsTranslator();

    /**
     * Name转换类：用来将Column转换为Getter Name.
     */
    Translator<Column, String> getGetterNameTranslator();

    /**
     * Javadoc转换类：用来将Column转换为Getter Javadoc.
     */
    Translator<Column, MethodJavadoc> getGetterJavadocTranslator();

    /**
     * Name转换类：用来将Column转换为Setter Name.
     */
    Translator<Column, String> getSetterNameTranslator();

    /**
     * Javadoc转换类：用来将Column转换为Setter Javadoc.
     */
    Translator<Column, MethodJavadoc> getSetterJavadocTranslator();

    /**
     * Customizer类：用来给Po Class提供额外的属性或方法。
     */
    List<BiConsumer<ClassSpec.Builder, Table>> getPoCustomizers();


    /**
     * AnnotationSpec转换类，用来将Table转换为Update Class的Annotations
     */
    Translator<Table, List<AnnotationInstanceSpec>> getUpdateClassAnnotationsTranslator();

    /**
     * Name转换类：用来将Table转换为Update Class的name
     */
    Translator<Table, String> getUpdateClassNameTranslator();

    /**
     * Name转换类：用来将Column转换为Updated Field的name
     */
    Translator<Column, String> getFieldUpdatedNameTranslator();

    /**
     * Name转换类：用来将Column转换为Updated Getter的name
     */
    Translator<Column, String> getGetterUpdatedNameTranslator();

    /**
     * Customizer类：用来给Update Class提供额外的属性或方法。
     */
    List<BiConsumer<ClassSpec.Builder, Table>> getUpdateCustomizers();

    /**
     * AnnotationSpec转换类，用来将Table转换为ColumnUpdate Class的Annotations
     */
    Translator<Table, List<AnnotationInstanceSpec>> getColumnUpdateClassAnnotationsTranslator();

    /**
     * Name转换类：用来将Table转换为ColumnUpdate Class的name
     */
    Translator<Table, String> getColumnUpdateClassNameTranslator();

    /**
     * Customizer类：用来给ColumnUpdate Class提供额外的属性或方法。
     */
    List<BiConsumer<ClassSpec.Builder, Table>> getColumnUpdateCustomizers();

    /**
     * Name转换类：用来将Table转换为Support Class的name
     */
    Translator<Table, String> getSupportClassNameTranslator();

    /**
     * Name转换类：用来将Table转换为field table的name，Support类用。
     */
    Translator<Table, String> getFieldTableNameTranslator();

    /**
     * Name转换类：用来将Table转换为table的name, Support类用来表示真实的物理表名。
     */
    Translator<Table, String> getTableNameTranslator();

    /**
     * Name转换类：用来将Column转换为field column的name，Support类用。
     */
    Translator<Column, String> getFieldColumnNameTranslator();

    /**
     * Name转换类：用来将Column转换为column的name, Support类用来表示真实的物理字段名。
     */
    Translator<Column, String> getColumnNameTranslator();

    /**
     * Name转换类：用来将Column转换为Mybatis认可的JdbcType name
     */
    Translator<Column, String> getJdbcTypeNameTranslator();

    /**
     * Name转换类：用来将Table转换为SqlProvider Class的name
     */
    Translator<Table, String> getSqlProviderClassNameTranslator();

    /**
     * Name转换类：用来将Table转换为Mapper Class的name
     */
    Translator<Table, String> getMapperClassNameTranslator();

    /**
     * Name转换类：用来将Column转换为Param Name
     */
    Translator<Column, String> getColumnParamNameTranslator();

    /**
     * ColumnFinder类：用来寻找维护记录版本号的Column
     */
    ColumnFinder getRecordVersionColumnFinder();

    /**
     * Name转换类：用来将Table转换为Repository Class的Name
     */
    Translator<Table, String> getRepositoryClassNameTranslator();

    /**
     * Name转换类：用来将Table转换为Abstract Repository Class的Name
     */
    Translator<Table, String> getAbstractRepositoryClassNameTranslator();

    /**
     * Name转换类：用来将Table转换为RepositoryImpl Class的Name
     */
    Translator<Table, String> getRepositoryImplClassNameTranslator();

    /**
     * Name转换类：用来将Index转换为Index的Name
     */
    Translator<Index, String> getIndexNameTranslator();

    /**
     * Name转换类：用来将Table转换为Primary Key的Name
     */
    Translator<Table, String> getPrimaryKeyNameTranslator();

    /**
     * suffix转换类：用来将Table转换为额外的表定义属性
     */
    Translator<Table, List<String>> getTableSuffixesTranslator();

    /**
     * DataTypeSpec转换类：用来将Column转换为DataTypeSpec
     */
    Translator<Column, DataTypeSpec> getDataTypeSpecTranslator();

    /**
     * Comment转换类：用来将Table转换为Table Comment
     */
    Translator<Table, Optional<String>> getTableCommentTranslator();

    /**
     * Comment转换类：用来将Column转换为Column Comment
     */
    Translator<Column, Optional<String>> getColumnCommentTranslator();

    /**
     * 用来过滤掉不需要的table
     */
    ModelFilter<Table> getTableFilter();

    /**
     * 获取生产TableToSupport的Factory
     */
    TableToSupportFactory getTableToSupportFactory();
}
