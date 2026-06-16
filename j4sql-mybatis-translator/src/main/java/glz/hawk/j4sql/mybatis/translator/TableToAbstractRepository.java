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
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.j4sql.util.QueryWrapper;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.core.helper.MapHelper;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawkframework.core.support.ArgumentSupport;
import glz.hawkframework.dao.context.DefaultRepositoryContext;
import glz.hawkframework.dao.context.ExceptionConverter;
import glz.hawkframework.dao.context.SqlMethod;
import glz.hawkframework.dao.process.InsertParameter;
import glz.hawkframework.dao.process.InsertProcessor;
import glz.hawkframework.dao.process.UpdateParameter;
import glz.hawkframework.dao.process.UpdateProcessor;
import org.apache.ibatis.cursor.Cursor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;

import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static javax.lang.model.element.Modifier.PROTECTED;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToAbstractRepository extends AbstractTableToRepository implements Translator<Table, JavaFile> {

    protected final Translator<Table, String> mapperClassPackageTranslator;

    protected final Translator<Table, String> mapperClassNameTranslator;

    protected final Translator<Table, String> sqlProviderClassPackageTranslator;

    protected final Translator<Table, String> sqlProviderClassNameTranslator;

    protected Translator<Table, String> abstractRepositoryPackageTranslator;

    protected Translator<Table, String> abstractRepositoryClassNameTranslator;

    public TableToAbstractRepository(Translator<Table, String> repositoryPackageTranslator,
                                     Translator<Table, String> repositoryClassNameTranslator,
                                     Translator<Table, String> abstractRepositoryPackageTranslator,
                                     Translator<Table, String> abstractRepositoryClassNameTranslator,
                                     Translator<Table, String> mapperClassPackageTranslator,
                                     Translator<Table, String> mapperClassNameTranslator,
                                     Translator<Table, String> sqlProviderClassPackageTranslator,
                                     Translator<Table, String> sqlProviderClassNameTranslator,
                                     Translator<Table, String> poPackageTranslator,
                                     Translator<Table, String> poClassNameTranslator,
                                     Translator<Column, String> columnToParamNameTranslator,
                                     Translator<Column, TypeName> columnToTypeNameTranslator,
                                     Translator<Table, String> updateClassPackageTranslator,
                                     Translator<Table, String> updateClassNameTranslator,
                                     Translator<Table, String> columnUpdateClassNameTranslator,
                                     ColumnFinder recordVersionColumnFinder,
                                     boolean supportColumnInsertOrUpdate) {
        super(repositoryPackageTranslator,
            repositoryClassNameTranslator,
            poPackageTranslator,
            poClassNameTranslator,
            columnToParamNameTranslator,
            columnToTypeNameTranslator,
            updateClassPackageTranslator,
            updateClassNameTranslator,
            columnUpdateClassNameTranslator,
            recordVersionColumnFinder,
            supportColumnInsertOrUpdate);
        this.abstractRepositoryPackageTranslator = argNotNull(abstractRepositoryPackageTranslator, "abstractRepositoryPackageTranslator");
        this.abstractRepositoryClassNameTranslator = argNotNull(abstractRepositoryClassNameTranslator, "abstractRepositoryClassNameTranslator");
        this.mapperClassPackageTranslator = argNotNull(mapperClassPackageTranslator, "mapperClassPackageTranslator");
        this.mapperClassNameTranslator = argNotNull(mapperClassNameTranslator, "mapperClassNameTranslator");
        this.sqlProviderClassPackageTranslator = argNotNull(sqlProviderClassPackageTranslator, "sqlProviderClassPackageTranslator");
        this.sqlProviderClassNameTranslator = argNotNull(sqlProviderClassNameTranslator, "sqlProviderClassNameTranslator");
    }

    @Override
    public @NonNull JavaFile translate(@NonNull Table table) {
        String className = abstractRepositoryClassNameTranslator.translate(argNotNull(table, "table"));
        ClassSpec.Builder builder = ClassSpec.builder(className).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        // constructors
        builder.addConstructor(constructor(table));

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

        if (supportColumnInsertOrUpdate) builder.addMethod(update(table));

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

        return JavaFile.builder(abstractRepositoryPackageTranslator.translate(table), builder.build()).build();
    }

    protected ConstructorSpec constructor(Table table) {
        String mapperParamName = mapperFieldName(table);
        String sqlProviderParamName = sqlProviderFieldName(table);
        return ConstructorSpec.builder(PROTECTED)
            .addParameter(ParameterSpec.builder(mapperClassName(table), mapperParamName).build())
            .addParameter(ParameterSpec.builder(sqlProviderClassName(table), sqlProviderParamName).build())
            .beginConstructorBody()
            .addStatement("this.$L = argNotNull($L, $S)", mapperFieldName(table), mapperParamName, mapperParamName)
            .addStatement("this.$L = argNotNull($L, $S)", sqlProviderFieldName(table), sqlProviderParamName, sqlProviderParamName)
            .end()
            .build();
    }

    protected FieldSpec mapper(Table table) {
        return FieldSpec.builder(mapperClassName(table), mapperFieldName(table), Modifier.PROTECTED, Modifier.FINAL).build();
    }

    protected ClassName mapperClassName(Table table) {
        return ClassName.of(mapperClassPackageTranslator.translate(table), mapperClassNameTranslator.translate(table));
    }

    protected String mapperFieldName(Table table) {
        return StringHelper.unCapitalize(mapperClassNameTranslator.translate(table));
    }

    protected FieldSpec sqlProvider(Table table) {
        return FieldSpec.builder(sqlProviderClassName(table), sqlProviderFieldName(table), Modifier.PROTECTED, Modifier.FINAL).build();
    }

    protected ClassName sqlProviderClassName(Table table) {
        return ClassName.of(sqlProviderClassPackageTranslator.translate(table), sqlProviderClassNameTranslator.translate(table));
    }

    protected String sqlProviderFieldName(Table table) {
        return StringHelper.unCapitalize(sqlProviderClassNameTranslator.translate(table));
    }

    protected FieldSpec insertProcessor() {
        return FieldSpec.builder(insertProcessorClassName(), insertProcessorFieldName(), Modifier.PROTECTED)
            .addAnnotation(AnnotationInstanceSpec.builder(Autowired.class).addMember("required", false).build())
            .build();
    }

    protected ClassName insertProcessorClassName() {
        return ClassName.ofClass(InsertProcessor.class);
    }

    protected String insertProcessorFieldName() {
        return "insertProcessor";
    }

    protected ClassName updateProcessorClassName() {
        return ClassName.ofClass(UpdateProcessor.class);
    }

    protected String updateProcessorFieldName() {
        return "updateProcessor";
    }

    protected FieldSpec updateProcessor() {
        return FieldSpec.builder(updateProcessorClassName(), updateProcessorFieldName(), Modifier.PROTECTED)
            .addAnnotation(AnnotationInstanceSpec.builder(Autowired.class).addMember("required", false).build())
            .build();
    }

    protected String exceptionConverterFieldName() {
        return "exceptionConverter";
    }

    protected FieldSpec exceptionConverter() {
        return FieldSpec.builder(ExceptionConverter.class, exceptionConverterFieldName(), Modifier.PROTECTED)
            .addAnnotation(Autowired.class)
            .build();
    }

    protected MethodSpec queryOne(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return queryOneBuild(table)
            .addModifier(PROTECTED)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), q -> !q.isCount(), q -> \"The count field in the $L should be set to false.\")", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName)
            .addStatement("return $L.selectOne($L.selectOrCountDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec queryMany(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return queryManyBuild(table)
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("return $L.count($L.selectOrCountDynamic($L))", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec exist(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return existBuild()
            .addModifier(PROTECTED)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("return $L.count($L.selectOrCountDynamic($L)) > 0", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec assertExist(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return assertExistBuild()
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
            .beginMethodBody()
            .addStatement("argument(argNotNull($L, $S), $T::isCount, q -> \"The count field in the $L should be set to true.\")", queryWrapperParamName, queryWrapperParamName, QueryWrapper.class, queryWrapperParamName)
            .addStatement("return $L.count($L.selectOrCountDynamic($L)) == 1", mapperFieldName(table), sqlProviderFieldName(table), queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec assertExistOne(Table table) {
        String queryWrapperParamName = queryWrapperParamName();
        return assertExistOneBuild()
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
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
            .addModifier(PROTECTED)
            .beginMethodBody()
            .addStatement("argNotNull($L, $S)",deleteWrapperParamName(),deleteWrapperParamName())
            .addStatement("return $L.deleteGeneral($L.deleteDynamic($L.getCondition()))", mapperFieldName(table), sqlProviderFieldName(table), deleteWrapperParamName())
            .end()
            .build();
    }

    protected MethodSpec update(Table table) {
        String conditionParamName = conditionParamName();
        String paramsLocalParamName = "updateParameter";
        String affectedRowCountLocalParamName = "affectedRowCount";
        return updateBuild(table)
            .addModifier(PROTECTED)
            .beginMethodBody()
            .addStatement("argNotNull($L,$S)", updateWrapperParamName(), updateWrapperParamName())
            .addStatement("$T $L = $T.builder().setColumnUpdateObject($L.getUpdateObject()).addParam($S, $L.getCondition()).build()", UpdateParameter.class, paramsLocalParamName, UpdateParameter.class, updateWrapperParamName(), conditionParamName, updateWrapperParamName())
            .addStatement("$L($L)", beforeUpdateMethodName(), paramsLocalParamName)
            .addStatement("long $L =  $L.updateGeneral($L.updateDynamic($L.getUpdateObject(), $L.getCondition()))", affectedRowCountLocalParamName, mapperFieldName(table), sqlProviderFieldName(table), updateWrapperParamName(), updateWrapperParamName())
            .addStatement("$L($L)", afterUpdateMethodName(), paramsLocalParamName)
            .addStatement("return $L", affectedRowCountLocalParamName)
            .end()
            .build();
    }

    protected String beforeInsertMethodName() {
        return "beforeInsert";
    }

    protected MethodSpec beforeInsert() {
        final String paramName = "insertParameter";
        return MethodSpec.builder(VOID, beforeInsertMethodName(), Modifier.PROTECTED)
            .addParameter(InsertParameter.class, paramName)
            .beginMethodBody()
            .beginIf("$L != null", insertProcessorFieldName())
            .addStatement("$L.beforeInsert($L)", insertProcessorFieldName(), paramName)
            .endIf()
            .end()
            .build();
    }

    protected String afterInsertMethodName() {
        return "afterInsert";
    }

    protected MethodSpec afterInsert() {
        final String paramName = "insertParameter";
        return MethodSpec.builder(VOID, afterInsertMethodName(), Modifier.PROTECTED)
            .addParameter(InsertParameter.class, paramName)
            .beginMethodBody()
            .beginIf("$L != null", insertProcessorFieldName())
            .addStatement("$L.afterInsert($L)", insertProcessorFieldName(), paramName)
            .endIf()
            .end()
            .build();
    }

    protected String beforeUpdateMethodName() {
        return "beforeUpdate";
    }

    protected MethodSpec beforeUpdate() {
        final String paramName = "updateParameter";
        return MethodSpec.builder(VOID, beforeUpdateMethodName(), Modifier.PROTECTED)
            .addParameter(UpdateParameter.class, paramName)
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
        final String paramName = "updateParameter";
        return MethodSpec.builder(VOID, afterUpdateMethodName(), Modifier.PROTECTED)
            .addParameter(UpdateParameter.class, paramName)
            .beginMethodBody()
            .beginIf("$L != null", updateProcessorFieldName())
            .addStatement("$L.afterUpdate($L)", updateProcessorFieldName(), paramName)
            .endIf()
            .end()
            .build();
    }

}
