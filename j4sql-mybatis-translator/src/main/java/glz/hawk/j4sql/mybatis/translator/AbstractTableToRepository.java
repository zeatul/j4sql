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

import com.google.common.base.CaseFormat;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.ParameterSpec;
import glz.hawk.codepoet.java.type.*;
import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.util.DeleteWrapper;
import glz.hawk.j4sql.util.QueryWrapper;
import glz.hawk.j4sql.util.UpdateWrapper;
import glz.hawk.jdesigner.spec.database.*;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.core.helper.StringHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static glz.hawk.codepoet.java.type.PrimitiveTypeName.*;
import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractTableToRepository {
    protected final Translator<Table, String> repositoryClassPackageTranslator;

    protected final Translator<Table, String> repositoryClassNameTranslator;

    protected final Translator<Table, String> poClassPackageTranslator;

    protected final Translator<Table, String> poClassNameTranslator;

    protected final Translator<Column, String> columnToParamNameTranslator;

    protected final Translator<Column, TypeName> columnToTypeNameTranslator;

    protected final Translator<Table, String> updateClassPackageTranslator;

    protected final Translator<Table, String> updateClassNameTranslator;

    protected final Translator<Table, String> columnUpdateClassNameTranslator;

    protected final ColumnFinder recordVersionColumnFinder;

    protected final boolean supportColumnInsertOrUpdate;

    protected AbstractTableToRepository(
        Translator<Table, String> repositoryClassPackageTranslator,
        Translator<Table, String> repositoryClassNameTranslator,
        Translator<Table, String> poClassPackageTranslator,
        Translator<Table, String> poClassNameTranslator,
        Translator<Column, String> columnToParamNameTranslator,
        Translator<Column, TypeName> columnToTypeNameTranslator,
        Translator<Table, String> updateClassPackageTranslator,
        Translator<Table, String> updateClassNameTranslator,
        Translator<Table, String> columnUpdateClassNameTranslator,
        ColumnFinder recordVersionColumnFinder,
        boolean supportColumnInsertOrUpdate
    ) {
        this.repositoryClassPackageTranslator = argNotNull(repositoryClassPackageTranslator, "repositoryClassPackageTranslator");
        this.repositoryClassNameTranslator = argNotNull(repositoryClassNameTranslator, "repositoryClassNameTranslator");
        this.poClassPackageTranslator = argNotNull(poClassPackageTranslator, "poClassPackageTranslator");
        this.poClassNameTranslator = argNotNull(poClassNameTranslator, "poClassNameTranslator");
        this.columnToParamNameTranslator = argNotNull(columnToParamNameTranslator, "columnToParamNameTranslator");
        this.columnToTypeNameTranslator = argNotNull(columnToTypeNameTranslator, "columnToTypeNameTranslator");
        this.updateClassPackageTranslator = argNotNull(updateClassPackageTranslator, "updateClassPackageTranslator");
        this.updateClassNameTranslator = argNotNull(updateClassNameTranslator, "updateClassNameTranslator");
        this.columnUpdateClassNameTranslator = argNotNull(columnUpdateClassNameTranslator, "columnUpdateClassNameTranslator");
        this.recordVersionColumnFinder = argNotNull(recordVersionColumnFinder, "recordVersionColumnFinder");
        this.supportColumnInsertOrUpdate = supportColumnInsertOrUpdate;
    }

    protected ClassName poClassName(Table table) {
        return ClassName.of(poClassPackageTranslator.translate(table), poClassNameTranslator.translate(table));
    }

    protected String poParamName(Table table) {
        return StringHelper.unCapitalize(poClassNameTranslator.translate(table));
    }

    protected ParameterizedTypeName posClassName(Table table) {
        return ParameterizedTypeName.of(List.class, poClassName(table));
    }

    protected String posParamName(Table table) {
        return poParamName(table) + "s";
    }

    protected ClassName updateClassName(Table table) {
        return ClassName.of(updateClassPackageTranslator.translate(table), updateClassNameTranslator.translate(table));
    }

    protected String updateParamName(Table table) {
        return StringHelper.unCapitalize(updateClassNameTranslator.translate(table));
    }

    protected ClassName columnUpdateClassName(Table table) {
        return ClassName.of(updateClassPackageTranslator.translate(table), columnUpdateClassNameTranslator.translate(table));
    }

    protected String columnUpdateParamName(Table table) {
        return StringHelper.unCapitalize(columnUpdateClassNameTranslator.translate(table));
    }

    protected String insertMethodName() {
        return "insert";
    }

    protected MethodSpec.Builder insertBuilder(Table table) {
        return MethodSpec.builder(VOID, insertMethodName())
            .addParameter(poClassName(table), poParamName(table));
    }

    protected String insertSelectiveMethodName() {
        return "insertSelective";
    }

    protected MethodSpec.Builder insertSelectiveBuilder(Table table) {
        return MethodSpec.builder(VOID, insertSelectiveMethodName())
            .addParameter(poClassName(table), poParamName(table));
    }

    protected String chunkSizeParamName() {
        return "chunkSize";
    }

    protected String insertMultipleMethodName() {
        return "insertMultiple";
    }

    protected MethodSpec.Builder insertMultipleWithChunkBuilder(Table table) {
        return MethodSpec.builder(VOID, insertMultipleMethodName())
            .addParameter(posClassName(table), posParamName(table))
            .addParameter(INT, chunkSizeParamName());
    }

    protected MethodSpec.Builder insertMultipleBuilder(Table table) {
        return MethodSpec.builder(VOID, insertMultipleMethodName())
            .addParameter(posClassName(table), posParamName(table));
    }

    protected String deleteByPrimaryKeyOrUniqueIndexMethodName(IndexSupport<?> indexSupport) {
        return indexSupport instanceof PrimaryKey ?
            "deleteByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "DELETE_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
    }

    protected MethodSpec.Builder deleteByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        return MethodSpec.builder(VOID, deleteByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .add(b -> Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .add(b -> recordVersionColumnFinder.find(indexSupport.getOwner()).ifPresent(column -> b.addParameter(columnToTypeNameTranslator.translate(column), columnToParamNameTranslator.translate(column))));
    }

    protected String updateByPrimaryKeyOrUniqueIndexMethodName(IndexSupport<?> indexSupport) {
        return indexSupport instanceof PrimaryKey ?
            "updateByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "UPDATE_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

    }

    protected MethodSpec.Builder updateByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        return MethodSpec.builder(VOID, updateByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .addParameter(updateClassName(table), updateParamName(table))
            .add(b -> Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .add(b -> recordVersionColumnFinder.find(indexSupport.getOwner()).ifPresent(column -> b.addParameter(columnToTypeNameTranslator.translate(column), columnToParamNameTranslator.translate(column))));
    }

    private String getByPrimaryKeyOrIndexMethodName(IndexSupport<?> indexSupport) {
        return indexSupport instanceof PrimaryKey ?
            "getByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "GET_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

    }

    protected MethodSpec.Builder getByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        return MethodSpec.builder(ParameterizedTypeName.of(Optional.class, poClassName(table)), getByPrimaryKeyOrIndexMethodName(indexSupport))
            .add(b -> Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))));
    }

    protected String loadByPrimaryKeyOrUniqueIndexMethodName(IndexSupport<?> indexSupport) {
        return indexSupport instanceof PrimaryKey ?
            "loadByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "LOAD_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

    }

    protected MethodSpec.Builder loadByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        return MethodSpec.builder(poClassName(table), loadByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .add(b -> Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .setJavadoc("Throws an exception if found no record by the primary key or unique index.");
    }

    protected String exceptionSupplierParamName() {
        return "exceptionSupplier";
    }

    protected MethodSpec.Builder loadWithThrowByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        String typeVariableName = "E";
        String exceptionSupplierParamName = exceptionSupplierParamName();
        return MethodSpec.builder(poClassName(table), loadByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .addTypeVariable(TypeVariableName.of(typeVariableName, Throwable.class))
            .add(b -> {
                Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c)));
                b.addParameter(ParameterizedTypeName.of(Supplier.class, WildcardTypeName.ofUpper(TypeVariableName.of(typeVariableName))), exceptionSupplierParamName);
            })
            .addThrowable(TypeVariableName.of(typeVariableName))
            .setJavadoc("Throws the supplied exception if found no record by the primary key or unique index.");
    }


    protected String existByPrimaryKeyOrUniqueIndexMethodName(IndexSupport<?> indexSupport) {
        return indexSupport instanceof PrimaryKey ?
            "existByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "EXIST_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
    }

    protected MethodSpec.Builder existByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        return MethodSpec.builder(BOOLEAN, existByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .add(b -> Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))));
    }

    protected String assertExistByPrimaryKeyOrUniqueIndexMethodName(IndexSupport<?> indexSupport) {
        return indexSupport instanceof PrimaryKey ?
            "assertExistByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "ASSERT_EXIST_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
    }

    protected MethodSpec.Builder assertExistByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        return MethodSpec.builder(VOID, assertExistByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .add(b -> Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c)))).setJavadoc("Throws an exception if the required record doesn't exist");
    }

    protected MethodSpec.Builder assertExistWithThrowByPrimaryKeyOrUniqueIndexBuilder(IndexSupport<?> indexSupport) {
        String typeVariableName = "E";
        String exceptionSupplierParamName = exceptionSupplierParamName();
        return MethodSpec.builder(VOID, assertExistByPrimaryKeyOrUniqueIndexMethodName(indexSupport))
            .addTypeVariable(TypeVariableName.of(typeVariableName, Throwable.class))
            .add(b -> {
                Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c)));
                b.addParameter(ParameterizedTypeName.of(Supplier.class, WildcardTypeName.ofUpper(TypeVariableName.of(typeVariableName))), exceptionSupplierParamName);
            }).addThrowable(TypeVariableName.of(typeVariableName))
            .setJavadoc("Throws the supplied exception if the required record doesn't exist");
    }

    // common methods

    protected String queryWrapperParamName() {
        return "queryWrapper";
    }

    protected MethodSpec.Builder queryOneBuild(Table table) {
        return MethodSpec.builder(ParameterizedTypeName.of(Optional.class, poClassName(table)), "queryOne")
            .addParameter(QueryWrapper.class, queryWrapperParamName());
    }

    protected MethodSpec.Builder queryManyBuild(Table table) {
        return MethodSpec.builder(ParameterizedTypeName.of(List.class, poClassName(table)), "queryMany")
            .addParameter(QueryWrapper.class, queryWrapperParamName());
    }

    protected String consumerParamName() {
        return "consumer";
    }

    protected MethodSpec.Builder cursorBuild(Table table) {
        return MethodSpec.builder(VOID, "cursor")
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .addParameter(ParameterizedTypeName.of(Consumer.class, poClassName(table)), consumerParamName());
    }

    protected String loadOneMethodName() {
        return "loadOne";
    }

    protected MethodSpec.Builder loadOneBuild(Table table) {
        return MethodSpec.builder(poClassName(table), loadOneMethodName())
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .setJavadoc("Throws an exception if found no required record.");
    }

    protected MethodSpec.Builder loadOneWithThrowBuild(Table table) {
        String typeVariableName = "E";
        String exceptionSupplierParamName = exceptionSupplierParamName();
        return MethodSpec.builder(poClassName(table), "loadOne")
            .addTypeVariable(TypeVariableName.of(typeVariableName, Throwable.class))
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .addParameter(ParameterizedTypeName.of(Supplier.class, WildcardTypeName.ofUpper(TypeVariableName.of(typeVariableName))), exceptionSupplierParamName).addThrowable(TypeVariableName.of(typeVariableName))
            .setJavadoc("Throws the supplied exception if found no required record.");
    }

    protected MethodSpec.Builder countBuild() {
        return MethodSpec.builder(LONG, "count")
            .addParameter(QueryWrapper.class, queryWrapperParamName());
    }

    protected MethodSpec.Builder existBuild() {
        return MethodSpec.builder(BOOLEAN, "exist")
            .addParameter(QueryWrapper.class, queryWrapperParamName());
    }

    protected String assertExistMethodName() {
        return "assertExist";
    }

    protected MethodSpec.Builder assertExistBuild() {
        return MethodSpec.builder(VOID, assertExistMethodName())
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .setJavadoc("Throws an exception if found no required record.");
    }

    protected MethodSpec.Builder assertExistWithThrowBuild() {
        String typeVariableName = "E";
        String exceptionSupplierParamName = exceptionSupplierParamName();
        return MethodSpec.builder(VOID, "assertExist")
            .addTypeVariable(TypeVariableName.of(typeVariableName, Throwable.class))
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .addParameter(ParameterizedTypeName.of(Supplier.class, WildcardTypeName.ofUpper(TypeVariableName.of(typeVariableName))), exceptionSupplierParamName).addThrowable(TypeVariableName.of(typeVariableName))
            .setJavadoc("Throws the supplied exception if found no required record.");
    }

    protected MethodSpec.Builder existOneBuild() {
        return MethodSpec.builder(BOOLEAN, "existOne")
            .addParameter(QueryWrapper.class, queryWrapperParamName());
    }

    protected MethodSpec.Builder assertExistOneBuild() {
        return MethodSpec.builder(VOID, "assertExistOne")
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .setJavadoc("Throws an exception if found no required record or found more than one record.");
    }

    protected MethodSpec.Builder assertExistOneWithThrowBuild() {
        return MethodSpec.builder(VOID, "assertExistOne")
            .addTypeVariable(TypeVariableName.of("E1", Throwable.class))
            .addTypeVariable(TypeVariableName.of("E2", Throwable.class))
            .addParameter(QueryWrapper.class, queryWrapperParamName())
            .addParameter(ParameterizedTypeName.of(Supplier.class, WildcardTypeName.ofUpper(TypeVariableName.of("E1"))), "recordNotFoundExceptionSupplier")
            .addParameter(ParameterizedTypeName.of(Supplier.class, WildcardTypeName.ofUpper(TypeVariableName.of("E2"))), "multipleRecordsFoundExceptionSupplier")
            .addThrowable(TypeVariableName.of("E1"))
            .addThrowable(TypeVariableName.of("E2"))
            .setJavadoc("Throws the supplied exception if found no required record or found more than one record.");
    }

    protected String conditionParamName() {
        return "condition";
    }

    protected String deleteWrapperParamName() {
        return "deleteWrapper";
    }

    protected TypeName deleteWrapperClassName() {
        return ClassName.ofClass(DeleteWrapper.class);
    }


    protected MethodSpec.Builder deleteBuild() {
        return MethodSpec.builder(LONG, "delete")
            .addParameter(ParameterSpec.builder(deleteWrapperClassName(), deleteWrapperParamName()).addAnnotation(Nonnull.class).build());
    }

    protected String updateWrapperParamName() {
        return "updateWrapper";
    }

    protected TypeName updateWrapperClassName(Table table) {
        return ParameterizedTypeName.of(ClassName.ofClass(UpdateWrapper.class), columnUpdateClassName(table));
    }

    protected MethodSpec.Builder updateBuild(Table table) {
        return MethodSpec.builder(LONG, "update")
            .addParameter(ParameterSpec.builder(updateWrapperClassName(table), updateWrapperParamName()).addAnnotation(Nonnull.class).build());
    }

}
