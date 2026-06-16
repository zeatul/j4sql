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
import glz.hawk.j4sql.dsl.update.Update;
import glz.hawk.j4sql.dsl.update.UpdateSetStep;
import glz.hawk.j4sql.dsl.update.UpdateUpdateStep;
import glz.hawk.j4sql.dsl.update.after.*;
import glz.hawk.j4sql.support.*;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class UpdateImpl implements Update, UpdateUpdateStep, AfterUpdateUpdateClauseStep, AfterUpdateJoinClauseStep, AfterUpdateOnClauseStep, AfterUpdateSetClauseStep, AfterUpdateWhereClauseStep {
    private final UpdateQuery updateQuery;

    public UpdateImpl() {
        this.updateQuery = new UpdateQueryImpl();
    }

    @Override
    public UpdateQuery getUpdateQuery() {
        return updateQuery;
    }

    @Nonnull
    @Override
    public AfterUpdateUpdateClauseStep update(SqlTable... updateTables) {
        updateQuery.addUpdateTable(updateTables);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep join(SqlTable joinSqlTable) {
        return innerJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep innerJoin(SqlTable joinSqlTable) {
        updateQuery.addJoin(JoinType.INNER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep crossJoin(SqlTable joinSqlTable) {
        updateQuery.addJoin(JoinType.CROSS_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep leftJoin(SqlTable joinSqlTable) {
        return leftOuterJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep leftOuterJoin(SqlTable joinSqlTable) {
        updateQuery.addJoin(JoinType.LEFT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep rightJoin(SqlTable joinSqlTable) {
        return rightOuterJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep rightOuterJoin(SqlTable joinSqlTable) {
        updateQuery.addJoin(JoinType.RIGHT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep fullJoin(SqlTable joinSqlTable) {
        return fullOuterJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterUpdateJoinClauseStep fullOuterJoin(SqlTable joinSqlTable) {
        updateQuery.addJoin(JoinType.FULL_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateSetClauseStep set(NamedColumn updateColumn, SqlColumn valueColumn) {
        updateQuery.addSet(updateColumn, valueColumn);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateSetClauseStep set(NamedColumn updateColumn, Object value) {
        updateQuery.addSet(updateColumn, value);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateSetClauseStep set(AliasedSelectColumn<NamedColumn> aliasedUpdateColumn, SqlColumn valueColumn) {
        updateQuery.addSet(aliasedUpdateColumn, valueColumn);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateSetClauseStep set(AliasedSelectColumn<NamedColumn> aliasedUpdateColumn, Object value) {
        updateQuery.addSet(aliasedUpdateColumn, value);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateSetClauseStep set(Consumer<UpdateSetStep> consumer) {
        consumer.accept(this);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateOnClauseStep on(Condition condition, ConnectCondition... connectConditions) {
        updateQuery.addOn(condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public AfterUpdateWhereClauseStep where(Condition condition, ConnectCondition... connectConditions) {
        updateQuery.addWhere(condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public UpdateImpl and(Condition condition, ConnectCondition... connectConditions) {
        updateQuery.addCondition(Connector.AND, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public UpdateImpl andNot(Condition condition, ConnectCondition... connectConditions) {
        updateQuery.addNotCondition(Connector.AND, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public UpdateImpl or(Condition condition, ConnectCondition... connectConditions) {
        updateQuery.addCondition(Connector.OR, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public UpdateImpl orNot(Condition condition, ConnectCondition... connectConditions) {
        updateQuery.addNotCondition(Connector.OR, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public Update build() {
        return this;
    }
}
