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
import glz.hawk.jdesigner.spec.base.Model;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.IndexColumn;
import glz.hawk.jdesigner.spec.database.PrimaryKey;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.jdesigner.translator.database.TableHelper;
import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawk.j4sql.mybatis.sql.MybatisParam;
import glz.hawk.j4sql.mybatis.sql.provider.AbstractSqlProvider;
import glz.hawk.j4sql.mybatis.statement.GeneralStatementProvider;
import glz.hawk.j4sql.mybatis.statement.InsertStatementProvider;
import glz.hawk.j4sql.mybatis.statement.MultiRowsInsertStatementProvider;
import glz.hawk.j4sql.mybatis.statement.UpdateStatementProvider;
import glz.hawk.j4sql.mybatis.statement.impl.GeneralStatementProviderImpl;
import glz.hawk.j4sql.mybatis.statement.impl.InsertStatementProviderImpl;
import glz.hawk.j4sql.mybatis.statement.impl.MultiRowsInsertStatementProviderImpl;
import glz.hawk.j4sql.mybatis.statement.impl.UpdateStatementProviderImpl;
import glz.hawk.j4sql.mybatis.writer.MybatisBuilderContext;
import glz.hawk.j4sql.mybatis.writer.MybatisBuilderContextImpl;
import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.dsl.delete.Delete;
import glz.hawk.j4sql.dsl.insert.Insert;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.update.Update;
import glz.hawk.j4sql.support.SqlBuilder;
import glz.hawk.j4sql.support.impl.DSL;
import glz.hawk.j4sql.support.impl.DefaultAliasedNamedColumn;
import glz.hawk.j4sql.util.QueryWrapper;
import glz.hawkframework.core.support.ArgumentSupport;
import glz.hawkframework.core.support.LogicSupport;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import glz.hawk.codepoet.java.type.TypeName;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToSqlProvider extends AbstractTableToSupport implements Translator<Table, JavaFile> {
    protected final Translator<Column, String> columnToParamNameTranslator;
    protected final Translator<Column, TypeName> columnToTypeNameTranslator;
    private final Translator<Model, String> sqlProviderClassPackageTranslator;
    private final Translator<Table, String> sqlProviderClassNameTranslator;
    private final Translator<Column, String> getterNameTranslator;
    private final Translator<Column, String> getterUpdatedNameTranslator;

    public TableToSqlProvider(Translator<Model, String> sqlProviderClassPackageTranslator, Translator<Table, String> sqlProviderClassNameTranslator,
                              Translator<Model, String> supportClassPackageTranslator, Translator<Table, String> supportClassNameTranslator,
                              Translator<Model, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator,
                              Translator<Column, String> columnToParamNameTranslator, Translator<Column, TypeName> columnToTypeNameTranslator,
                              Translator<Model, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator,
                              Translator<Table, String> columnUpdateClassNameTranslator, Translator<Column, String> getterNameTranslator,
                              Translator<Column, String> getterUpdatedNameTranslator) {
        super(supportClassPackageTranslator, supportClassNameTranslator, poClassPackageTranslator, poClassNameTranslator, updateClassPackageTranslator, updateClassNameTranslator, columnUpdateClassNameTranslator);
        this.sqlProviderClassPackageTranslator = argNotNull(sqlProviderClassPackageTranslator, "sqlProviderClassPackageTranslator");
        this.sqlProviderClassNameTranslator = argNotNull(sqlProviderClassNameTranslator, "sqlProviderClassNameTranslator");
        this.columnToParamNameTranslator = argNotNull(columnToParamNameTranslator, "columnToParamNameTranslator");
        this.columnToTypeNameTranslator = argNotNull(columnToTypeNameTranslator, "columnToTypeNameTranslator");
        this.getterNameTranslator = argNotNull(getterNameTranslator, "getterNameTranslator");
        this.getterUpdatedNameTranslator = argNotNull(getterUpdatedNameTranslator, "getterUpdatedNameTranslator");
    }

    @Nonnull
    @Override
    public JavaFile translate(@Nonnull Table table) {
        argNotNull(table, "table");
        String className = sqlProviderClassNameTranslator.translate(table);
        ClassSpec.Builder builder = ClassSpec.builder(className).addModifier(Modifier.PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Component.class).build())
            .setSuperClass(AbstractSqlProvider.class)
            .addSuperInterface(ProviderMethodResolver.class);


        // generate constructors
        builder.addConstructor(constructor());

        // generate methods
        // insert
        builder.addMethod(buildInsert(table));
        builder.addMethod(insert(table));

        // insertSelective
        builder.addMethod(buildInsertSelective(table));
        builder.addMethod(insertSelective(table));

        // insertMultiple
        builder.addMethod(buildInsertMultiple(table));
        builder.addMethod(insertMultiple(table));

        // deleteByPrimaryKey
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(buildDeleteByPrimaryKey(pk)));
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(deleteByPrimaryKey(pk)));

        // updateByPrimaryKey
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(buildUpdateByPrimaryKey(pk)));
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(updateByPrimaryKey(pk)));

        // selectByPrimaryKey
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(buildSelectByPrimaryKey(pk)));
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(selectByPrimaryKey(pk)));

        // countByPrimaryKey
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(buildCountByPrimaryKey(pk)));
        table.getPrimaryKey().ifPresent(pk -> builder.addMethod(countByPrimaryKey(pk)));

        // common
        builder.addMethod(buildSelectOrCountDynamic(table));
        builder.addMethod(selectOrCountDynamic());

        builder.addMethod(buildDeleteDynamic(table));
        builder.addMethod(deleteDynamic());

        builder.addMethod(buildUpdateDynamic(table));
        builder.addMethod(updateDynamic(table));

        // static imports
        builder.addStaticImport(ArgumentSupport.class, "*")
            .addStaticImport(MybatisBuilderContextImpl.class, "*")
            .addStaticImport(supportClassName(table), "*")
            .addStaticImport(DSL.class, "*")
            .addStaticImport(LogicSupport.class, "*");


        return JavaFile.builder(sqlProviderClassPackageTranslator.translate(table), builder.build()).build();
    }

    protected String fieldNameSqlBuilder() {
        return "sqlBuilder";
    }

    protected ConstructorSpec constructor() {
        final String fieldNameSqlBuilder = fieldNameSqlBuilder();
        return ConstructorSpec.builder(Modifier.PUBLIC)
            .addParameter(ParameterizedTypeName.of(SqlBuilder.class, MybatisBuilderContext.class), fieldNameSqlBuilder)
            .beginConstructorBody()
            .addStatement("super($L)", fieldNameSqlBuilder)
            .end()
            .build();
    }


    protected ParameterizedTypeName insertStatementProviderClassName(Table table) {
        return ParameterizedTypeName.of(InsertStatementProvider.class, poClassName(table));
    }

    protected ParameterizedTypeName multiRowsInsertStatementProvider(Table table) {
        return ParameterizedTypeName.of(MultiRowsInsertStatementProvider.class, poClassName(table));
    }

    protected MethodSpec buildInsert(Table table) {
        return MethodSpec.builder(Insert.class, "buildInsert", Modifier.PROTECTED)
            .beginMethodBody()
            .addStatement("return insertInto($L, COLUMNS).values($T.stream(PARAM_COLUMNS).map(p->p.prefix($T.PARAM_PREFIX)).toArray()).build()", fieldTableName(table), Arrays.class, InsertStatementProvider.class)
            .end()
            .build();
    }

    protected MethodSpec insert(Table table) {
        String insertSqlLocalParamName = "insertSql";
        String keyLocalParamName = "key";
        String poParamName = poParamName(table);
        return MethodSpec.builder(insertStatementProviderClassName(table), "insert", Modifier.PUBLIC)
            .addParameter(poClassName(table), poParamName)
            .beginMethodBody()
            .addStatement("String $L = buildKey(\"insert\", $T.class)", keyLocalParamName, poClassName(table))
            .addStatement("String $L = sqlCache.computeIfAbsent($L, k -> $L.build(buildInsert(), DUMMY_INSTANCE))", insertSqlLocalParamName, keyLocalParamName, fieldNameSqlBuilder())
            .addStatement("return $T.builder($L, $L).build()", InsertStatementProviderImpl.class, insertSqlLocalParamName, poParamName)
            .end()
            .build();
    }

    protected MethodSpec buildInsertSelective(Table table) {
        return MethodSpec.builder(Insert.class, "buildInsertSelective", Modifier.PROTECTED)
            .addParameter(poClassName(table), poParamName(table))
            .beginMethodBody()
            .addStatement("$T<$T> columns = new $T<>()", List.class, DefaultAliasedNamedColumn.class, ArrayList.class)
            .addStatement("$T<$T> params = new $T<>()", List.class, MybatisParam.class, ArrayList.class)
            .addCode(m -> {
                for (Column column : table.getColumns()) {
                    String getterName = getterNameTranslator.translate(column);
                    m.addCode("if ($L.$L() != null) {columns.add($L); params.add($L.prefix($T.PARAM_PREFIX));}", poParamName(table), getterName, fieldColumnName(column), paramColumnName(column), InsertStatementProvider.class);
                    m.addNewLine();
                }
            }).addStatement("return insertInto($L, columns.toArray(new $T[0])).values(params.toArray(new $T[0])).build()", fieldTableName(table), DefaultAliasedNamedColumn.class, MybatisParam.class)
            .end()
            .build();
    }

    protected MethodSpec insertSelective(Table table) {
        String poParamName = poParamName(table);
        String localParamNameOfInsertSql = "insertSql";
        return MethodSpec.builder(insertStatementProviderClassName(table), "insertSelective", Modifier.PUBLIC)
            .addParameter(poClassName(table), poParamName)
            .beginMethodBody()
            .addStatement("String $L = $L.build(buildInsertSelective($L), DUMMY_INSTANCE)", localParamNameOfInsertSql, fieldNameSqlBuilder(), poParamName)
            .addStatement("return $T.builder($L, $L).build()", InsertStatementProviderImpl.class, localParamNameOfInsertSql, poParamName)
            .end()
            .build();
    }

    protected MethodSpec buildInsertMultiple(Table table) {
        return MethodSpec.builder(Insert.class, "buildInsertMultiple", Modifier.PROTECTED)
            .addParameter(posClassName(table), posParamName(table))
            .beginMethodBody()
            .addStatement("$T[][] paramses = new $T[$L.size()][$L]", MybatisParam.class, MybatisParam.class, posParamName(table), table.getColumns().length)
            .beginFor("int i = 0; i < $L.size(); i++", posParamName(table))
            .addCode(f1 -> {
                f1.addStatement("$T[] params = new $T[$L]", MybatisParam.class, MybatisParam.class, table.getColumns().length);
                for (int j = 0; j < table.getColumns().length; j++) {
                    String paramColumnName = String.format("PARAM_%S", table.getColumns()[j].getName());
                    f1.addStatement("params[$L] = $T.builder(String.format(\"%s[%d].%s\", $T.PARAM_PREFIX, i, $L.getParamName()) , $L.getJdbcType()).build()", j, MybatisParam.class, MultiRowsInsertStatementProvider.class, paramColumnName, paramColumnName);
                }
            })
            .addStatement("paramses[i] = params")
            .endFor()
            .addStatement("return insertInto($L, COLUMNS).valueses(paramses).build()", fieldTableName(table))
            .end()
            .build();
    }

    protected MethodSpec insertMultiple(Table table) {
        final String posParamName = posParamName(table);
        String localParamNameOfInsertSql = "insertSql";
        return MethodSpec.builder(multiRowsInsertStatementProvider(table), "insertMultiple", Modifier.PUBLIC)
            .addParameter(ParameterizedTypeName.of(List.class, poClassName(table)), posParamName)
            .beginMethodBody()
            .addStatement("String $L = $L.build(buildInsertMultiple($L), DUMMY_INSTANCE)", localParamNameOfInsertSql, fieldNameSqlBuilder(), posParamName)
            .addStatement("return $T.builder($L, $L).build()", MultiRowsInsertStatementProviderImpl.class, localParamNameOfInsertSql, posParamName)
            .end()
            .build();
    }

    protected MethodSpec buildDeleteByPrimaryKey(PrimaryKey primaryKey) {
        return MethodSpec.builder(Delete.class, "buildDeleteByPrimaryKey", Modifier.PUBLIC, Modifier.STATIC)
            .beginMethodBody()
            .addCode(c -> {
                c.addCode("return deleteFrom($L).where(", fieldTableName(primaryKey.getOwner()));
                Column column = primaryKey.getIndexColumns()[0].getColumn();
                c.addCode("$L.eq($L.prefix($T.PARAM_PREFIX))", fieldColumnName(column), paramColumnName(column), GeneralStatementProvider.class);
                for (int i = 1; i < primaryKey.getIndexColumns().length; i++) {
                    column = primaryKey.getIndexColumns()[i].getColumn();
                    c.addCode(", and($L.eq($L.prefix($T.PARAM_PREFIX)))", fieldColumnName(column), paramColumnName(column), GeneralStatementProvider.class);
                }
                TableHelper.findRecordVersionColumn(primaryKey.getOwner()).ifPresent(col -> c.addCode(", and($L.eq($L.prefix($T.PARAM_PREFIX)))", fieldColumnName(col), paramColumnName(col), GeneralStatementProvider.class));
                c.addCode(").build();");
                c.addNewLine();
            })
            .end()
            .build();
    }

    protected MethodSpec deleteByPrimaryKey(PrimaryKey primaryKey) {
        String deleteSqlLocalParamName = "deleteSql";
        Optional<Column> recordVersionColumnOptional = TableHelper.findRecordVersionColumn(primaryKey.getOwner());
        return MethodSpec.builder(GeneralStatementProvider.class, "deleteByPrimaryKey", Modifier.PUBLIC)
            .add(b -> Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .add(b -> recordVersionColumnOptional.ifPresent(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .beginMethodBody()
            .addCode(b -> {
                List<TypeName> typeNameList = new ArrayList<>();
                Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> typeNameList.add(columnToTypeNameTranslator.translate(c)));
                recordVersionColumnOptional.ifPresent(c -> typeNameList.add(columnToTypeNameTranslator.translate(c)));
                StringBuilder sb = new StringBuilder();
                sb.append("String key = buildKey(\"deleteByPrimaryKey\"");
                typeNameList.forEach(t -> sb.append(", ").append("$T.class"));
                sb.append(")");
                b.addStatement(sb.toString(), typeNameList.toArray());
            })
            .addStatement("String $L = sqlCache.computeIfAbsent(key, k -> $L.build(buildDeleteByPrimaryKey(), DUMMY_INSTANCE))", deleteSqlLocalParamName, fieldNameSqlBuilder())
            .addCode(cb -> {
                cb.addCode("return $T.builder($L)", GeneralStatementProviderImpl.class, deleteSqlLocalParamName);
                Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    cb.addCode(".addParam($S, argNotNull($L, $S))", columnParamName, columnParamName, columnParamName);
                });
                recordVersionColumnOptional.ifPresent(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    cb.addCode(".addParam($S, argNotNull($L, $S))", columnParamName, columnParamName, columnParamName);
                });
                cb.addCode(".build();");
                cb.addNewLine();
            })
            .end()
            .build();
    }

    protected MethodSpec buildUpdateByPrimaryKey(PrimaryKey primaryKey) {
        Table table = primaryKey.getOwner();
        return MethodSpec.builder(Update.class, "buildUpdateByPrimaryKey", Modifier.PROTECTED)
            .addParameter(updateClassName(table), updateParamName(table))
            .beginMethodBody()
            .addCode(c -> {
                c.addCode("return update($L)", fieldTableName(table)).addNewLine().addIndent();
                Arrays.stream(table.getColumns()).forEach(column -> c.addCode(".set(c -> consumeIfTrue($L::$L, () -> c.set($L, $L.prefix($T.UPDATE_PARAM_PREFIX))))", updateParamName(table), getterUpdatedNameTranslator.translate(column), fieldColumnName(column), paramColumnName(column), UpdateStatementProvider.class).addNewLine());
                c.addCode(".where(");
                Column column = primaryKey.getIndexColumns()[0].getColumn();
                c.addCode("$L.eq($L.prefix($T.UPDATE_CONDITION_PREFIX))", fieldColumnName(column), paramColumnName(column), UpdateStatementProvider.class);
                for (int i = 1; i < primaryKey.getIndexColumns().length; i++) {
                    column = primaryKey.getIndexColumns()[i].getColumn();
                    c.addCode(", and($L.eq($L.prefix($T.UPDATE_CONDITION_PREFIX)))", fieldColumnName(column), paramColumnName(column), UpdateStatementProvider.class);
                }
                TableHelper.findRecordVersionColumn(primaryKey.getOwner()).ifPresent(col -> c.addCode(", and($L.eq($L.prefix($T.UPDATE_CONDITION_PREFIX)))", fieldColumnName(col), paramColumnName(col), UpdateStatementProvider.class));
                c.addCode(")").addNewLine().addCode(".build();").addNewLine().removeIndent();
            })
            .end()
            .build();
    }

    protected MethodSpec updateByPrimaryKey(PrimaryKey primaryKey) {
        String localParameterNameOfUpdateSql = "updateSql";
        Table table = primaryKey.getOwner();
        String updateParam = updateParamName(table);
        Optional<Column> recordVersionColumnOptional = TableHelper.findRecordVersionColumn(primaryKey.getOwner());
        return MethodSpec.builder(UpdateStatementProvider.class, "updateByPrimaryKey", Modifier.PUBLIC)
            .addParameter(updateClassName(table), updateParam)
            .add(b -> Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .add(b -> recordVersionColumnOptional.ifPresent(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c))))
            .beginMethodBody()
            .addStatement("String $L = $L.build(buildUpdateByPrimaryKey($L), DUMMY_INSTANCE)", localParameterNameOfUpdateSql, fieldNameSqlBuilder(), updateParam)
            .addCode(cb -> {
                cb.addCode("return $T.builder($L, $L)", UpdateStatementProviderImpl.class, localParameterNameOfUpdateSql, updateParam);
                Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    cb.addCode(".addParam($S, argNotNull($L, $S))", columnParamName, columnParamName, columnParamName);
                });
                recordVersionColumnOptional.ifPresent(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    cb.addCode(".addParam($S, argNotNull($L, $S))", columnParamName, columnParamName, columnParamName);
                });
                cb.addCode(".build();");
                cb.addNewLine();
            })
            .end()
            .build();
    }

    protected MethodSpec buildSelectByPrimaryKey(PrimaryKey primaryKey) {
        return MethodSpec.builder(Select.class, "buildSelectByPrimaryKey", Modifier.PROTECTED)
            .beginMethodBody()
            .addCode(c -> {
                c.addCode("return select($L).from($L).where(", fieldColumnsName(primaryKey.getOwner()), fieldTableName(primaryKey.getOwner()));
                Column column = primaryKey.getIndexColumns()[0].getColumn();
                c.addCode("$L.eq($L.prefix($T.PARAM_PREFIX))", fieldColumnName(column), paramColumnName(column), GeneralStatementProvider.class);
                for (int i = 1; i < primaryKey.getIndexColumns().length; i++) {
                    column = primaryKey.getIndexColumns()[i].getColumn();
                    c.addCode(", and($L.eq($L.prefix($T.PARAM_PREFIX)))", fieldColumnName(column), paramColumnName(column), GeneralStatementProvider.class);
                }
                c.addCode(").build();");
                c.addNewLine();
            })
            .end()
            .build();
    }

    protected MethodSpec selectByPrimaryKey(PrimaryKey primaryKey) {
        String localParamNameOfDeleteSql = "selectSql";
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        return MethodSpec.builder(GeneralStatementProvider.class, "selectByPrimaryKey", Modifier.PUBLIC)
            .add(b -> columnList.forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c)))).beginMethodBody()
            .addCode(b -> {
                StringBuilder format = new StringBuilder();
                format.append("String key = buildKey(\"selectByPrimaryKey\"");
                columnList.forEach(c -> format.append(", $T.class"));
                format.append(")");
                b.addStatement(format.toString(), columnList.stream().map(columnToTypeNameTranslator::translate).toArray());
            })
            .addStatement("String $L = sqlCache.computeIfAbsent(key, k -> $L.build(buildSelectByPrimaryKey(), DUMMY_INSTANCE))", localParamNameOfDeleteSql, fieldNameSqlBuilder())
            .addCode(cb -> {
                cb.addCode("return $T.builder($L)", GeneralStatementProviderImpl.class, localParamNameOfDeleteSql);
                Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    cb.addCode(".addParam($S, argNotNull($L, $S))", columnParamName, columnParamName, columnParamName);
                });
                cb.addCode(".build();");
                cb.addNewLine();
            })
            .end()
            .build();
    }

    protected MethodSpec buildCountByPrimaryKey(PrimaryKey primaryKey) {
        return MethodSpec.builder(Select.class, "buildCuntByPrimaryKey", Modifier.PROTECTED)
            .beginMethodBody()
            .addCode(c -> {
                c.addCode("return selectCount().from($L).where(", fieldTableName(primaryKey.getOwner()));
                Column column = primaryKey.getIndexColumns()[0].getColumn();
                c.addCode("$L.eq($L.prefix($T.PARAM_PREFIX))", fieldColumnName(column), paramColumnName(column), GeneralStatementProvider.class);
                for (int i = 1; i < primaryKey.getIndexColumns().length; i++) {
                    column = primaryKey.getIndexColumns()[i].getColumn();
                    c.addCode(", and($L.eq($L.prefix($T.PARAM_PREFIX)))", fieldColumnName(column), paramColumnName(column), GeneralStatementProvider.class);
                }
                c.addCode(").build();");
                c.addNewLine();
            })
            .end()
            .build();
    }

    protected MethodSpec countByPrimaryKey(PrimaryKey primaryKey) {
        String localParamNameOfDeleteSql = "selectSql";
        List<Column> columnList = Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).collect(Collectors.toList());
        return MethodSpec.builder(GeneralStatementProvider.class, "countByPrimaryKey", Modifier.PUBLIC)
            .add(b -> Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> b.addParameter(columnToTypeNameTranslator.translate(c), columnToParamNameTranslator.translate(c)))).beginMethodBody()
            .addCode(b -> {
                StringBuilder format = new StringBuilder();
                format.append("String key = buildKey(\"countByPrimaryKey\"");
                columnList.forEach(c -> format.append(", $T.class"));
                format.append(")");
                b.addStatement(format.toString(), columnList.stream().map(columnToTypeNameTranslator::translate).toArray());
            })
            .addStatement("String $L = sqlCache.computeIfAbsent(key, k -> $L.build(buildCuntByPrimaryKey(), DUMMY_INSTANCE))", localParamNameOfDeleteSql, fieldNameSqlBuilder())
            .addCode(cb -> {
                cb.addCode("return $T.builder($L)", GeneralStatementProviderImpl.class, localParamNameOfDeleteSql);
                Arrays.stream(primaryKey.getIndexColumns()).map(IndexColumn::getColumn).forEach(c -> {
                    String columnParamName = columnToParamNameTranslator.translate(c);
                    cb.addCode(".addParam($S, argNotNull($L, $S))", columnParamName, columnParamName, columnParamName);
                });
                cb.addCode(".build();");
                cb.addNewLine();
            })
            .end()
            .build();
    }


    protected MethodSpec buildSelectOrCountDynamic(Table table) {
        String queryWrapperParamName = "queryWrapper";
        return MethodSpec.builder(Select.class, "buildSelectOrCountDynamic", Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(QueryWrapper.class, queryWrapperParamName)
            .beginMethodBody()
            .addCode(c -> {
                c.addCode("return select($L.isCount(), $L.isDistinct(), $T.isEmpty($L.getColumns()) ? $L : $L.getColumns())", queryWrapperParamName, queryWrapperParamName, ObjectHelper.class, queryWrapperParamName, fieldColumnsName(), queryWrapperParamName).addNewLine().addIndent();
                c.addCode(".from($L)", fieldTableName(table)).addNewLine();
                c.addCode(".where($L.getCondition())", queryWrapperParamName).addNewLine();
                c.addCode(".orderBy($T.isNotEmpty($L.getOrderColumns()), $L.getOrderColumns())", ObjectHelper.class, queryWrapperParamName, queryWrapperParamName).addNewLine();
                c.addCode(".limit($L.getLimit() != null && !$L.isCount(), $L.getLimit())", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName).addNewLine();
                c.addCode(".offset($L.getOffset() != null && !$L.isCount(), $L.getOffset())", queryWrapperParamName, queryWrapperParamName, queryWrapperParamName).addNewLine();
                c.addCode(".forUpdate($L.isForUpdate() && !$L.isCount())", queryWrapperParamName, queryWrapperParamName).addNewLine();
                c.addCode(".build();").addNewLine().removeIndent();
            })
            .end()
            .build();
    }

    protected MethodSpec selectOrCountDynamic() {
        String queryWrapperParamName = "queryWrapper";
        return MethodSpec.builder(GeneralStatementProvider.class, "selectOrCountDynamic", Modifier.PUBLIC)
            .addParameter(QueryWrapper.class, queryWrapperParamName)
            .beginMethodBody()
            .addStatement("return generalSelect(buildSelectOrCountDynamic($L))", queryWrapperParamName)
            .end()
            .build();
    }

    protected MethodSpec buildDeleteDynamic(Table table) {
        String conditionParamName = "condition";
        return MethodSpec.builder(Delete.class, "buildDeleteDynamic", Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(Condition.class, conditionParamName)
            .beginMethodBody()
            .addStatement("return deleteFrom($L).where($L).build()", fieldTableName(table), conditionParamName)
            .end()
            .build();
    }

    protected MethodSpec deleteDynamic() {
        String conditionParamName = "condition";
        return MethodSpec.builder(GeneralStatementProvider.class, "deleteDynamic", Modifier.PUBLIC)
            .addParameter(Condition.class, conditionParamName)
            .beginMethodBody()
            .addStatement("return generalDelete(buildDeleteDynamic($L))", conditionParamName)
            .end()
            .build();
    }

    protected MethodSpec buildUpdateDynamic(Table table) {
        String conditionParamName = "condition";
        String columnUpdateParamName = columnUpdateParamName(table);
        return MethodSpec.builder(Update.class, "buildUpdateDynamic", Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(columnUpdateClassName(table), columnUpdateParamName)
            .addParameter(Condition.class, conditionParamName)
            .beginMethodBody()
            .addCode(c -> {
                c.addCode("return update($L)", fieldTableName(table)).addNewLine().addIndent();
                Arrays.stream(table.getColumns()).forEach(column -> c.addCode(".set(c -> consumeIfTrue($L::$L, () -> c.set($L, $L.$L())))", columnUpdateParamName, getterUpdatedNameTranslator.translate(column), fieldColumnName(column), columnUpdateParamName, getterNameTranslator.translate(column)).addNewLine());
                c.addCode(".where($L)", conditionParamName).addNewLine();
                c.addCode(".build();").addNewLine().removeIndent();
            })
            .end()
            .build();
    }

    protected MethodSpec updateDynamic(Table table) {
        String conditionParamName = "condition";
        String columnUpdateParamName = columnUpdateParamName(table);
        return MethodSpec.builder(GeneralStatementProvider.class, "updateDynamic", Modifier.PUBLIC)
            .addParameter(columnUpdateClassName(table), columnUpdateParamName)
            .addParameter(Condition.class, conditionParamName)
            .beginMethodBody()
            .addStatement("return generalUpdate(buildUpdateDynamic($L, $L))", columnUpdateParamName, conditionParamName)
            .end()
            .build();
    }

}
