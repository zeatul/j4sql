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

import glz.hawk.j4sql.dsl.delete.Delete;
import glz.hawk.j4sql.dsl.delete.DeleteDeleteFromStep;
import glz.hawk.j4sql.dsl.delete.after.AfterDeleteDeleteFromClauseStep;
import glz.hawk.j4sql.dsl.delete.after.AfterDeleteJoinClauseStep;
import glz.hawk.j4sql.dsl.delete.after.AfterDeleteOnClauseStep;
import glz.hawk.j4sql.dsl.delete.after.AfterDeleteWhereClauseStep;
import glz.hawk.j4sql.support.*;
import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.Connector;

import javax.annotation.Nonnull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DeleteImpl implements Delete, DeleteDeleteFromStep, AfterDeleteDeleteFromClauseStep, AfterDeleteJoinClauseStep, AfterDeleteOnClauseStep, AfterDeleteWhereClauseStep {

    private final DeleteQuery deleteQuery;

    public DeleteImpl() {
        this.deleteQuery = new DeleteQueryImpl();
    }


    @Nonnull
    @Override
    public DeleteQuery getDeleteQuery() {
        return deleteQuery;
    }

    @Nonnull
    @Override
    public Delete build() {
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteWhereClauseStep where(Condition condition, ConnectCondition... connectConditions) {
        deleteQuery.addWhere(condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteDeleteFromClauseStep deleteFrom(PhysicalTable fromTable) {
        deleteQuery.addFromTable(fromTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteDeleteFromClauseStep deleteFrom(AliasedSqlTable<PhysicalTable> fromTable) {
        deleteQuery.addFromTable(fromTable);
        return this;
    }

    @Nonnull
    @Override
    public DeleteImpl and(Condition condition, ConnectCondition... connectConditions) {
        deleteQuery.addCondition(Connector.AND, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public DeleteImpl andNot(Condition condition, ConnectCondition... connectConditions) {
        deleteQuery.addNotCondition(Connector.AND, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public DeleteImpl or(Condition condition, ConnectCondition... connectConditions) {
        deleteQuery.addCondition(Connector.OR, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public DeleteImpl orNot(Condition condition, ConnectCondition... connectConditions) {
        deleteQuery.addNotCondition(Connector.OR, condition, connectConditions);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep join(SqlTable joinSqlTable) {
        return innerJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep innerJoin(SqlTable joinSqlTable) {
        deleteQuery.addJoin(JoinType.INNER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep crossJoin(SqlTable joinSqlTable) {
        deleteQuery.addJoin(JoinType.CROSS_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep leftJoin(SqlTable joinSqlTable) {
        return leftOuterJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep leftOuterJoin(SqlTable joinSqlTable) {
        deleteQuery.addJoin(JoinType.LEFT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep rightJoin(SqlTable joinSqlTable) {
        return rightOuterJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep rightOuterJoin(SqlTable joinSqlTable) {
        deleteQuery.addJoin(JoinType.RIGHT_OUTER_JOIN, joinSqlTable);
        return this;
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep fullJoin(SqlTable joinSqlTable) {
        return fullOuterJoin(joinSqlTable);
    }

    @Nonnull
    @Override
    public AfterDeleteJoinClauseStep fullOuterJoin(SqlTable joinSqlTable) {
        deleteQuery.addJoin(JoinType.FULL_OUTER_JOIN, joinSqlTable);
        return this;
    }
}
