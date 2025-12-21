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
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.select.SelectFinalStep;
import glz.hawk.j4sql.dsl.select.SelectSelectStep;
import glz.hawk.j4sql.dsl.select.after.*;
import glz.hawk.j4sql.support.*;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SelectImpl implements Select, SelectSelectStep,
        AfterSelectSelectClauseStep, AfterSelectIntoClauseStep, AfterSelectFromClauseStep, AfterSelectJoinClauseStep, AfterSelectOnClauseStep,
        AfterSelectWhereClauseStep, AfterSelectGroupByClauseStep, AfterSelectHavingClauseStep, AfterSelectOrderByClauseStep, AfterSelectLimitClauseStep,
    AfterSelectOffsetClauseStep, AfterSelectForUpdateClauseStep {

    private final SelectQuery selectQuery;

    public SelectImpl() {
        this.selectQuery = new SelectQueryImpl();
    }

    @Override
    public AfterSelectSelectClauseStep select(SelectColumn... selectColumns) {
        selectQuery.addSelect(selectColumns);
        return this;
    }

    @Override
    public AfterSelectSelectClauseStep selectDistinct(SelectColumn... selectColumns) {
        selectQuery.addSelect(selectColumns);
        selectQuery.setDistinct(true);
        return this;
    }

    @Override
    public AfterSelectSelectClauseStep selectCountDistinct(SelectColumn... selectColumns) {
        selectQuery.addSelect(selectColumns);
        selectQuery.setDistinct(true);
        selectQuery.setCount(true);
        return this;
    }

    @Override
    public AfterSelectSelectClauseStep selectCount(SelectColumn... selectColumns) {
        selectQuery.addSelect(selectColumns);
        selectQuery.setCount(true);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectIntoClauseStep into(SqlTable sqlTable) {
        selectQuery.setInto(sqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectFromClauseStep from(SqlTable... sqlTables) {
        selectQuery.addFrom(sqlTables);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectFromClauseStep from(Select select) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public AfterSelectFromClauseStep hint(String hint) {
        selectQuery.setHint(hint);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectGroupByClauseStep groupBy(SelectColumn... groupByColumns) {
        selectQuery.addGroupBy(groupByColumns);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectWhereClauseStep where(Condition condition, ConnectCondition... connectConditions) {
        if (condition != null) {
            selectQuery.addWhere(condition, connectConditions);
        }
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep join(SqlTable joinSqlTable) {
        return innerJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep innerJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.INNER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep crossJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.CROSS_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep leftJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.LEFT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep leftOuterJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.LEFT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep rightJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.RIGHT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep rightOuterJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.RIGHT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep fullJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.FULL_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectJoinClauseStep fullOuterJoin(SqlTable joinSqlTable) {
        selectQuery.addJoin(JoinType.FULL_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOnClauseStep on(Condition condition, ConnectCondition... connectConditions) {
        selectQuery.addOn(condition, connectConditions);
        return this;
    }

    @Override
    public SelectImpl and(Condition condition, ConnectCondition... connectConditions) {
        selectQuery.addCondition(Connector.AND, condition, connectConditions);
        return this;
    }

    @Override
    public SelectImpl andNot(Condition condition, ConnectCondition... connectConditions) {
        selectQuery.addNotCondition(Connector.AND, condition, connectConditions);
        return this;
    }

    @Override
    public SelectImpl or(Condition condition, ConnectCondition... connectConditions) {
        selectQuery.addCondition(Connector.OR, condition, connectConditions);
        return this;
    }

    @Override
    public SelectImpl orNot(Condition condition, ConnectCondition... connectConditions) {
        selectQuery.addNotCondition(Connector.OR, condition, connectConditions);
        return this;
    }

    @Override
    public AfterSelectHavingClauseStep having(Condition condition, ConnectCondition... connectConditions) {
        selectQuery.addHaving(condition, connectConditions);
        return this;
    }

    @Override
    public AfterSelectOrderByClauseStep orderBy(OrderColumn... orderColumns) {
        selectQuery.addOrderBy(orderColumns);
        return this;
    }

    @Override
    public AfterSelectOrderByClauseStep orderBy(@Nonnull Supplier<Boolean> conditionSupplier, OrderColumn... orderColumns) {
        if (conditionSupplier.get()) {
            selectQuery.addOrderBy(orderColumns);
        }
        return this;
    }

    @Override
    public AfterSelectOrderByClauseStep orderBy(boolean condition, OrderColumn... orderColumns) {
        if (condition) {
            selectQuery.addOrderBy(orderColumns);
        }
        return this;
    }

    @Override
    public AfterSelectLimitClauseStep limit(long limit) {
        selectQuery.addLimit(new DefaultValueColumn(limit));
        return this;
    }

    @Override
    public AfterSelectLimitClauseStep limit(ValueColumn limit) {
        selectQuery.addLimit(argNotNull(limit,"limit"));
        return this;
    }

    @Override
    public AfterSelectLimitClauseStep limit(@Nonnull Supplier<Boolean> conditionSupplier, Long limit) {
        if (conditionSupplier.get()) {
            selectQuery.addLimit(new DefaultValueColumn(argNotNull(limit,"limit")));
        }
        return this;
    }

    @Override
    public AfterSelectLimitClauseStep limit(@Nonnull Supplier<Boolean> conditionSupplier, ValueColumn limit) {
        if (conditionSupplier.get()) {
            selectQuery.addLimit(argNotNull(limit,"limit"));
        }
        return this;
    }

    @Override
    public AfterSelectLimitClauseStep limit(boolean condition, Long limit) {
        if (condition) {
            selectQuery.addLimit(new DefaultValueColumn(argNotNull(limit,"limit")));
        }
        return this;
    }

    @Override
    public AfterSelectLimitClauseStep limit(boolean condition, ValueColumn limit) {
        if (condition) {
            selectQuery.addLimit(argNotNull(limit,"limit"));
        }
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOffsetClauseStep offset(long offset) {
        selectQuery.addOffset(new DefaultValueColumn(offset));
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOffsetClauseStep offset(ValueColumn offset) {
        selectQuery.addOffset(argNotNull(offset,"offset"));
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOffsetClauseStep offset(@Nonnull Supplier<Boolean> conditionSupplier, Long offset) {
        if (conditionSupplier.get()) {
            selectQuery.addOffset(new DefaultValueColumn(argNotNull(offset,"offset")));
        }
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOffsetClauseStep offset(@Nonnull Supplier<Boolean> conditionSupplier, ValueColumn offset) {
        if (conditionSupplier.get()) {
            selectQuery.addOffset(argNotNull(offset,"offset"));
        }
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOffsetClauseStep offset(boolean condition, Long offset) {
        if (condition) {
            selectQuery.addOffset(new DefaultValueColumn(argNotNull(offset,"offset")));
        }
        return this;
    }

    @Nonnull
    @Override
    public AfterSelectOffsetClauseStep offset(boolean condition, ValueColumn offset) {
        if (condition) {
            selectQuery.addOffset(argNotNull(offset,"offset"));
        }
        return this;
    }

    @Override
    public AfterSelectForUpdateClauseStep forUpdate() {
        selectQuery.setForUpdate();
        return this;
    }

    @Override
    public AfterSelectForUpdateClauseStep forUpdate(boolean forUpdate) {
        if (forUpdate){
            selectQuery.setForUpdate();
        }
        return this;
    }

    @Nonnull
    @Override
    public SelectFinalStep wait(int seconds) {
        selectQuery.setWait(5);
        return this;
    }

    @Nonnull
    @Override
    public SelectFinalStep noWait() {
        selectQuery.setNoWait();
        return this;
    }

    @Nonnull
    @Override
    public Select build() {
        return this;
    }

    @Nonnull
    @Override
    public SelectColumn asColumn() {
        return new DefaultSubqueryColumn(this);
    }

    @Nonnull
    @Override
    public AliasedSelectColumn<SubqueryColumn> asColumn(String aliasName) {
        return new DefaultSubqueryColumn(this).as(aliasName);
    }

    @Nonnull
    @Override
    public AliasedSqlTable<SubqueryTable> asTable(String aliasName) {
        return new DefaultSubqueryTable(this).as(aliasName);
    }

    @Override
    public SelectQuery getSelectQuery() {
        return selectQuery;
    }
}
