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
import glz.hawk.codepoet.java.*;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.jdesigner.spec.database.*;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.core.helper.MapHelper;
import glz.hawkframework.core.support.ArgumentSupport;
import glz.hawkframework.dao.context.DefaultRepositoryContext;
import glz.hawkframework.dao.context.SqlMethod;
import glz.hawkframework.dao.process.InsertParameter;
import glz.hawkframework.dao.process.UpdateParameter;
import glz.hawkframework.dao.process.UpdateProcessor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToRepositoryImpl extends TableToAbstractRepository implements Translator<Table, JavaFile> {

    protected final Translator<Table, String> repositoryImplClassPackageTranslator;

    protected final Translator<Table, String> repositoryImplClassNameTranslator;

    public TableToRepositoryImpl(Translator<Table, String> repositoryClassPackageTranslator,
                                 Translator<Table, String> repositoryClassNameTranslator,
                                 Translator<Table, String> abstractRepositoryPackageTranslator,
                                 Translator<Table, String> abstractRepositoryClassNameTranslator,
                                 Translator<Table, String> mapperClassPackageTranslator,
                                 Translator<Table, String> mapperClassNameTranslator,
                                 Translator<Table, String> sqlProviderClassPackageTranslator,
                                 Translator<Table, String> sqlProviderClassNameTranslator,
                                 Translator<Table, String> poClassPackageTranslator,
                                 Translator<Table, String> poClassNameTranslator,
                                 Translator<Column, String> columnToParamNameTranslator,
                                 Translator<Column, TypeName> dataTypeToTypeNameTranslator,
                                 Translator<Table, String> updateClassPackageTranslator,
                                 Translator<Table, String> updateClassNameTranslator,
                                 Translator<Table, String> columnUpdateClassNameTranslator,
                                 Translator<Table, String> repositoryImplClassPackageTranslator,
                                 Translator<Table, String> repositoryImplClassNameTranslator,
                                 ColumnFinder recordVersionColumnFinder,
                                 boolean supportColumnInsertOrUpdate
    ) {
        super(repositoryClassPackageTranslator, repositoryClassNameTranslator, abstractRepositoryPackageTranslator, abstractRepositoryClassNameTranslator,
            mapperClassPackageTranslator, mapperClassNameTranslator, sqlProviderClassPackageTranslator, sqlProviderClassNameTranslator,
            poClassPackageTranslator, poClassNameTranslator, columnToParamNameTranslator, dataTypeToTypeNameTranslator, updateClassPackageTranslator, updateClassNameTranslator, columnUpdateClassNameTranslator, recordVersionColumnFinder, supportColumnInsertOrUpdate);
        this.repositoryImplClassPackageTranslator = argNotNull(repositoryImplClassPackageTranslator, "repositoryImplClassPackageTranslator");
        this.repositoryImplClassNameTranslator = argNotNull(repositoryImplClassNameTranslator, "repositoryImplClassNameTranslator");

    }

    @Nonnull
    @Override
    public JavaFile translate(@Nonnull Table table) {
        argNotNull(table, "table");
        String className = repositoryImplClassNameTranslator.translate(table);
        ClassSpec.Builder builder = ClassSpec.builder(className).addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Repository.class).build())
            .setSuperClass(ClassName.of(abstractRepositoryPackageTranslator.translate(table), abstractRepositoryClassNameTranslator.translate(table)))
            .addSuperInterface(ClassName.of(repositoryClassPackageTranslator.translate(table), repositoryClassNameTranslator.translate(table)));

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
        table.getPrimaryKey().map(this::deleteByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // updateByPrimaryKey
        table.getPrimaryKey().map(this::updateByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // getByPrimaryKey
        table.getPrimaryKey().map(this::getByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // loadByPrimaryKey
        table.getPrimaryKey().map(this::loadByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // loadByPrimaryKeyWithThrow
        table.getPrimaryKey().map(this::loadWithThrowByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // existByPrimaryKey
        table.getPrimaryKey().map(this::existByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // assertExistByPrimaryKey
        table.getPrimaryKey().map(this::assertExistByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // assertExistWithThrowByPrimaryKey
        table.getPrimaryKey().map(this::assertExistWithThrowByPrimaryKeyOrUniqueIndex).ifPresent(builder::addMethod);

        // unique index
        // delete
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::deleteByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // update
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::updateByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // get
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::getByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // load
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::loadByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // load with throw
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::loadWithThrowByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // exist
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::existByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // assert exist
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::assertExistByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);
        // assert exist with throw
        Arrays.stream(table.getIndexes()).filter(Index::isUnique).map(this::assertExistWithThrowByPrimaryKeyOrUniqueIndex).forEach(builder::addMethod);

        // static import
        builder.addStaticImport(ArgumentSupport.class, "*")
            .addStaticImport(DefaultRepositoryContext.class, "*")
            .addStaticImport(SqlMethod.class, "*")
            .addStaticImport(MapHelper.class, "*")
            .addStaticImport(UpdateProcessor.class, "*");

        return JavaFile.builder(repositoryImplClassPackageTranslator.translate(table), builder.build()).build();
    }

    protected ConstructorSpec constructor(Table table) {
        String mapperParamName = mapperFieldName(table);
        String sqlProviderParamName = sqlProviderFieldName(table);
        return ConstructorSpec.builder(PUBLIC)
            .addParameter(ParameterSpec.builder(mapperClassName(table), mapperParamName).build())
            .addParameter(ParameterSpec.builder(sqlProviderClassName(table), sqlProviderParamName).build())
            .beginConstructorBody()
            .addStatement("super($L, $L)", mapperFieldName(table), sqlProviderFieldName(table))
            .end()
            .build();
    }

    protected MethodSpec insert(Table table) {
        String mapperFieldName = mapperFieldName(table);
        String poParamName = poParamName(table);
        String insertParameterParamName = "insertParameter";
        return insertBuilder(table)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addStatement("$T $L = $T.builder().setInsertObject(argNotNull($L, $S)).build()", InsertParameter.class, insertParameterParamName, InsertParameter.class, poParamName, poParamName)
            .addStatement("$L($L)", beforeInsertMethodName(), insertParameterParamName)
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
            .addStatement("$L($L)", afterInsertMethodName(), insertParameterParamName)
            .end()
            .build();
    }

    protected MethodSpec insertSelective(Table table) {
        String mapperFieldName = mapperFieldName(table);
        String poParamName = poParamName(table);
        String insertParameterParamName = "insertParameter";
        return insertSelectiveBuilder(table)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addStatement("$T $L = $T.builder().setInsertObject(argNotNull($L, $S)).build()", InsertParameter.class, insertParameterParamName, InsertParameter.class, poParamName, poParamName)
            .addStatement("$L($L)", beforeInsertMethodName(), insertParameterParamName)
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
            .addStatement("$L($L)", afterInsertMethodName(), insertParameterParamName)
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
            .addStatement("$L.stream().map(po->$T.builder().setInsertObject(po).build()).forEach(this::$L)", chunkLocalParamName, InsertParameter.class, beforeInsertMethodName())
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
            .addStatement("$L.stream().map(po->$T.builder().setInsertObject(po).build()).forEach(this::$L)", chunkLocalParamName, InsertParameter.class, afterInsertMethodName())
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

    protected MethodSpec deleteByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "deleteByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "DELETE_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        recordVersionColumnFinder.find(table).ifPresent(columnList::add);
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return deleteByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .beginIf("$L.deleteGeneral($L.$L($L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters)
            .addCode(b -> {
                b.addStatement("$T builder = builder(getClass(), $S, DELETE)", DefaultRepositoryContext.Builder.class, deleteByPrimaryKeyOrUniqueIndexMethodName(indexSupport));
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

    protected MethodSpec updateByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "updateByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "UPDATE_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        recordVersionColumnFinder.find(indexSupport.getOwner()).ifPresent(columnList::add);
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        String updateParamName = updateParamName(table);
        String paramsLocalParamName = "updateParameter";
        return updateByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
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
                b.addCode("$T $L = $T.builder().setFieldUpdateObject($L)", UpdateParameter.class, paramsLocalParamName, UpdateParameter.class, updateParamName);
                recordVersionColumnFinder.find(indexSupport.getOwner()).ifPresent(column -> b.addCode(".setRecordVersion($L)", columnToParamNameTranslator.translate(column)));
                Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addCode(".addParam($S, $L)", columnToParamNameTranslator.translate(c), columnToParamNameTranslator.translate(c)));
                b.addCode(".build();").addNewLine();
            })
            .addStatement("$L($L)", beforeUpdateMethodName(), paramsLocalParamName)
            .beginIf("$L.update($L.$L($L, $L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, updateParamName((table)), parameters)
            .addCode(b -> {
                b.addStatement("Builder builder = builder(getClass(), $S, $T.UPDATE)", updateByPrimaryKeyOrUniqueIndexMethodName(indexSupport), SqlMethod.class);
                b.addStatement("builder.setPoClass($T.class)", poClassName(table));
                b.addStatement("builder.setDiscriminator(AFFECTED_ROWS_COUNT_IS_UNEXPECTED)");
                b.addStatement("builder.addParam($S,$L)", paramsLocalParamName, paramsLocalParamName);
                b.addStatement("throw $L.convertTo(builder.build())", exceptionConverterFieldName());
            })
            .endIf()
            .addStatement("$L($L)", afterUpdateMethodName(), paramsLocalParamName)
            .end()
            .build();
    }

    protected MethodSpec getByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        final String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "selectByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "SELECT_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
        return getByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .addStatement("return $L.selectOne($L.$L($L))", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters)
            .end()
            .build();
    }


    protected MethodSpec loadByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        final String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "selectByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "SELECT_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

        return loadByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .addCode(b -> {
                b.addCode("return $L.selectOne($L.$L($L))", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters).addNewLine();
                b.addIndent(2);
                b.addCode(".orElseThrow(() -> $L.convertTo(", exceptionConverterFieldName()).addNewLine();
                b.addIndent(2);
                b.addCode("builder(getClass(), $S, SELECT)", loadByPrimaryKeyOrUniqueIndexMethodName(indexSupport)).addNewLine();
                b.addIndent(2);
                b.addCode(".setPoClass($T.class)", poClassName(table)).addNewLine();
                b.addCode(".setDiscriminator(FOUND_NONE)").addNewLine();
                columnList.forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    b.addCode(".addParam($S, $L)", columnParamName, columnParamName).addNewLine();
                });
                b.addCode(".build()));").addNewLine();
                b.removeIndent(6);
            })
            .end()
            .build();
    }

    protected MethodSpec loadWithThrowByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        final String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "selectByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "SELECT_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());

        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return loadWithThrowByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
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
            .addStatement("return $L.selectOne($L.$L($L)).orElseThrow($L)", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters, exceptionSupplierParamName())
            .end()
            .build();
    }

    protected MethodSpec existByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        final String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "countByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "COUNT_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return existByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .addStatement("return $L.count($L.$L($L)) == 1", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters)
            .end()
            .build();
    }

    protected MethodSpec assertExistByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        final String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "countByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "COUNT_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return assertExistByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
            .addModifier(PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Override.class).build())
            .beginMethodBody()
            .addCode(b -> columnList.forEach(c -> {
                String columnParamName = columnToParamNameTranslator.translate(c);
                b.addStatement("argNotNull($L, $S)", columnParamName, columnParamName);
            }))
            .beginIf("$L.count($L.$L($L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters)
            .addCode(b -> {
                b.addStatement("Builder builder = builder(getClass(), $S, SELECT)", assertExistByPrimaryKeyOrUniqueIndexMethodName(indexSupport));
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

    protected MethodSpec assertExistWithThrowByPrimaryKeyOrUniqueIndex(IndexSupport<?> indexSupport) {
        Table table = indexSupport.getOwner();
        final String sqlProviderMethodName = indexSupport instanceof PrimaryKey ? "countByPrimaryKey" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "COUNT_BY_" + ((Index) indexSupport).getShortName().orElse(((Index) indexSupport).getName()).toUpperCase());
        List<Column> columnList = Arrays.stream(indexSupport.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        String parameters = columnList.stream().map(columnToParamNameTranslator::translate).collect(Collectors.joining(", "));
        return assertExistWithThrowByPrimaryKeyOrUniqueIndexBuilder(indexSupport)
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
            .beginIf("$L.count($L.$L($L)) != 1", mapperFieldName(table), sqlProviderFieldName(table), sqlProviderMethodName, parameters)
            .addStatement("throw $L.get()", exceptionSupplierParamName())
            .endIf()
            .end()
            .build();
    }


}
