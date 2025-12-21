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

package glz.hawk.j4sql.mybatis.processor;

import glz.hawk.jdesigner.builder.context.PackageGenerator;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.j4sql.mybatis.translator.*;
import glz.hawk.jdesigner.spec.manager.ModelWarehouse;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.jdesigner.translator.database.*;
import glz.hawkframework.dao.function.ColumnFunction;
import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawk.j4sql.support.SqlColumn;
import glz.hawk.codepoet.ddl.DatabaseSpec;
import glz.hawk.codepoet.ddl.SqlFile;
import glz.hawk.codepoet.ddl.dialect.Dialect;
import glz.hawk.codepoet.java.ClassSpec;
import glz.hawk.codepoet.java.FieldSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.PrimitiveTypeName;
import glz.hawk.codepoet.java.type.TypeName;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
class J4sqlMybatisClassGenerator {

    private final Map<Class<? extends Annotation>, Class<? extends ColumnFunction>> columnFunctionMap = new HashMap<>();
    private final Translator<Column, String> fieldNameTranslator;
    private final Translator<Column, String> getterNameTranslator;
    private final Translator<Column, String> setterNameTranslator;
    private final Translator<Column, FieldSpec> fieldTranslator;
    private final Translator<Column, TypeName> columnToTypeNameTranslator;
    private final Translator<Column, MethodSpec> getterTranslator;
    private final Translator<Column, MethodSpec> setterTranslator;
    private final Translator<Column, String> getterUpdatedNameTranslator;
    private final Translator<Column, FieldSpec> fieldUpdatedTranslator;
    private final Translator<Column, String> fieldUpdatedNameTranslator;
    private final Translator<Column, MethodSpec> getterUpdatedTranslator;
    private final Translator<Column, MethodSpec> setterUpdatedTranslator;
    private final Translator<Column, FieldSpec> columnFieldTranslator;
    private final Translator<Column, MethodSpec> columnGetterTranslator;
    private final Translator<Column, MethodSpec> sqlFieldUpdatedSetterTranslator;
    private final Translator<Column, MethodSpec> sqlColumnUpdatedSetterTranslator;
    private final Translator<Table, String> sqlProviderClassNameTranslator;
    private final Translator<Column, String> columnParamNameTranslator;
    private final Translator<Table, String> repositoryClassNameTranslator;
    private final Translator<Table, String> repositoryImplClassNameTranslator;

    private final PackageGenerator poClassPackageGenerator;
    private final Translator<Table, String> poClassNameTranslator;
    private final PackageGenerator updateClassPackageGenerator;
    private final Translator<Table, String> updateClassNameTranslator;
    private final Translator<Table, String> columnUpdateClassNameTranslator;
    private final PackageGenerator supportClassPackageGenerator;
    private final Translator<Table, String> supportClassNameTranslator;
    private final PackageGenerator mapperClassPackageGenerator;
    private final Translator<Table, String> mapperClassNameTranslator;
    private final PackageGenerator sqlProviderClassPackageGenerator;
    private final PackageGenerator repositoryClassPackageGenerator;
    private final PackageGenerator repositoryImplClassPackageGenerator;
    private final Filer filer;
    private final ModelWarehouse modelWarehouse;
    private final List<Element> originatingElements;
    private final String ddlPackageName;
    private final String[] dialectNames;

    J4sqlMybatisClassGenerator(ModelWarehouse modelWarehouse,
                               Supplier<Map<Class<? extends Annotation>, Class<? extends ColumnFunction>>> columnFunctionMapSupplier,
                               Filer filer,
                               List<Element> originatingElements,
                               Translator<Column, String> fieldNameTranslator, Translator<Column, String> getterNameTranslator, Translator<Column, String> setterNameTranslator,
                               Translator<Column, TypeName> columnToTypeNameTranslator, PackageGenerator poClassPackageGenerator, Translator<Table, String> poClassNameTranslator,
                               Translator<Column, String> getterUpdatedNameTranslator,
                               Translator<Column, String> fieldUpdatedNameTranslator,
                               PackageGenerator updateClassPackageGenerator, Translator<Table, String> updateClassNameTranslator, Translator<Table, String> columnUpdateClassNameTranslator,
                               PackageGenerator supportClassPackageGenerator, Translator<Table, String> supportClassNameTranslator,
                               PackageGenerator mapperClassPackageGenerator, Translator<Table, String> mapperClassNameTranslator,
                               PackageGenerator sqlProviderClassPackageGenerator, Translator<Table, String> sqlProviderClassNameTranslator,
                               Translator<Column, String> columnParamNameTranslator,
                               PackageGenerator repositoryClassPackageGenerator,
                               Translator<Table, String> repositoryClassNameTranslator,
                               PackageGenerator repositoryImplClassPackageGenerator,
                               Translator<Table, String> repositoryImplClassNameTranslator,
                               String ddlPackageName, String[] dialectNames) {
        this.filer = argNotNull(filer, "filer");
        this.originatingElements = originatingElements;
        this.columnFunctionMap.putAll(argNotNull(columnFunctionMapSupplier, "columnFunctionMapSupplier").get());
        this.modelWarehouse = argNotNull(modelWarehouse, "modelWarehouse");
        this.fieldNameTranslator = argNotNull(fieldNameTranslator, "fieldNameTranslator");
        this.getterNameTranslator = argNotNull(getterNameTranslator, "getterNameTranslator");
        this.setterNameTranslator = argNotNull(setterNameTranslator, "setterNameTranslator");

        this.columnToTypeNameTranslator = argNotNull(columnToTypeNameTranslator, "columnToTypeNameTranslator");

        this.poClassPackageGenerator = argNotNull(poClassPackageGenerator, "poClassPackageGenerator");
        this.poClassNameTranslator = argNotNull(poClassNameTranslator, "poClassNameTranslator");

        this.getterUpdatedNameTranslator = argNotNull(getterUpdatedNameTranslator, "getterUpdatedNameTranslator");
        this.fieldUpdatedNameTranslator = argNotNull(fieldUpdatedNameTranslator, "fieldUpdatedNameTranslator");

        this.fieldTranslator = new ColumnToField(fieldNameTranslator, columnToTypeNameTranslator);
        this.getterTranslator = new ColumnToGetter(getterNameTranslator, fieldNameTranslator, columnToTypeNameTranslator);
        this.setterTranslator = new ColumnToSetter(setterNameTranslator, fieldNameTranslator, columnToTypeNameTranslator);

        this.fieldUpdatedTranslator = new ColumnToFieldUpdated(fieldUpdatedNameTranslator, d -> PrimitiveTypeName.BOOLEAN);
        this.getterUpdatedTranslator = new ColumnToGetter(getterUpdatedNameTranslator, fieldUpdatedNameTranslator, d -> PrimitiveTypeName.BOOLEAN);

        this.setterUpdatedTranslator = new ColumnToUpdatedSetter(setterNameTranslator, fieldNameTranslator, columnToTypeNameTranslator, fieldUpdatedNameTranslator);

        this.updateClassPackageGenerator = argNotNull(updateClassPackageGenerator, "updateClassPackageGenerator");
        this.updateClassNameTranslator = argNotNull(updateClassNameTranslator, "updateClassNameTranslator");
        this.columnUpdateClassNameTranslator = argNotNull(columnUpdateClassNameTranslator, "columnUpdateClassNameTranslator");
        this.columnFieldTranslator = new ColumnToField(fieldNameTranslator, c -> ClassName.ofClass(SqlColumn.class));

        this.columnGetterTranslator = new ColumnToGetter(getterNameTranslator, fieldNameTranslator, c -> ClassName.ofClass(SqlColumn.class));
        this.sqlFieldUpdatedSetterTranslator = new ColumnToSqlFieldUpdatedSetter(setterNameTranslator, fieldNameTranslator, columnToTypeNameTranslator, fieldUpdatedNameTranslator);
        this.sqlColumnUpdatedSetterTranslator = new ColumnToUpdatedSetter(setterNameTranslator, fieldNameTranslator, c -> ClassName.ofClass(SqlColumn.class), fieldUpdatedNameTranslator);

        this.supportClassPackageGenerator = argNotNull(supportClassPackageGenerator, "supportClassPackageGenerator");
        this.supportClassNameTranslator = argNotNull(supportClassNameTranslator, "supportClassNameTranslator");

        this.mapperClassPackageGenerator = argNotNull(mapperClassPackageGenerator, "mapperClassPackageGenerator");
        this.mapperClassNameTranslator = argNotNull(mapperClassNameTranslator, "mapperClassNameTranslator");

        this.sqlProviderClassPackageGenerator = argNotNull(sqlProviderClassPackageGenerator, "sqlProviderClassPackageGenerator");
        this.sqlProviderClassNameTranslator = argNotNull(sqlProviderClassNameTranslator, "sqlProviderClassNameTranslator");

        this.columnParamNameTranslator = argNotNull(columnParamNameTranslator, "columnParamNameTranslator");
        this.repositoryClassPackageGenerator = argNotNull(repositoryClassPackageGenerator, "repositoryClassPackageGenerator");
        this.repositoryClassNameTranslator = argNotNull(repositoryClassNameTranslator, "repositoryClassNameTranslator");
        this.repositoryImplClassPackageGenerator = argNotNull(repositoryImplClassPackageGenerator, "repositoryImplClassPackageGenerator");
        this.repositoryImplClassNameTranslator = argNotNull(repositoryImplClassNameTranslator, "repositoryImplClassNameTranslator");

        this.ddlPackageName = argNotBlank(ddlPackageName, "ddlPackageName");

        this.dialectNames = dialectNames == null ? new String[0] : dialectNames;
    }

    void generatePo() {
        List<BiConsumer<ClassSpec.Builder, Table>> customizers = new ArrayList<>();
        customizers.add(new PersistObjectCustomizer(fieldNameTranslator, getterNameTranslator, setterNameTranslator, null, columnFunctionMap));
        Translator<Table, JavaFile> tableToPoTranslator = new TableToPojo(
            poClassNameTranslator,
            Collections.singletonList(fieldTranslator),
            Arrays.asList(getterTranslator, setterTranslator),
            poClassPackageGenerator::loadPackage,
            customizers);
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToPoTranslator::translate).forEach(this::write);
    }

    private void write(JavaFile j) {
        j.toBuilder().addOriginatingElements(this.originatingElements).build();
        j.writeTo(filer);
    }

    void generateUpdate() {
        List<BiConsumer<ClassSpec.Builder, Table>> customizers = new ArrayList<>();
        customizers.add(new PersistObjectCustomizer(fieldNameTranslator, getterNameTranslator, setterNameTranslator, getterUpdatedNameTranslator, columnFunctionMap));
        Translator<Table, JavaFile> tableToUpdateTranslator = new TableToPojo(
            updateClassNameTranslator,
            Arrays.asList(fieldTranslator, fieldUpdatedTranslator),
            Arrays.asList(getterTranslator, getterUpdatedTranslator, setterUpdatedTranslator),
            updateClassPackageGenerator::loadPackage,
            customizers);

        Translator<Table, JavaFile> tableToColumnUpdateTranslator = new TableToPojo(
            columnUpdateClassNameTranslator,
            Arrays.asList(columnFieldTranslator, fieldUpdatedTranslator),
            Arrays.asList(columnGetterTranslator, getterUpdatedTranslator, sqlFieldUpdatedSetterTranslator, sqlColumnUpdatedSetterTranslator),
            updateClassPackageGenerator::loadPackage,
            customizers);
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToUpdateTranslator::translate).forEach(this::write);
        tables.stream().map(tableToColumnUpdateTranslator::translate).forEach(this::write);
    }

    void generateSupport() {
        Translator<Table, JavaFile> tableToSupportTranslator = new TableToSupport(
            supportClassPackageGenerator::loadPackage,
            supportClassNameTranslator,
            poClassPackageGenerator::loadPackage,
            poClassNameTranslator,
            fieldNameTranslator,
            getterNameTranslator,
            updateClassPackageGenerator::loadPackage,
            updateClassNameTranslator,
            columnUpdateClassNameTranslator,
            getterUpdatedNameTranslator

        );
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToSupportTranslator::translate).forEach(this::write);
    }

    void generateMapper() {
        Translator<Table, JavaFile> tableToMapperTranslator = new TableToMapper(
            mapperClassPackageGenerator::loadPackage,
            mapperClassNameTranslator,
            poClassPackageGenerator::loadPackage,
            poClassNameTranslator
        );
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToMapperTranslator::translate).forEach(this::write);
    }

    void generateSqlProvider() {
        TableToSqlProvider tableToSqlProviderTranslator = new TableToSqlProvider(
            sqlProviderClassPackageGenerator::loadPackage,
            sqlProviderClassNameTranslator,
            supportClassPackageGenerator::loadPackage,
            supportClassNameTranslator,
            poClassPackageGenerator::loadPackage,
            poClassNameTranslator,
            columnParamNameTranslator,
            columnToTypeNameTranslator,
            updateClassPackageGenerator::loadPackage,
            updateClassNameTranslator,
            columnUpdateClassNameTranslator,
            getterNameTranslator,
            getterUpdatedNameTranslator

        );
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToSqlProviderTranslator::translate).forEach(this::write);
    }

    void generateRepository() {
        Translator<Table, JavaFile> tableToRepositoryTranslator = new TableToRepository(
            repositoryClassPackageGenerator::loadPackage,
            repositoryClassNameTranslator,
            poClassPackageGenerator::loadPackage,
            poClassNameTranslator,
            columnParamNameTranslator,
            columnToTypeNameTranslator,
            updateClassPackageGenerator::loadPackage,
            updateClassNameTranslator,
            columnUpdateClassNameTranslator
        );
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToRepositoryTranslator::translate).forEach(this::write);
    }

    void generateRepositoryImpl() {
        Translator<Table, JavaFile> tableToRepositoryTranslator = new TableToRepositoryImpl(
            repositoryClassPackageGenerator::loadPackage,
            repositoryClassNameTranslator,
            poClassPackageGenerator::loadPackage,
            poClassNameTranslator,
            columnParamNameTranslator,
            columnToTypeNameTranslator,
            updateClassPackageGenerator::loadPackage,
            updateClassNameTranslator,
            columnUpdateClassNameTranslator,
            repositoryImplClassPackageGenerator::loadPackage,
            repositoryImplClassNameTranslator,
            mapperClassPackageGenerator::loadPackage,
            mapperClassNameTranslator,
            sqlProviderClassPackageGenerator::loadPackage,
            sqlProviderClassNameTranslator
        );
        List<Table> tables = modelWarehouse.listModels(Table.class);
        tables.stream().map(tableToRepositoryTranslator::translate).forEach(this::write);
    }

    void generateDDL()  {
        if (ObjectHelper.isEmpty(dialectNames)){
                  return;
        }
        Translator<List<Table>, DatabaseSpec> databaseSpecTranslator = new TablesToDatabaseSpec();
        List<Table> tables = modelWarehouse.listModels(Table.class);
        DatabaseSpec databaseSpec = databaseSpecTranslator.translate(tables);

        for (String dialectName : dialectNames) {
            Dialect dialect = Dialect.parse(dialectName);
            SqlFile sqlFile = SqlFile.builder(databaseSpec, Dialect.MYSQL)
                .setPackage(ddlPackageName)
                .setFilename(filenameOf(dialect))
                .addOriginatingElements(this.originatingElements).build();

            sqlFile.writeTo(filer);
        }
    }

    private String filenameOf(Dialect dialect) {
        return String.format("create_table_%s.sql", dialect.name().toLowerCase());
    }
}
