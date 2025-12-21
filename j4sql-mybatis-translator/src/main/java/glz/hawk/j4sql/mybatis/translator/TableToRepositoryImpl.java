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
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.IndexColumn;
import glz.hawk.jdesigner.spec.database.PrimaryKey;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.jdesigner.translator.database.TableHelper;
import glz.hawkframework.dao.context.DefaultRepositoryContext;
import glz.hawkframework.dao.context.ExceptionConverter;
import glz.hawkframework.dao.context.SqlMethod;
import glz.hawkframework.dao.process.InsertProcessor;
import glz.hawkframework.dao.process.UpdateProcessor;
import glz.hawkframework.core.helper.MapHelper;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawk.j4sql.util.QueryWrapper;
import glz.hawkframework.core.support.ArgumentSupport;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import glz.hawk.codepoet.java.type.TypeName;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToRepositoryImpl extends AbstractTableToRepository implements Translator<Table, JavaFile> {

    protected final Translator<Table, String> repositoryImplClassPackageTranslator;
    protected final Translator<Table, String> repositoryImplClassNameTranslator;
    protected final Translator<Table, String> mapperClassPackageTranslator;
    protected final Translator<Table, String> mapperClassNameTranslator;
    protected final Translator<Table, String> sqlProviderClassPackageTranslator;
    protected final Translator<Table, String> sqlProviderClassNameTranslator;

    public TableToRepositoryImpl(Translator<Table, String> repositoryClassPackageTranslator, Translator<Table, String> repositoryClassNameTranslator,
                                 Translator<Table, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator,
                                 Translator<Column, String> columnToParamNameTranslator, Translator<Column, TypeName> dataTypeToTypeNameTranslator,
                                 Translator<Table, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator, Translator<Table, String> columnUpdateClassNameTranslator,
                                 Translator<Table, String> repositoryImplClassPackageTranslator, Translator<Table, String> repositoryImplClassNameTranslator,
                                 Translator<Table, String> mapperClassPackageTranslator, Translator<Table, String> mapperClassNameTranslator,
                                 Translator<Table, String> sqlProviderClassPackageTranslator, Translator<Table, String> sqlProviderClassNameTranslator
    ) {
        super(repositoryClassPackageTranslator, repositoryClassNameTranslator, poClassPackageTranslator, poClassNameTranslator, columnToParamNameTranslator, dataTypeToTypeNameTranslator, updateClassPackageTranslator, updateClassNameTranslator, columnUpdateClassNameTranslator);
        this.repositoryImplClassPackageTranslator = argNotNull(repositoryImplClassPackageTranslator, "repositoryImplClassPackageTranslator");
        this.repositoryImplClassNameTranslator = argNotNull(repositoryImplClassNameTranslator, "repositoryImplClassNameTranslator");
        this.mapperClassPackageTranslator = argNotNull(mapperClassPackageTranslator, "mapperClassPackageTranslator");
        this.mapperClassNameTranslator = argNotNull(mapperClassNameTranslator, "mapperClassNameTranslator");
        this.sqlProviderClassPackageTranslator = argNotNull(sqlProviderClassPackageTranslator, "sqlProviderClassPackageTranslator");
        this.sqlProviderClassNameTranslator = argNotNull(sqlProviderClassNameTranslator, "sqlProviderClassNameTranslator");
    }

    @Nonnull
    @Override
    public JavaFile translate(@Nonnull Table table) {
        argNotNull(table, "table");
        String className = repositoryImplClassNameTranslator.translate(table);
        ClassSpec.Builder builder = ClassSpec.builder(className).addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Repository.class).build())
            .addSuperInterface(ClassName.of(repositoryClassPackageTranslator.translate(table), repositoryClassNameTranslator.translate(table)));

        // fields
        // mapper
        builder.addField(mapper(table));

        // sqlProvider
        builder.addField(sqlProvider(table));

        // insertProcessor
        builder.addField(insertProcessor());

        // updateProcessor
        builder.addField(updateProcessor());

        // exceptionConverter
        builder.addField(exceptionConverter());

        // constructors
        builder.addConstructor(constructor(table));

        // methods
        // insert
        builder.addMethod(insert(table));

        // insetSelective
        builder.addMethod(insertSelective(table));

        // insertMultiple
        builder.addMethod(insertMultiple(table));
        builder.addMethod(insertMultipleWithChunk(table));

        // deleteByPrimaryKey
        table.getPrimaryKey().map(this::deleteByPrimaryKey).ifPresent(builder::addMethod);

        // updateByPrimaryKey
        table.getPrimaryKey().map(this::updateByPrimaryKey).ifPresent(builder::addMethod);

        // getByPrimaryKey
        table.getPrimaryKey().map(this::getByPrimaryKey).ifPresent(builder::addMethod);

        // loadByPrimaryKey
        table.getPrimaryKey().map(this::loadByPrimaryKey).ifPresent(builder::addMethod);

        // loadByPrimaryKeyWithThrow
        table.getPrimaryKey().map(this::loadWithThrowByPrimaryKey).ifPresent(builder::addMethod);

        // existByPrimaryKey
        table.getPrimaryKey().map(this::existByPrimaryKey).ifPresent(builder::addMethod);

        // assertExistByPrimaryKey
        table.getPrimaryKey().map(this::assertExistByPrimaryKey).ifPresent(builder::addMethod);

        // assertExistWithThrowByPrimaryKey
        table.getPrimaryKey().map(this::assertExistWithThrowByPrimaryKey).ifPresent(builder::addMethod);

        // common
        builder.addMethod(queryOne(table));
        builder.addMethod(queryMany(table));
        builder.addMethod(cursor(table));
        builder.addMethod(loadOne(table));
        builder.addMethod(loadOneWithThrow(table));
        builder.addMethod(count(table));
        builder.addMethod(exist(table));
        builder.addMethod(assertExist(table));
        builder.addMethod(assertExistWithThrow(table));
        builder.addMethod(existOne(table));
        builder.addMethod(assertExistOne(table));
        builder.addMethod(assertExistOneWithThrow(table));
        builder.addMethod(delete(table));
        builder.addMethod(update(table));

        // before/after insert/update
        builder.addMethod(beforeInsert());
        builder.addMethod(afterInsert());
        builder.addMethod(beforeUpdate());
        builder.addMethod(afterUpdate());

        // static import
        builder.addStaticImport(ArgumentSupport.class, "*")
            .addStaticImport(DefaultRepositoryContext.class, "*")
            .addStaticImport(SqlMethod.class, "*")
            .addStaticImport(MapHelper.class, "*")
            .addStaticImport(UpdateProcessor.class, "*");

        return JavaFile.builder(repositoryImplClassPackageTranslator.translate(table), builder.build()).build();
    }

    protected String beforeInsertMethodName() {
        return "beforeInsert";
    }

    protected MethodSpec beforeInsert() {
        final String paramName = "obj";
        return MethodSpec.builder(VOID, beforeInsertMethodName(), Modifier.PROTECTED)
            .addParameter(Object.class, paramName)
            .beginMethodBody()
            .beginIf("$L != null", insertProcessorFieldName())
            .beginIf("$L instanceof $T", paramName, List.class)
            .addStatement("$L.beforeInsert(($T<?>) $L)", insertProcessorFieldName(), List.class, paramName)
            .beginElse()
            .addStatement("$L.beforeInsert($L)", insertProcessorFieldName(), paramName)
            .endIf()
            .endIf()
            .end()
            .build();
    }

    protected String afterInsertMethodName() {
        return "afterInsert";
    }

    protected MethodSpec afterInsert() {
        final String paramName = "obj";
        return MethodSpec.builder(VOID, afterInsertMethodName(), Modifier.PROTECTED)
            .addParameter(Object.class, paramName)
            .beginMethodBody()
            .beginIf("$L != null", insertProcessorFieldName())
            .beginIf("$L instanceof $T", paramName, List.class)
            .addStatement("$L.afterInsert(($T<?>) $L)", insertProcessorFieldName(), List.class, paramName)
            .beginElse()
            .addStatement("$L.afterInsert($L)", insertProcessorFieldName(), paramName)
            .endIf()
            .endIf()
            .end()
            .build();
    }

    protected String beforeUpdateMethodName() {
        return "beforeUpdate";
    }

    protected MethodSpec beforeUpdate() {
        final String paramName = "params";
        return MethodSpec.builder(VOID, beforeUpdateMethodName(), Modifier.PROTECTED)
            .addParameter(ParameterizedTypeName.of(Map.class, String.class, Object.class), paramName)
            .beginMethodBody()
            .beginIf("$L != null", updateProcessorFieldName())
            .addStatement("$L.beforeUpdate($L)", updateProcessorFieldName(), paramName)
            .endIf()
            .end()
            .build();
    }

    protected String afterUpdateMethodName() {
        return "afterUpdate";
    }

    protected MethodSpec afterUpdate() {
        final String paramName = "params";
        return MethodSpec.builder(VOID, afterUpdateMethodName(), Modifier.PROTECTED)
            .addParameter(ParameterizedTypeName.of(Map.class, String.class, Object.class), paramName)
            .beginMethodBody()
            .beginIf("$L != null", updateProcessorFieldName())
            .addStatement("$L.afterUpdate($L)", updateProcessorFieldName(), paramName)
            .endIf()
            .end()
            .build();
    }


    protected ClassName insertProcessorClassName() {
        return ClassName.ofClass(InsertProcessor.class);
    }

    protected String insertProcessorFieldName() {
        return "insertProcessor";
    }

    protected FieldSpec insertProcessor() {
        return FieldSpec.builder(insertProcessorClassName(), insertProcessorFieldName(), Modifier.PRIVATE)
            .addAnnotation(AnnotationInstanceSpec.builder(Autowired.class).addMember("required", false).build())
            .build();
    }

    protected ClassName updateProcessorClassName() {
        return ClassName.ofClass(UpdateProcessor.class);
    }

    protected String updateProcessorFieldName() {
        return "updateProcessor";
    }

    protected FieldSpec updateProcessor() {
        return FieldSpec.builder(updateProcessorClassName(), updateProcessorFieldName(), Modifier.PRIVATE)
            .addAnnotation(AnnotationInstanceSpec.builder(Autowired.class).addMember("required", false).build())
            .build();
    }

    protected String exceptionConverterFieldName() {
        return "exceptionConverter";
    }

    protected FieldSpec exceptionConverter() {
        return FieldSpec.builder(ExceptionConverter.class, exceptionConverterFieldName())
            .addAnnotation(Autowired.class)
            .build();
    }

    protected ClassName mapperClassName(Table table) {
        return ClassName.of(mapperClassPackageTranslator.translate(table), mapperClassNameTranslator.translate(table));
    }

    protected String mapperFieldName(Table table) {
        return StringHelper.unCapitalize(mapperClassNameTranslator.translate(table));
    }

    protected FieldSpec mapper(Table table) {
        return FieldSpec.builder(mapperClassName(table), mapperFieldName(table), Modifier.PRIVATE, Modifier.FINAL).build();
    }

    protected ClassName sqlProviderClassName(Table table) {
        return ClassName.of(sqlProviderClassPackageTranslator.translate(table), sqlProviderClassNameTranslator.translate(table));
    }

    protected String sqlProviderFieldName(Table table) {
        return StringHelper.unCapitalize(sqlProviderClassNameTranslator.translate(table));
    }

    protected FieldSpec sqlProvider(Table table) {
        return FieldSpec.builder(sqlProviderClassName(table), sqlProviderFieldName(table), Modifier.PRIVATE, Modifier.FINAL).build();
    }

    protected ConstructorSpec constructor(Table table) {
        String mapperParamName = mapperFieldName(table);
        String sqlProviderParamName = sqlProviderFieldName(table);
        return ConstructorSpec.builder(PUBLIC)
            .addParameter(ParameterSpec.builder(mapperClassName(table), mapperParamName).build())
            .addParameter(ParameterSpec.builder(sqlProviderClassName(table), sqlProviderParamName).build())
            .beginConstructorBody()
            .addStatement("this.$L = argNotNull($L, $S)", mapperFieldName(table), mapperParamName, mapperParamName)
            .addStatement("this.$L = argNotNull($L, $S)", sqlProviderFieldName(table), sqlProviderParamName, sqlProviderParamName)
            .end()
            .build();
    }

    protected MethodSpec insert(Table table) {
        String mapperFieldName = mapperFieldName(table);
        String poParamName = poParamName(table);
        return insertBuilder(table)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addStatement("$L(argNotNull($L, $S))", beforeInsertMethodName(), poParamName, poParamName)
            .beginTry()
            .addStatement("$L.insert($L.insert($L))", mapperFieldName, sqlProviderFieldName(table), poParamName)
            .beginCatch("$T ex", Exception.class)
            .addCode(b -> {
                b.addCode("throw $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, INSERT)", insertMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setInitCause(ex)").addNewLine();
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".addParam($S, $L)", poParamName, poParamName).addNewLine();
                b.addCode(".build());").addNewLine();
                b.removeIndent(4);
            })
            .endTry()
            .addStatement("$L($L)", afterInsertMethodName(), poParamName)
            .end()
            .build();
    }

    protected MethodSpec insertSelective(Table table) {
        String mapperFieldName = mapperFieldName(table);
        String poParamName = poParamName(table);
        return insertSelectiveBuilder(table)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addStatement("$L(argNotNull($L, $S))", beforeInsertMethodName(), poParamName, poParamName)
            .beginTry()
            .addStatement("$L.insertSelective($L.insertSelective($L))", mapperFieldName, sqlProviderFieldName(table), poParamName)
            .beginCatch("$T ex", Exception.class)
            .addCode(b -> {
                b.addCode("throw $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, INSERT)", insertSelectiveMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setInitCause(ex)").addNewLine();
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".addParam($S, $L)", poParamName, poParamName).addNewLine();
                b.addCode(".build());").addNewLine();
                b.removeIndent(4);
            })
            .endTry()
            .addStatement("$L($L)", afterInsertMethodName(), poParamName)
            .end()
            .build();
    }

    protected MethodSpec insertMultipleWithChunk(Table table) {
        String mapperFieldName = mapperFieldName(table);
        String posParamName = posParamName(table);
        String chunkSizeParamName = chunkSizeParamName();
        String chunkLocalParamName = "chunk";
        String sqlProviderFieldName = sqlProviderFieldName(table);
        return insertMultipleWithChunkBuilder(table)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addStatement("argNotEmpty($L, $S)", posParamName, posParamName)
            .addStatement("argument($L, c -> c > 0, c -> \"The parameter['$L'] must be greater than 0.\")", chunkSizeParamName, chunkSizeParamName)
            .beginFor("int i = 0; i < $L.size(); i += $L", posParamName, chunkSizeParamName)
            .addStatement("$T<$T> $L = $L.subList(i, Math.min(i + $L, $L.size()))", List.class, poClassName(table), chunkLocalParamName, posParamName, chunkSizeParamName, posParamName)
            .addStatement("$L($L)", beforeInsertMethodName(), chunkLocalParamName)
            .beginTry()
            .addStatement("$L.insertMultiple($L.insertMultiple($L))", mapperFieldName, sqlProviderFieldName, chunkLocalParamName)
            .beginCatch("$T ex", Exception.class)
            .addCode(b -> {
                b.addCode("throw $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, INSERT)", insertMultipleMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setInitCause(ex)").addNewLine();
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".addParam($S, $L)", posParamName, posParamName).addNewLine();
                b.addCode(".addParam($S, $L)", chunkSizeParamName, chunkSizeParamName).addNewLine();
                b.addCode(".build());").addNewLine();
                b.removeIndent(4);
            })
            .endTry()
            .addStatement("$L($L)", afterInsertMethodName(), chunkLocalParamName)
            .endFor()
            .end()
            .build();
    }

    protected MethodSpec insertMultiple(Table table) {
        String defaultChunkSizeLocalParamName = "defaultChunkSize";
        String posParamName = posParamName(table);
        return insertMultipleBuilder(table)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addStatement("final int $L = 200", defaultChunkSizeLocalParamName)
            .addStatement("$L($L, $L)", insertMultipleMethodName(), posParamName, defaultChunkSizeLocalParamName)
            .end()
            .build();
    }

    protected MethodSpec deleteByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        TableHelper.findRecordVersionColumn(primaryKey.getOwner()).ifPresent(columnList::add);
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return deleteByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .beginIf("$L.deleteGeneral($L.deleteByPrimaryKey($L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), parameters)
            .addCode(b -> {
                b.addStatement("$T builder = builder(getClass(), $S, DELETE)", DefaultRepositoryContext.Builder.class, deleteByPrimaryKeyMethodName());
                b.addStatement("builder.setPoClass($T.class)", poClassName(table));
                b.addStatement("builder.setDiscriminator(AFFECTED_ROWS_COUNT_IS_UNEXPECTED)");
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    b.addStatement("builder.addParam($S, $L)", columnParamName, columnParamName);
                });
                b.addStatement("throw $L.convertTo(builder.build())", exceptionConverterFieldName());
            })
            .endIf()
            .end()
            .build();
    }

    protected MethodSpec updateByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        TableHelper.findRecordVersionColumn(primaryKey.getOwner()).ifPresent(columnList::add);
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        String updateParamName = updateParamName(table);
        String paramsLocalParamName = "params";
        return updateByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> {
                b.addStatement("argNotNull($L, $S)", updateParamName, updateParamName);
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
                });
            })
            .addCode(b -> {
                b.addCode("$T<String, Object> $L = ofHashMap(entry(UPDATE_BY_FIELD_OBJECT, $L)", HashMap.class, paramsLocalParamName, updateParamName);
                if (primaryKey.getIndexColumns().length == 1) {
                    Column column = primaryKey.getIndexColumns()[0].getColumn();
                    b.addCode(", entry(RECORD_ID, $L)", columnToParamNameTranslator.translate(column));
                } else {
                    Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(column -> b.addCode(", entry($L, $L)", columnToParamNameTranslator.translate(column), columnToParamNameTranslator.translate(column)));
                }
                TableHelper.findRecordVersionColumn(primaryKey.getOwner()).ifPresent(column -> b.addCode(", entry(RECORD_VERSION, $L)", columnToParamNameTranslator.translate(column)));
                b.addCode(");").addNewLine();
            })
            .addStatement("$L($L)", beforeUpdateMethodName(), paramsLocalParamName)
            .beginIf("$L.update($L.updateByPrimaryKey($L, $L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), updateParamName((table)), parameters)
            .addCode(b -> {
                b.addStatement("Builder builder = builder(getClass(), $S, $T.UPDATE)", updateByPrimaryKeyMethodName(), SqlMethod.class);
                b.addStatement("builder.setPoClass($T.class)", poClassName(table));
                b.addStatement("builder.setDiscriminator(AFFECTED_ROWS_COUNT_IS_UNEXPECTED)");
                b.addStatement("builder.addParam($L)", paramsLocalParamName);
                b.addStatement("throw $L.convertTo(builder.build())", exceptionConverterFieldName());
            })
            .endIf()
            .addStatement("$L($L)", afterUpdateMethodName(), paramsLocalParamName)
            .end()
            .build();
    }

    protected MethodSpec getByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return getByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .addStatement("return $L.selectOne($L.selectByPrimaryKey($L))", mapperFieldName(table), sqlProviderFieldName(table), parameters)
            .end()
            .build();
    }

    protected MethodSpec loadByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return loadByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .addCode(b -> {
                b.addCode("return $L.selectOne($L.selectByPrimaryKey($L))", mapperFieldName(table), sqlProviderFieldName(table), parameters).addNewLine();
                b.addIndent(2);
                b.addCode(".orElseThrow(() -> $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, SELECT)", loadByPrimaryKeyMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".setDiscriminator(FOUND_NONE)").addNewLine();
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(columnList.get(0));
                    b.addCode(".addParam($S, $L)", columnParamName, columnParamName).addNewLine();
                });
                b.addCode(".build()));").addNewLine();
                b.removeIndent(6);
            })
            .end()
            .build();
    }

    protected MethodSpec loadWithThrowByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return loadWithThrowByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> {
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
                });
                b.addStatement("argNotNull($L, $S)", exceptionSupplierParamName(), exceptionSupplierParamName());
            })
            .addStatement("return $L.selectOne($L.selectByPrimaryKey($L)).orElseThrow($L)", mapperFieldName(table), sqlProviderFieldName(table), parameters, exceptionSupplierParamName())
            .end()
            .build();
    }

    protected MethodSpec existByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return existByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .addStatement("return $L.count($L.countByPrimaryKey($L)) == 1", mapperFieldName(table), sqlProviderFieldName(table), parameters)
            .end()
            .build();
    }

    protected MethodSpec assertExistByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return assertExistByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .beginIf("$L.count($L.countByPrimaryKey($L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), parameters)
            .addCode(b -> {
                b.addStatement("Builder builder = builder(getClass(), $S, SELECT)", assertExistByPrimaryKeyMethodName());
                b.addStatement("builder.setPoClass($T.class)", poClassName(table));
                b.addStatement("builder.setDiscriminator(FOUND_NONE)");
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    b.addStatement("builder.addParam($S, $L)", columnParamName, columnParamName);
                });
                b.addStatement("throw $L.convertTo(builder.build())", exceptionConverterFieldName());
            })
            .endIf()
            .end()
            .build();
    }

    protected MethodSpec assertExistWithThrowByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return assertExistWithThrowByPrimaryKeyBuilder(primaryKey)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> {
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
                });
                b.addStatement("argNotNull($L, $S)", exceptionSupplierParamName(), exceptionSupplierParamName());
            })
            .beginIf("$L.count($L.countByPrimaryKey($L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), parameters)
            .addStatement("throw $L.get()", exceptionSupplierParamName())
            .endIf()
            .end()
            .build();
    }

    protected MethodSpec queryOne(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return queryOneBuild(table)
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), q -> !q.isCount(), q -> \"The count field in the $L should be set to false.\")", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName)
            .addStatement("return $L.selectOne($L.selectOrCountDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec queryMany(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return queryManyBuild(table)
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), q -> !q.isCount(), q -> \"The count field in the $L should be set to false.\")", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName)
            .addStatement("return $L.selectMany($L.selectOrCountDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec cursor(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        String consumerParamName = consumerParamName();
        String cursorLocalParamName = "cursor";
        return cursorBuild(table)
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argNotNull($L, $S)", consumerParamName, consumerParamName)
            .addStatement("argument(argNotNull($L, $S), q -> !q.isCount(), q -> \"The count field in the $L should be set to false.\")", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName)
            .beginTry("$T<$T> $L = $L.cursor($L.selectOrCountDynamic($L))",
                Cursor.class, poClassName(table), cursorLocalParamName, mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addStatement("$L.forEach($L)", cursorLocalParamName, consumerParamName)
            .beginCatch("$T e", IOException.class)
            .addStatement("throw new $T(e)", UncheckedIOException.class)
            .endTry()
            .end()
            .build();
    }

    protected MethodSpec loadOne(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return loadOneBuild(table)
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), q -> !q.isCount(), q -> \"The count field in the $L should be set to false.\")", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName)
            .addCode(b -> {
                b.addCode("return $L.selectOne($L.selectOrCountDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName).addNewLine();
                b.addIndent(2);
                b.addCode(".orElseThrow(() -> $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, SELECT)", loadOneMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".setDiscriminator(FOUND_NONE)").addNewLine();
                b.addCode(".addParam($S, $L)", queryWrapperParamName, queryWrapperParamName).addNewLine();
                b.addCode(".build()));").addNewLine();
                b.removeIndent(6);
            })
            .end()
            .build();
    }

    protected MethodSpec loadOneWithThrow(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return loadOneWithThrowBuild(table)
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), q -> !q.isCount(), q -> \"The count field in the $L should be set to false.\")", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName)
            .addStatement("argNotNull($L, $S)", exceptionSupplierParamName(), exceptionSupplierParamName())
            .addStatement("return $L.selectOne($L.selectOrCountDynamic($L)).orElseThrow($L)", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName, exceptionSupplierParamName())
            .end()
            .build();
    }

    protected MethodSpec count(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return countBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("return $L.count($L.selectOrCountDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec exist(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return existBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("return $L.count($L.selectOrCountDynamic($L)) > 0", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec assertExist(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return assertExistBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .beginIf("$L.count($L.selectOrCountDynamic($L)) <= 0", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addCode(b -> {
                b.addCode("throw $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, SELECT)", assertExistMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".setDiscriminator(FOUND_NONE)").addNewLine();
                b.addCode(".addParam($S, $L)", queryWrapperParamName, queryWrapperParamName).addNewLine();
                b.addCode(".build());").addNewLine();
                b.removeIndent(4);
            })
            .endIf()
            .end()
            .build();
    }

    protected MethodSpec assertExistWithThrow(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return assertExistWithThrowBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("argNotNull($L, $S)", exceptionSupplierParamName(), exceptionSupplierParamName())
            .beginIf("$L.count($L.selectOrCountDynamic($L)) <= 0", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addStatement("throw $L.get()", exceptionSupplierParamName())
            .endIf()
            .end()
            .build();
    }

    protected MethodSpec existOne(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return existOneBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("return $L.count($L.selectOrCountDynamic($L)) == 1", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec assertExistOne(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return assertExistOneBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .beginIf("$L.count($L.selectOrCountDynamic($L)) == 0", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addCode(b -> {
                b.addCode("throw $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, SELECT)", assertExistMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".setDiscriminator(FOUND_NONE)").addNewLine();
                b.addCode(".addParam($S, $L)", queryWrapperParamName, queryWrapperParamName).addNewLine();
                b.addCode(".build());").addNewLine();
                b.removeIndent(4);
            })
            .endIf()
            .beginIf("$L.count($L.selectOrCountDynamic($L)) > 1", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addCode(b -> {
                b.addCode("throw $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, SELECT)", assertExistMethodName()).addNewLine();
                b.addIndent(2);
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".setDiscriminator(FOUND_MULTIPLE)").addNewLine();
                b.addCode(".addParam($S, $L)", queryWrapperParamName, queryWrapperParamName).addNewLine();
                b.addCode(".build());").addNewLine();
                b.removeIndent(4);
            })
            .endIf()
            .end()
            .build();
    }

    protected MethodSpec assertExistOneWithThrow(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return assertExistOneWithThrowBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("argNotNull($L, $S)", "recordNotFoundExceptionSupplier", "recordNotFoundExceptionSupplier")
            .addStatement("argNotNull($L, $S)", "multipleRecordsFoundExceptionSupplier", "multipleRecordsFoundExceptionSupplier")
            .beginIf("$L.count($L.selectOrCountDynamic($L)) == 0", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addStatement("throw $L.get()", "recordNotFoundExceptionSupplier")
            .endIf()
            .beginIf("$L.count($L.selectOrCountDynamic($L)) > 1", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .addStatement("throw $L.get()", "multipleRecordsFoundExceptionSupplier")
            .endIf()
            .end()
            .build();
    }


    protected MethodSpec delete(Table table) {
        return deleteBuild()
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("return $L.deleteGeneral($L.deleteDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), conditionParamName())
            .end()
            .build();
    }

    protected MethodSpec update(Table table) {
        String columnUpdateParamName = columnUpdateParamName(table);
        String conditionParamName = conditionParamName();
        String paramsLocalParamName = "params";
        String affectedRowCountLocalParamName = "affectedRowCount";
        return updateBuild(table)
            .addModifier(PUBLIC)
            .addAnnotation(Override.class)
            .beginMethodBody()
            .addStatement("argNotNull($L,$S)", columnUpdateParamName, columnUpdateParamName)
            .addStatement("argNotNull($L,$S)", conditionParamName, conditionParamName)
            .addStatement("$T<String, Object> $L = ofHashMap(entry(UPDATE_BY_COLUMN_OBJECT, $L), entry($S, $L))",
                HashMap.class, paramsLocalParamName, columnUpdateParamName, conditionParamName, conditionParamName)
            .addStatement("$L($L)", beforeUpdateMethodName(), paramsLocalParamName)
            .addStatement("long $L =  $L.updateGeneral($L.updateDynamic($L, $L))", affectedRowCountLocalParamName, mapperFieldName(table), sqlProviderFieldName(table), columnUpdateParamName, conditionParamName())
            .addStatement("$L($L)", afterUpdateMethodName(), paramsLocalParamName)
            .addStatement("return $L", affectedRowCountLocalParamName)
            .end()
            .build();
    }

}
