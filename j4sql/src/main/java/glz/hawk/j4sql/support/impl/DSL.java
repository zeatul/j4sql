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

package glz.hawk.j4sql.support.impl;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.Connector;
import glz.hawk.j4sql.condition.impl.*;
import glz.hawk.j4sql.dsl.cases.after.AfterCaseClauseStep;
import glz.hawk.j4sql.dsl.delete.after.AfterDeleteDeleteFromClauseStep;
import glz.hawk.j4sql.dsl.insert.after.AfterInsertInsertIntoClauseStep;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.select.after.AfterSelectSelectClauseStep;
import glz.hawk.j4sql.dsl.update.after.AfterUpdateUpdateClauseStep;
import glz.hawk.j4sql.support.*;
import glz.hawk.j4sql.util.ConditionBuilder;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class DSL {

    /**
     * Null Value = {@code null}
     */
    public static final NullValue NULL = NullValue.INSTANCE;

    /**
     * Empty String = {@code ''}
     */
    public static final EmptyValue EMPTY = EmptyValue.INSTANCE;

    /**
     * This condition is {@code 1 = 1}
     */
    public static final Condition TRUE = DefaultScalarColumn.of(1).eq(1);

    /**
     * This condition is {@code 1 = -1}
     */
    public static final Condition FALSE = DefaultScalarColumn.of(1).eq(-1);

    public static Condition exists(Select select) {
        return ExistsCondition.of(select);
    }

    public static Condition not(Condition condition, ConnectCondition... connectConditions) {
        return NotCondition.of($c(condition, connectConditions).build());
    }

    public static IgnorableConnectCondition and(boolean acceptable, Supplier<Condition> conditionSupplier) {
        if (!acceptable) {
            return IgnorableConnectCondition.of(false, Connector.AND, conditionSupplier);
        } else {
            return IgnorableConnectCondition.of(true, Connector.AND, conditionSupplier);
        }
    }

    public static ConnectCondition and(Condition condition) {
        return DefaultConnectCondition.and(condition);
    }

    public static ConnectCondition and(Condition condition, ConnectCondition... connectConditions) {
        return DefaultConnectCondition.and($c(condition, connectConditions).build());
    }

    public static ConnectCondition and(Condition condition, IgnorableConnectCondition... ignorableConnectConditions) {
        return DefaultConnectCondition.and($c(condition, ignorableConnectConditions).build());
    }

    public static ConnectCondition and(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        return DefaultConnectCondition.and($c(ignorableCondition, ignorableConnectConditions).build());
    }

    public static IgnorableConnectCondition or(boolean acceptable, Supplier<Condition> conditionSupplier) {
        if (!acceptable) {
            return IgnorableConnectCondition.of(false, Connector.OR, conditionSupplier);
        } else {
            return IgnorableConnectCondition.of(true, Connector.OR, conditionSupplier);
        }
    }

    public static ConnectCondition or(Condition condition) {
        return DefaultConnectCondition.or(condition);
    }

    public static ConnectCondition or(Condition condition, ConnectCondition... connectConditions) {
        return DefaultConnectCondition.or($c(condition, connectConditions).build());
    }

    public static ConnectCondition or(Condition condition, IgnorableConnectCondition... ignorableConnectConditions) {
        return DefaultConnectCondition.or($c(condition, ignorableConnectConditions).build());
    }

    public static ConnectCondition or(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        return DefaultConnectCondition.or($c(ignorableCondition, ignorableConnectConditions).build());
    }

    /**
     * Obtains a Condition Builder
     *
     * @param condition         the required condition
     * @param connectConditions the required connect conditions
     * @return a Condition Builder
     */
    public static ConditionBuilder $c(Condition condition, ConnectCondition... connectConditions) {
        return ConditionBuilder.builder(condition, connectConditions);
    }

    /**
     * Obtains a Condition Builder
     *
     * @param condition the required condition
     * @return a Condition Builder
     */
    public static ConditionBuilder $c(Condition condition) {
        return ConditionBuilder.builder(condition);
    }

    /**
     * Obtains a Condition Builder
     *
     * @param condition                  the required condition
     * @param ignorableConnectConditions the required ignorable connect conditions
     * @return a Condition Builder
     */
    public static ConditionBuilder $c(Condition condition, IgnorableConnectCondition... ignorableConnectConditions) {
        return ConditionBuilder.builder(condition, ignorableConnectConditions);
    }

    /**
     * Obtains a Condition Builder
     *
     * @param ignorableCondition         the required ignorable condition
     * @param ignorableConnectConditions the required ignorable connect conditions
     * @return a Condition Builder
     */
    public static ConditionBuilder $c(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        return ConditionBuilder.builder(ignorableCondition, ignorableConnectConditions);
    }

    /**
     * Obtains an Ignorable Condition
     * <p>if the value of {@code acceptable} is {@code false}, the {@code condition} will be ignored.</p>
     *
     * @param acceptable        whether to accept connection。
     * @param conditionSupplier the required condition
     * @return an Ignorable Condition
     */
    public static IgnorableCondition $c(boolean acceptable, Supplier<Condition> conditionSupplier) {
        return IgnorableCondition.of(acceptable, conditionSupplier);
    }

    /**
     * Obtains an Ignorable Condition which acceptable is always {@code true}.
     *
     * @param condition the required conditions
     * @return an Ignorable Condition
     */
    public static IgnorableCondition t(Condition condition) {
        return IgnorableCondition.of(true, () -> condition);
    }

    /**
     * Obtains an Ignorable Connect Condition
     * <p>if the value of {@code acceptable} is {@code false}, the value of connectConditionSupplier will be ignored.</p>
     *
     * @param acceptable        whether to accept connectCondition
     * @param connector         the required connector
     * @param conditionSupplier the required condition supplier
     * @return an Ignorable Connect Condition
     */
    public static IgnorableConnectCondition $c(boolean acceptable, @Nonnull Connector connector, @Nonnull Supplier<Condition> conditionSupplier) {
        return IgnorableConnectCondition.of(acceptable, connector, conditionSupplier);
    }

    /**
     * Obtains an Ignorable Connect Condition which acceptable is always {@code true}.
     *
     * @param connectCondition the required connectCondition
     * @return an Ignorable Connect Condition
     */
    public static IgnorableConnectCondition t(ConnectCondition connectCondition) {
        return IgnorableConnectCondition.of(true, connectCondition.getConnector(), connectCondition::getCondition);
    }

    public static ValueColumn colv(Object value) {
        return new DefaultValueColumn(value);
    }

    /**
     * 空值ValueColumn，相当于{@code null}.
     * <p>ValueColumn不可以作为{@code select}子句的字段</p>
     */
    public static ValueColumn colNull() {
        return new DefaultValueColumn(NULL);
    }

    /**
     * 空字符串值ValueColumn，相当于{@code ''}
     * <p>ValueColumn不可以作为{@code select}子句的字段</p>
     */
    public static ValueColumn colEmpty() {
        return new DefaultValueColumn(EMPTY);
    }

    /**
     * 空值ScalarColumn，相当于{@code null}
     * <p>ScalarColumn可以作为{@code select}子句的字段</p>
     */
    public static ScalarColumn colsNull() {
        return DefaultScalarColumn.ofNull();
    }

    /**
     * 空字符串值ScalarColumn，相当于{@code ‘’}
     * <p>ScalarColumn可以作为{@code select}子句的字段</p>
     */
    public static ScalarColumn colsEmpty() {
        return DefaultScalarColumn.ofEmpty();
    }

    public static ScalarColumn cols(Long value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(long value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(Integer value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(int value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(String value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(Short value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(short value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(Byte value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(byte value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(LocalDateTime value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(LocalDate value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(LocalTime value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(BigDecimal value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(BigInteger value) {
        return DefaultScalarColumn.of(value);
    }

    public static ScalarColumn cols(Character value) {
        return DefaultScalarColumn.of(value);
    }

    /**
     * 生成一个字段名为{@code columnName}的字段
     */
    public static NamedColumn col(String columnName) {
        return new DefaultNamedColumn(columnName);
    }

    /**
     * 生成一个表名为{@code tableName}，字段名为{@code columnName}的字段
     */
    public static NamedColumn col(String tableName, String columnName) {
        return new DefaultNamedColumn(tableName, columnName);
    }

    /**
     * 生成一个表名为{@code tableName}，字段名为{@code columnName}，别名为{@code aliasName}的字段
     */
    public static AliasedSelectColumn<NamedColumn> col(String tableName, String columnName, String aliasName) {
        return new DefaultNamedColumn(tableName, columnName).as(aliasName);
    }

    /**
     * 生成一个表名为{@code tableName}，字段名来自于{@code namedColumn}的字段
     */
    public static NamedColumn col(String tableName, NamedColumn namedColumn) {
        return new DefaultNamedColumn(tableName, namedColumn.getColumnName());
    }

    /**
     * 生成一个表名为{@code tableName}，字段名来自于{@code namedColumn}，别名为{@code aliasName}的字段
     */
    public static AliasedSelectColumn<NamedColumn> col(String tableName, NamedColumn namedColumn, String aliasName) {
        return new DefaultNamedColumn(tableName, namedColumn.getColumnName()).as(aliasName);
    }

    /**
     * 生成一个表名全部为{@code tableName，字段名来自于{@code namedColumns}的字段数组
     */
    public static NamedColumn[] cola(String tableName, NamedColumn... namedColumns) {
        NamedColumn[] result = new NamedColumn[namedColumns.length];
        for (int i = 0; i < namedColumns.length; i++) {
            result[i] = new DefaultNamedColumn(tableName, namedColumns[i].getColumnName());
        }
        return result;
    }

    /**
     * 生成一个表名为{@code tableName，字段名来自于{@code aliasedSelectColumn}的字段，不保留别名
     */
    public static NamedColumn col(String tableName, AliasedSelectColumn<NamedColumn> aliasedSelectColumn) {
        return new DefaultNamedColumn(tableName, aliasedSelectColumn.getSourceColumn().getColumnName());
    }

    /**
     * 生成一个表名来自于{@code physicalTable}，字段名为{@code columnName}的字段
     */
    public static PhysicalColumn col(PhysicalTable physicalTable, String columnName) {
        return argNotNull(physicalTable, "physicalTable").column(columnName);
    }

    /**
     * 生成一个表名来自于{@code physicalTable}，字段名来自于{@code namedColumn}的字段
     */
    public static PhysicalColumn col(PhysicalTable physicalTable, NamedColumn namedColumn) {
        return argNotNull(physicalTable, "physicalTable").column(namedColumn.getColumnName());
    }

    /**
     * 生成一个表名为{@code aliasedSqlTable}的别名，字段名为{@code columnName}的字段
     */
    public static NamedColumn col(AliasedSqlTable<?> aliasedSqlTable, String columnName) {
        return new DefaultNamedColumn(argNotNull(aliasedSqlTable, "aliasedSqlTable").getAlias().getName(), columnName);
    }

    /**
     * 生成一个表名为{@code aliasedSqlTable}的别名，字段名来自于{@code namedColumn}的字段
     */
    public static NamedColumn col(AliasedSqlTable<?> aliasedSqlTable, NamedColumn namedColumn) {
        return new DefaultNamedColumn(argNotNull(aliasedSqlTable, "aliasedSqlTable").getAlias().getName(), argNotNull(namedColumn, "namedColumn").getColumnName());
    }

    /**
     * 生成一个表名为{@code tableName，字段名来自于{@code aliasedSelectColumn}的字段，保留别名
     */
    public static AliasedSelectColumn<NamedColumn> col2(String tableName, AliasedSelectColumn<NamedColumn> aliasedSelectColumn) {
        return new DefaultAliasedSelectColumn<>(new DefaultNamedColumn(tableName, aliasedSelectColumn.getSourceColumn().getColumnName()), aliasedSelectColumn.getAlias().getName());
    }

    /**
     * 生成一个表名来自于{@code PhysicalTable}，字段名来自于{@code aliasedSelectColumn}的字段，保留别名
     */
    public static DefaultAliasedSelectColumn<PhysicalColumn> col2(PhysicalTable physicalTable, AliasedSelectColumn<NamedColumn> aliasedSelectColumn) {
        return new DefaultAliasedSelectColumn<>(argNotNull(physicalTable, "physicalTable").column(argNotNull(aliasedSelectColumn, "aliasedSelectColumn").getSourceColumn().getColumnName()), aliasedSelectColumn.getAlias().getName());
    }

    /**
     * 生成一个表名为{@code aliasedSqlTable}的别名，字段名来自于{@code aliasedSelectColumn}的字段，保留别名
     */
    public static DefaultAliasedNamedColumn col2(AliasedSqlTable<?> aliasedSqlTable, AliasedSelectColumn<NamedColumn> aliasedSelectColumn) {
        String tableName = argNotNull(aliasedSqlTable, "aliasedSqlTable").getAlias().getName();
        String columnName = argNotNull(aliasedSelectColumn, "aliasedSelectColumn").getSourceColumn().getColumnName();
        return new DefaultAliasedNamedColumn(new DefaultNamedColumn(tableName, columnName), aliasedSelectColumn.getAlias().getName());
    }

    /**
     * 生成一个表名全部为{@code tableName}，字段名来自于{@code columnNames}的字段数组
     */
    public static NamedColumn[] cola(String tableName, String... columnNames) {
        NamedColumn[] namedColumns = new NamedColumn[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            namedColumns[i] = new DefaultNamedColumn(tableName, columnNames[i]);
        }
        return namedColumns;
    }

    /**
     * 生成一个表名全部为{@code tableName}，字段名来自于{@code namedColumns}的字段数组，不保留别名
     */
    @SafeVarargs
    public static NamedColumn[] cola(String tableName, AliasedSelectColumn<NamedColumn>... aliasedSelectColumns) {
        NamedColumn[] namedColumns = new NamedColumn[aliasedSelectColumns.length];
        for (int i = 0; i < aliasedSelectColumns.length; i++) {
            namedColumns[i] = new DefaultNamedColumn(tableName, aliasedSelectColumns[i].getSourceColumn().getColumnName());
        }
        return namedColumns;
    }

    /**
     * 生成一个表名全部为{@code tableName}，字段名来自于{@code namedColumns}的字段数组，保留字段别名
     */
    public static AliasedSelectColumn<NamedColumn>[] cola2(String tableName, AliasedSelectColumn<NamedColumn>... aliasedSelectColumns) {
        AliasedSelectColumn<NamedColumn>[] result = new AliasedSelectColumn[aliasedSelectColumns.length];
        for (int i = 0; i < aliasedSelectColumns.length; i++) {
            result[i] = new DefaultNamedColumn(tableName, aliasedSelectColumns[i].getSourceColumn().getColumnName()).as(aliasedSelectColumns[i].getAlias().getName());
        }
        return result;
    }

    /**
     * 生成一个表名为{@code aliasedSqlTable}的别名，字段名来自于{@code namedColumns}的字段数组，保留字段别名
     */
    public static AliasedSelectColumn<NamedColumn>[] cola2(AliasedSqlTable<?> aliasedSqlTable, AliasedSelectColumn<NamedColumn>... aliasedSelectColumns) {
        AliasedSelectColumn<NamedColumn>[] result = new AliasedSelectColumn[aliasedSelectColumns.length];
        for (int i = 0; i < aliasedSelectColumns.length; i++) {
            result[i] = new DefaultNamedColumn(aliasedSqlTable.getAlias().getName(), aliasedSelectColumns[i].getSourceColumn().getColumnName()).as(aliasedSelectColumns[i].getAlias().getName());
        }
        return result;
    }

    /**
     * 合并数组
     */
    public static SelectColumn[] merge(SelectColumn[]... selectColumnsArray) {
        List<SelectColumn> list = new ArrayList<>();
        for (SelectColumn[] selectColumns : selectColumnsArray) {
            Collections.addAll(list, selectColumns);
        }
        return list.toArray(new SelectColumn[0]);
    }

    /**
     * 合并数组
     */
    public static NamedColumn[] merge(NamedColumn[]... namedColumnsArray) {
        List<NamedColumn> namedColumnList = new ArrayList<>();
        for (NamedColumn[] namedColumns : namedColumnsArray) {
            Collections.addAll(namedColumnList, namedColumns);
        }
        return namedColumnList.toArray(new NamedColumn[0]);
    }

    /**
     * 合并数组
     */
    @SafeVarargs
    public static AliasedSelectColumn<NamedColumn>[] merge(AliasedSelectColumn<NamedColumn>[]... aliasedSelectColumnsArray) {
        List<AliasedSelectColumn<NamedColumn>> list = new ArrayList<>();
        for (AliasedSelectColumn<NamedColumn>[] aliasedSelectColumns : aliasedSelectColumnsArray) {
            Collections.addAll(list, aliasedSelectColumns);
        }
        return list.toArray(new AliasedSelectColumn[0]);
    }

    /**
     * 生成表名为{@code tableName}的表
     */
    public static PhysicalTable tab(String tableName) {
        return new DefaultPhysicalTable(tableName);
    }

    /**
     * 生成schema为{@code scheam}，表名为{@code tableName}的表
     */
    public static PhysicalTable tab(String schema, String tableName) {
        return new DefaultPhysicalTable(schema, tableName);
    }

    public static AfterSelectSelectClauseStep select(SelectColumn... selectColumns) {
        return new SelectImpl().select(selectColumns);
    }

    public static AfterSelectSelectClauseStep select(@Nonnull Supplier<SelectColumn> selectColumnsSupplier) {
        return new SelectImpl().select(selectColumnsSupplier.get());
    }

    public static AfterSelectSelectClauseStep select(boolean distinct, SelectColumn... selectColumns) {
        return distinct ? selectDistinct(selectColumns) : select(selectColumns);
    }

    public static AfterSelectSelectClauseStep select(boolean count, boolean distinct, SelectColumn... selectColumns) {
        if (count && distinct) {
            return selectCountDistinct(selectColumns);
        } else if (count) {
            return selectCount();
        } else if (distinct) {
            return selectDistinct(selectColumns);
        } else {
            return select(selectColumns);
        }
    }

    public static AfterSelectSelectClauseStep selectDistinct(SelectColumn... selectColumns) {
        return new SelectImpl().selectDistinct(selectColumns);
    }

    public static AfterSelectSelectClauseStep selectCountDistinct(SelectColumn... selectColumns) {
        return new SelectImpl().selectCountDistinct(selectColumns);
    }

    public static AfterSelectSelectClauseStep selectCount() {
        return new SelectImpl().select(count(1));
    }

    public static AfterSelectSelectClauseStep selectCount(SelectColumn... selectColumns) {
        return new SelectImpl().selectCount(selectColumns);
    }

    public static AfterInsertInsertIntoClauseStep insertInto(PhysicalTable intoTable, NamedColumn... intoColumns) {
        return new InsertImpl().insertInto(intoTable, intoColumns);
    }

    @SafeVarargs
    public static AfterInsertInsertIntoClauseStep insertInto(PhysicalTable intoTable, AliasedSelectColumn<NamedColumn>... aliasedIntoColumns) {
        return new InsertImpl().insertInto(intoTable, Arrays.stream(aliasedIntoColumns).map(AliasedSelectColumn::getSourceColumn).toArray(NamedColumn[]::new));
    }

    public static AfterInsertInsertIntoClauseStep insertInto(AliasedSqlTable<PhysicalTable> aliasedIntoTable, NamedColumn... intoColumns) {
        return new InsertImpl().insertInto(argNotNull(aliasedIntoTable, "aliasedIntoTable").getSourceTable(), intoColumns);
    }

    @SafeVarargs
    public static AfterInsertInsertIntoClauseStep insertInto(AliasedSqlTable<PhysicalTable> aliasedIntoTable, AliasedSelectColumn<NamedColumn>... aliasedIntoColumns) {
        return new InsertImpl().insertInto(argNotNull(aliasedIntoTable, "aliasedIntoTable").getSourceTable(), Arrays.stream(aliasedIntoColumns).map(AliasedSelectColumn::getSourceColumn).toArray(NamedColumn[]::new));
    }

    // Delete SQL

    public static AfterDeleteDeleteFromClauseStep deleteFrom(PhysicalTable fromTable) {
        return new DeleteImpl().deleteFrom(fromTable);
    }

    public static AfterDeleteDeleteFromClauseStep deleteFrom(AliasedSqlTable<PhysicalTable> aliasedFromTable) {
        return new DeleteImpl().deleteFrom(aliasedFromTable);
    }

    // Update SQl

    public static AfterUpdateUpdateClauseStep update(SqlTable... updateTables) {
        return new UpdateImpl().update(updateTables);
    }

    // Case When Then Else End

    public static AfterCaseClauseStep cases() {
        return new CaseWhenImpl().cases();
    }

    public static AfterCaseClauseStep cases(Object value) {
        if (value == null) {
            return cases();
        } else if (value instanceof SqlColumn) {
            return new CaseWhenImpl().cases((SqlColumn) value);
        }
        return new CaseWhenImpl().cases(value);
    }


    // Functions

    public static SelectFunctionColumn functionForSelect(FunctionName functionName, Object... params) {
        Function function = new DefaultFunction(functionName, params);
        return new DefaultSelectFunctionColumn(function);
    }

    public static SelectFunctionColumn sum(Object param) {
        argNotNull(param, "param");
        Function function = new DefaultFunction(FunctionNames.SUM, param);
        return new DefaultSelectFunctionColumn(function);
    }

    public static SelectFunctionColumn count(Object param) {
        argNotNull(param, "param");
        Function function = new DefaultFunction(FunctionNames.COUNT, param);
        return new DefaultSelectFunctionColumn(function);
    }


}
