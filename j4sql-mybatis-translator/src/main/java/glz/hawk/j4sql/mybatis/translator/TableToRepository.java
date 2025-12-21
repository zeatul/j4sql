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
import glz.hawk.jdesigner.spec.database.PrimaryKey;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.codepoet.java.InterfaceSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.type.TypeName;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToRepository extends AbstractTableToRepository implements Translator<Table, JavaFile> {


    public TableToRepository(Translator<Table, String> repositoryPackageTranslator, Translator<Table, String> repositoryClassNameTranslator,
                             Translator<Table, String> poPackageTranslator, Translator<Table, String> poClassNameTranslator,
                             Translator<Column, String> columnToParamNameTranslator, Translator<Column, TypeName> columnToTypeNameTranslator,
                             Translator<Table, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator,
                             Translator<Table, String> columnUpdateClassNameTranslator) {
        super(repositoryPackageTranslator, repositoryClassNameTranslator, poPackageTranslator, poClassNameTranslator,
            columnToParamNameTranslator, columnToTypeNameTranslator, updateClassPackageTranslator, updateClassNameTranslator,
            columnUpdateClassNameTranslator);
    }

    @Nonnull
    @Override
    public JavaFile translate(@Nonnull Table table) {

        argNotNull(table, "table");
        String className = repositoryClassNameTranslator.translate(table);
        InterfaceSpec.Builder builder = InterfaceSpec.builder(className).addModifier(Modifier.PUBLIC);

        // insert
        builder.addMethod(insert(table));

        // insetSelective
        builder.addMethod(insertSelective(table));

        // insertMultiple
        builder.addMethod(insertMultipleWithChunk(table));
        builder.addMethod(insertMultiple(table));

        // insertBatch
//        builder.addMethod(insertBatchWithChunk(table));
//        builder.addMethod(insertBatch(table));

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
        builder.addMethod(count());
        builder.addMethod(exist());
        builder.addMethod(assertExist());
        builder.addMethod(assertExistWithThrow());
        builder.addMethod(existOne());
        builder.addMethod(assertExistOne());
        builder.addMethod(assertExistOneWithThrow());
        builder.addMethod(delete());
        builder.addMethod(update(table));

        return JavaFile.builder(repositoryClassPackageTranslator.translate(table), builder.build()).build();
    }

    protected MethodSpec insert(Table table) {
        return insertBuilder(table).build();
    }

    protected MethodSpec insertSelective(Table table) {
        return insertSelectiveBuilder(table).build();
    }

    protected MethodSpec insertMultipleWithChunk(Table table) {
        return insertMultipleWithChunkBuilder(table).build();
    }

    protected MethodSpec insertMultiple(Table table) {
        return insertMultipleBuilder(table).build();
    }

    protected MethodSpec deleteByPrimaryKey(PrimaryKey primaryKey) {
        return deleteByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec updateByPrimaryKey(PrimaryKey primaryKey) {
        return updateByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec getByPrimaryKey(PrimaryKey primaryKey) {
        return getByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec loadByPrimaryKey(PrimaryKey primaryKey) {
        return loadByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec loadWithThrowByPrimaryKey(PrimaryKey primaryKey) {
        return loadWithThrowByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec existByPrimaryKey(PrimaryKey primaryKey) {
        return existByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec assertExistByPrimaryKey(PrimaryKey primaryKey) {
        return assertExistByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec assertExistWithThrowByPrimaryKey(PrimaryKey primaryKey) {
        return assertExistWithThrowByPrimaryKeyBuilder(primaryKey).build();
    }

    protected MethodSpec queryOne(Table table) {
        return queryOneBuild(table).build();
    }

    protected MethodSpec queryMany(Table table) {
        return queryManyBuild(table).build();
    }

    protected MethodSpec cursor(Table table) {
        return cursorBuild(table).build();
    }

    protected MethodSpec loadOne(Table table) {
        return loadOneBuild(table).build();
    }

    protected MethodSpec loadOneWithThrow(Table table) {
        return loadOneWithThrowBuild(table).build();
    }

    protected MethodSpec count() {
        return countBuild().build();
    }

    protected MethodSpec exist() {
        return existBuild().build();
    }

    protected MethodSpec assertExist() {
        return assertExistBuild().build();
    }

    protected MethodSpec assertExistWithThrow() {
        return assertExistWithThrowBuild().build();
    }

    protected MethodSpec existOne() {
        return existOneBuild().build();
    }

    protected MethodSpec assertExistOne() {
        return assertExistOneBuild().build();
    }

    protected MethodSpec assertExistOneWithThrow() {
        return assertExistOneWithThrowBuild().build();
    }

    protected MethodSpec delete() {
        return deleteBuild().build();
    }

    protected MethodSpec update(Table table) {
        return updateBuild(table).build();
    }
}
