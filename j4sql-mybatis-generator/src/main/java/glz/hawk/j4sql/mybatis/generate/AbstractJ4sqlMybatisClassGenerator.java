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

import glz.hawk.codepoet.ddl.DatabaseSpec;
import glz.hawk.codepoet.ddl.SqlFile;
import glz.hawk.codepoet.ddl.dialect.Dialect;
import glz.hawk.codepoet.java.FieldSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.PrimitiveTypeName;
import glz.hawk.j4sql.mybatis.translator.*;
import glz.hawk.j4sql.support.SqlColumn;
import glz.hawk.jdesigner.builder.context.PackageGenerator;
import glz.hawk.jdesigner.generator.GeneratorUtils;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.spec.manager.ModelWarehouse;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.jdesigner.translator.database.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractJ4sqlMybatisClassGenerator {
    private final PackageGenerator poClassPackageGenerator;

    private final PackageGenerator updateClassPackageGenerator;

    private final PackageGenerator supportClassPackageGenerator;

    private final PackageGenerator mapperClassPackageGenerator;

    private final PackageGenerator sqlProviderClassPackageGenerator;

    private final PackageGenerator repositoryClassPackageGenerator;

    private final PackageGenerator abstractRepositoryClassPackageGenerator;

    private final PackageGenerator repositoryImplClassPackageGenerator;

    private final ModelWarehouse modelWarehouse;

    private final String[] includePackages;

    private final String[] excludePackages;

    private final String ddlPackageName;

    private final String dialectName;

    private final J4sqlMybatisGeneratorCustomizer customizer;

    private final boolean supportColumnInsertOrUpdate;

    private final String tableEnumClassPackage;

    private final String tableEnumClassName;

    //
    private final Translator<Column, FieldSpec> fieldTranslator;

    private final Translator<Column, MethodSpec> getterTranslator;

    private final Translator<Column, MethodSpec> setterTranslator;

    private final Translator<Column, FieldSpec> fieldUpdatedTranslator;

    private final Translator<Column, MethodSpec> getterUpdatedTranslator;

    private final Translator<Column, MethodSpec> setterUpdatedTranslator;

    private final Translator<Column, FieldSpec> fieldColumnTranslator;

    private final Translator<Column, MethodSpec> columnGetterTranslator;

    private final Translator<Column, MethodSpec> sqlFieldUpdatedSetterTranslator;

    private final Translator<Column, MethodSpec> sqlColumnUpdatedSetterTranslator;

    AbstractJ4sqlMybatisClassGenerator(ModelWarehouse modelWarehouse,
                                       String[] includePackages,
                                       String[] excludePackages,
                                       PackageGenerator poClassPackageGenerator,
                                       PackageGenerator updateClassPackageGenerator,
                                       PackageGenerator supportClassPackageGenerator,
                                       PackageGenerator mapperClassPackageGenerator,
                                       PackageGenerator sqlProviderClassPackageGenerator,
                                       PackageGenerator repositoryClassPackageGenerator,
                                       PackageGenerator abstractRepositoryClassPackageGenerator,
                                       PackageGenerator repositoryImplClassPackageGenerator,
                                       String ddlPackageName, String dialectName,
                                       J4sqlMybatisGeneratorCustomizer customizer,
                                       boolean supportColumnInsertOrUpdate,
                                       String tableEnumClassPackage,
                                       String tableEnumClassName) {
        this.modelWarehouse = argNotNull(modelWarehouse, "modelWarehouse");
        this.includePackages = includePackages;
        this.excludePackages = excludePackages;
        this.poClassPackageGenerator = argNotNull(poClassPackageGenerator, "poClassPackageGenerator");
        this.updateClassPackageGenerator = argNotNull(updateClassPackageGenerator, "updateClassPackageGenerator");
        this.supportClassPackageGenerator = argNotNull(supportClassPackageGenerator, "supportClassPackageGenerator");
        this.mapperClassPackageGenerator = argNotNull(mapperClassPackageGenerator, "mapperClassPackageGenerator");
        this.sqlProviderClassPackageGenerator = argNotNull(sqlProviderClassPackageGenerator, "sqlProviderClassPackageGenerator");
        this.repositoryClassPackageGenerator = argNotNull(repositoryClassPackageGenerator, "repositoryClassPackageGenerator");
        this.abstractRepositoryClassPackageGenerator = argNotNull(abstractRepositoryClassPackageGenerator, "abstractRepositoryClassPackageGenerator");
        this.repositoryImplClassPackageGenerator = argNotNull(repositoryImplClassPackageGenerator, "repositoryImplClassPackageGenerator");
        this.ddlPackageName = argNotBlank(ddlPackageName, "ddlPackageName");
        this.dialectName = argNotBlank(dialectName, "dialectName");
        this.customizer = argNotNull(customizer, "customizer");
        this.supportColumnInsertOrUpdate = supportColumnInsertOrUpdate;
        this.tableEnumClassPackage = argNotBlank(tableEnumClassPackage, "tableEnumClassPackage");
        this.tableEnumClassName = argNotBlank(tableEnumClassName, "tableEnumClassName");
        //
        this.fieldTranslator = new ColumnToField(customizer.getFieldNameTranslator(), customizer.getFieldTypeNameTranslator(), customizer.getFieldJavadocTranslator(), customizer.getFieldAnnotationsTranslator());
        this.getterTranslator = new ColumnToGetter(customizer.getFieldNameTranslator(), customizer.getFieldTypeNameTranslator(), customizer.getGetterNameTranslator(), customizer.getGetterJavadocTranslator());
        this.setterTranslator = new ColumnToSetter(customizer.getFieldNameTranslator(), customizer.getFieldTypeNameTranslator(), customizer.getSetterNameTranslator(), customizer.getSetterJavadocTranslator());
        this.fieldUpdatedTranslator = new ColumnToField(customizer.getFieldUpdatedNameTranslator(), column -> PrimitiveTypeName.BOOLEAN, customizer.getFieldJavadocTranslator(), column -> Collections.emptyList());
        this.getterUpdatedTranslator = new ColumnToGetter(customizer.getFieldUpdatedNameTranslator(), column -> PrimitiveTypeName.BOOLEAN, customizer.getGetterUpdatedNameTranslator(), customizer.getGetterJavadocTranslator());
        this.setterUpdatedTranslator = new ColumnToUpdatedSetter(customizer.getFieldNameTranslator(), customizer.getFieldTypeNameTranslator(), customizer.getFieldUpdatedNameTranslator(), customizer.getSetterNameTranslator(), customizer.getSetterJavadocTranslator());
        this.fieldColumnTranslator = new ColumnToField(customizer.getFieldNameTranslator(), c -> ClassName.ofClass(SqlColumn.class), customizer.getFieldJavadocTranslator(), customizer.getFieldAnnotationsTranslator());
        this.columnGetterTranslator = new ColumnToGetter(customizer.getFieldNameTranslator(), c -> ClassName.ofClass(SqlColumn.class), customizer.getGetterNameTranslator(), customizer.getGetterJavadocTranslator());
        this.sqlFieldUpdatedSetterTranslator = new ColumnToSqlFieldUpdatedSetter(customizer.getSetterNameTranslator(), customizer.getFieldNameTranslator(), customizer.getFieldTypeNameTranslator(), customizer.getFieldUpdatedNameTranslator(), customizer.getSetterJavadocTranslator());
        this.sqlColumnUpdatedSetterTranslator = new ColumnToUpdatedSetter(customizer.getFieldNameTranslator(), c -> ClassName.ofClass(SqlColumn.class), customizer.getFieldUpdatedNameTranslator(), customizer.getSetterNameTranslator(), customizer.getSetterJavadocTranslator());
    }

    protected boolean pass(Table table) {
        return GeneratorUtils.pass(table, includePackages, excludePackages) && customizer.getTableFilter().pass(table);
    }

    public void generatePo() {
        Translator<Table, JavaFile> tableToPoTranslator = new TableToPojo(poClassPackageGenerator::loadPackage, customizer.getPoClassNameTranslator(), Collections.singletonList(fieldTranslator), Arrays.asList(getterTranslator, setterTranslator), customizer.getPoJavadocTranslator(),customizer.getPoClassAnnotationsTranslator(), customizer.getPoCustomizers());
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToPoTranslator::translate).forEach(this::write);
    }

    public void generateUpdate() {
        Translator<Table, JavaFile> tableToUpdateTranslator = new TableToPojo(updateClassPackageGenerator::loadPackage, customizer.getUpdateClassNameTranslator(), Arrays.asList(fieldTranslator, fieldUpdatedTranslator), Arrays.asList(getterTranslator, getterUpdatedTranslator, setterUpdatedTranslator), customizer.getPoJavadocTranslator(),customizer.getUpdateClassAnnotationsTranslator(), customizer.getUpdateCustomizers());
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToUpdateTranslator::translate).forEach(this::write);

        if (supportColumnInsertOrUpdate) {
            generateColumnUpdate();
        }
    }

    private void generateColumnUpdate() {
        Translator<Table, JavaFile> tableToColumnUpdateTranslator = new TableToPojo(updateClassPackageGenerator::loadPackage, customizer.getColumnUpdateClassNameTranslator(), Arrays.asList(fieldColumnTranslator, fieldUpdatedTranslator), Arrays.asList(columnGetterTranslator, getterUpdatedTranslator, sqlFieldUpdatedSetterTranslator, sqlColumnUpdatedSetterTranslator), customizer.getPoJavadocTranslator(),customizer.getColumnUpdateClassAnnotationsTranslator(), customizer.getColumnUpdateCustomizers());
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToColumnUpdateTranslator::translate).forEach(this::write);
    }

    public void generateSupport() {
        List<Table> tables = modelWarehouse.listModels(Table.class);
        if (tables.isEmpty()) return;
        Translator<Table, JavaFile> tableToSupportTranslator = customizer.getTableToSupportFactory().generate(supportClassPackageGenerator::loadPackage, customizer.getSupportClassNameTranslator(), poClassPackageGenerator::loadPackage, customizer.getPoClassNameTranslator(), updateClassPackageGenerator::loadPackage, customizer.getUpdateClassNameTranslator(), customizer.getColumnUpdateClassNameTranslator(), customizer.getFieldTableNameTranslator(), customizer.getTableNameTranslator(), customizer.getFieldColumnNameTranslator(), customizer.getColumnNameTranslator(), supportColumnInsertOrUpdate, customizer.getFieldNameTranslator(), customizer.getJdbcTypeNameTranslator());
        tables.stream().filter(this::pass).map(tableToSupportTranslator::translate).forEach(this::write);
    }

    public void generateTableEnum() {
        List<Table> tables = modelWarehouse.listModels(Table.class);
        if (tables.isEmpty()) return;
        Translator<List<Table>, JavaFile> tableToTableEnumTranslator = new TablesToTableEnum(supportClassPackageGenerator::loadPackage, customizer.getSupportClassNameTranslator(), customizer.getFieldTableNameTranslator(), tableEnumClassPackage, tableEnumClassName);
        write(tableToTableEnumTranslator.translate(tables));
    }

    public void generateSqlProvider() {
        TableToSqlProvider tableToSqlProviderTranslator = new TableToSqlProvider(sqlProviderClassPackageGenerator::loadPackage, customizer.getSqlProviderClassNameTranslator(), supportClassPackageGenerator::loadPackage, customizer.getSupportClassNameTranslator(), poClassPackageGenerator::loadPackage, customizer.getPoClassNameTranslator(), customizer.getColumnParamNameTranslator(), customizer.getFieldTypeNameTranslator(), updateClassPackageGenerator::loadPackage, customizer.getUpdateClassNameTranslator(), customizer.getColumnUpdateClassNameTranslator(), customizer.getFieldTableNameTranslator(), customizer.getTableNameTranslator(), customizer.getFieldColumnNameTranslator(), customizer.getColumnNameTranslator(), customizer.getGetterNameTranslator(), customizer.getGetterUpdatedNameTranslator(), customizer.getRecordVersionColumnFinder(), supportColumnInsertOrUpdate);
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToSqlProviderTranslator::translate).forEach(this::write);
    }

    public void generateMapper() {
        Translator<Table, JavaFile> tableToMapperTranslator = new TableToMapper(mapperClassPackageGenerator::loadPackage, customizer.getMapperClassNameTranslator(), poClassPackageGenerator::loadPackage, customizer.getPoClassNameTranslator());
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToMapperTranslator::translate).forEach(this::write);
    }

    public void generateRepository() {
        Translator<Table, JavaFile> tableToRepositoryTranslator = new TableToRepository(repositoryClassPackageGenerator::loadPackage, customizer.getRepositoryClassNameTranslator(), poClassPackageGenerator::loadPackage, customizer.getPoClassNameTranslator(), customizer.getColumnParamNameTranslator(), customizer.getFieldTypeNameTranslator(), updateClassPackageGenerator::loadPackage, customizer.getUpdateClassNameTranslator(), customizer.getColumnUpdateClassNameTranslator(), customizer.getRecordVersionColumnFinder(), supportColumnInsertOrUpdate);
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToRepositoryTranslator::translate).forEach(this::write);
    }

    public void generateAbstractRepository() {
        Translator<Table, JavaFile> tableToAbstractRepositoryTranslator = new TableToAbstractRepository(
            repositoryClassPackageGenerator::loadPackage,
            customizer.getRepositoryClassNameTranslator(),
            abstractRepositoryClassPackageGenerator::loadPackage,
            customizer.getAbstractRepositoryClassNameTranslator(),
            mapperClassPackageGenerator::loadPackage,
            customizer.getMapperClassNameTranslator(),
            sqlProviderClassPackageGenerator::loadPackage,
            customizer.getSqlProviderClassNameTranslator(),
            poClassPackageGenerator::loadPackage,
            customizer.getPoClassNameTranslator(),
            customizer.getColumnParamNameTranslator(),
            customizer.getFieldTypeNameTranslator(),
            updateClassPackageGenerator::loadPackage,
            customizer.getUpdateClassNameTranslator(),
            customizer.getColumnUpdateClassNameTranslator(),
            customizer.getRecordVersionColumnFinder(),
            supportColumnInsertOrUpdate
        );
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToAbstractRepositoryTranslator::translate).forEach(this::write);
    }

    public void generateRepositoryImpl() {
        Translator<Table, JavaFile> tableToRepositoryTranslator = new TableToRepositoryImpl(
            repositoryClassPackageGenerator::loadPackage,
            customizer.getRepositoryClassNameTranslator(),
            abstractRepositoryClassPackageGenerator::loadPackage,
            customizer.getAbstractRepositoryClassNameTranslator(),
            mapperClassPackageGenerator::loadPackage,
            customizer.getMapperClassNameTranslator(),
            sqlProviderClassPackageGenerator::loadPackage,
            customizer.getSqlProviderClassNameTranslator(),
            poClassPackageGenerator::loadPackage,
            customizer.getPoClassNameTranslator(),
            customizer.getColumnParamNameTranslator(),
            customizer.getFieldTypeNameTranslator(),
            updateClassPackageGenerator::loadPackage,
            customizer.getUpdateClassNameTranslator(),
            customizer.getColumnUpdateClassNameTranslator(),
            repositoryImplClassPackageGenerator::loadPackage,
            customizer.getRepositoryImplClassNameTranslator(),
            customizer.getRecordVersionColumnFinder(),
            supportColumnInsertOrUpdate)
            ;
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().filter(this::pass).map(tableToRepositoryTranslator::translate).forEach(this::write);
    }

    public void generateDDL() {
        Translator<List<Table>, DatabaseSpec> databaseSpecTranslator = new TablesToDatabase(new TableToTable(customizer.getTableNameTranslator(), customizer.getColumnNameTranslator(), customizer.getDataTypeSpecTranslator(), customizer.getIndexNameTranslator(), customizer.getPrimaryKeyNameTranslator(), customizer.getTableCommentTranslator(), customizer.getColumnCommentTranslator(), customizer.getTableSuffixesTranslator()));
        List<Table> tables = modelWarehouse.listModels(Table.class).stream().filter(this::pass).collect(Collectors.toList());
        DatabaseSpec databaseSpec = databaseSpecTranslator.translate(tables);
        Dialect dialect = Dialect.parse(dialectName);
        SqlFile sqlFile = SqlFile.builder(databaseSpec, dialect).setPackage(ddlPackageName).setFilename(filenameOf(dialect)).build();
        write(sqlFile);
    }

    protected abstract void write(SqlFile sqlFile);

    protected String filenameOf(Dialect dialect) {
        return String.format("create_table_%s.sql", dialect.name().toLowerCase());
    }

    protected abstract void write(JavaFile javaFile);


}
