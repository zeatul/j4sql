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
import glz.hawk.j4sql.condition.impl.DefaultConnectCondition;
import glz.hawk.j4sql.condition.impl.ExistsCondition;
import glz.hawk.j4sql.condition.impl.NotCondition;
import glz.hawk.j4sql.dsl.delete.after.AfterDeleteDeleteFromClauseStep;
import glz.hawk.j4sql.dsl.insert.after.AfterInsertInsertIntoClauseStep;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.select.after.AfterSelectSelectClauseStep;
import glz.hawk.j4sql.dsl.update.after.AfterUpdateUpdateClauseStep;
import glz.hawk.j4sql.support.*;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class DSL {

    public static Condition not(Condition condition) {
        return NotCondition.of(condition);
    }

    public static Condition exists(Select select) {
        return ExistsCondition.of(select);
    }

    public static ConnectCondition and(Condition condition) {
        return DefaultConnectCondition.and(condition);
    }

    public static ConnectCondition or(Condition condition) {
        return DefaultConnectCondition.or(condition);
    }

    public static NamedColumn col(String columnName) {
        return new DefaultNamedColumn(columnName);
    }

    public static NamedColumn col(String tableName, String columnName) {
        return new DefaultNamedColumn(tableName, columnName);
    }

    public static NamedColumn col(PhysicalTable defaultSqlTable, String columnName) {
        return new DefaultNamedColumn(argNotNull(defaultSqlTable, "defaultSqlTable").getTableName(), columnName);
    }

    public static NamedColumn col(PhysicalTable defaultSqlTable, NamedColumn namedColumn) {
        return new DefaultNamedColumn(argNotNull(defaultSqlTable, "defaultSqlTable").getTableName(), argNotNull(namedColumn, "namedColumn").getColumnName());
    }

    public static NamedColumn col(PhysicalTable defaultSqlTable, AliasedSelectColumn<?> aliasedSelectColumn) {
        return new DefaultNamedColumn(argNotNull(defaultSqlTable, "defaultSqlTable").getTableName(), argNotNull(aliasedSelectColumn, "aliasedSelectColumn").getAlias().getName());
    }

    public static NamedColumn col(AliasedSqlTable<?> aliasedSqlTable, String columnName) {
        return new DefaultNamedColumn(argNotNull(aliasedSqlTable, "aliasedSqlTable").getAlias().getName(), columnName);
    }

    public static NamedColumn col(AliasedSqlTable<?> aliasedSqlTable, NamedColumn namedColumn) {
        return new DefaultNamedColumn(argNotNull(aliasedSqlTable, "aliasedSqlTable").getAlias().getName(), argNotNull(namedColumn, "namedColumn").getColumnName());
    }

    public static NamedColumn col(AliasedSqlTable<?> aliasedSqlTable, AliasedSelectColumn<?> aliasedSelectColumn) {
        return new DefaultNamedColumn(argNotNull(aliasedSqlTable, "aliasedSqlTable").getAlias().getName(), argNotNull(aliasedSelectColumn, "aliasedSelectColumn").getAlias().getName());
    }

    public static PhysicalTable tab(String tableName) {
        return new DefaultPhysicalTable(tableName);
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

    public static AfterUpdateUpdateClauseStep update(PhysicalTable updateTable) {
        return new UpdateImpl().update(updateTable);
    }

    public static AfterUpdateUpdateClauseStep update(AliasedSqlTable<PhysicalTable> aliasedUpdateTable) {
        return new UpdateImpl().update(aliasedUpdateTable);
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
