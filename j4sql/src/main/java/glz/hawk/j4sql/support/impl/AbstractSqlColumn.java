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

import glz.hawk.j4sql.condition.Comparator;
import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.impl.*;
import glz.hawk.j4sql.support.SelectColumn;
import glz.hawk.j4sql.support.SqlColumn;

import javax.annotation.Nonnull;
import java.util.Collection;

import static glz.hawk.j4sql.support.impl.ExpressionOperators.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractSqlColumn implements SqlColumn {

    // condition

    @Nonnull
    @Override
    public Condition eq(Object value) {
        return compare(value, Comparators.EQUALS);
    }

    @Nonnull
    @Override
    public Condition eq(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.EQUALS);
    }

    @Nonnull
    @Override
    public Condition ne(Object value) {
        return compare(value, Comparators.NOT_EQUALS);
    }

    @Nonnull
    @Override
    public Condition ne(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.NOT_EQUALS);
    }

    @Nonnull
    @Override
    public Condition gt(Object Value) {
        return compare(Value, Comparators.GREATER);
    }

    @Nonnull
    @Override
    public Condition gt(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.GREATER);
    }

    @Nonnull
    @Override
    public Condition ge(Object Value) {
        return compare(Value, Comparators.GREATER_OR_EQUAL);
    }

    @Nonnull
    @Override
    public Condition ge(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.GREATER_OR_EQUAL);
    }

    @Nonnull
    @Override
    public Condition lt(Object Value) {
        return compare(Value, Comparators.LESS);
    }

    @Nonnull
    @Override
    public Condition lt(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.LESS);
    }

    @Nonnull
    @Override
    public Condition le(Object Value) {
        return compare(Value, Comparators.LESS_OR_EQUAL);
    }

    @Nonnull
    @Override
    public Condition le(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.LESS_OR_EQUAL);
    }

    @Nonnull
    @Override
    public Condition like(String Value) {
        return compare(Value, Comparators.LIKE);
    }

    @Nonnull
    @Override
    public Condition like(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.LIKE);
    }

    @Nonnull
    @Override
    public Condition notLike(String Value) {
        return compare(Value, Comparators.NOT_LIKE);
    }

    @Nonnull
    @Override
    public Condition notLike(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.NOT_LIKE);
    }

    @Nonnull
    @Override
    public Condition in(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.IN);
    }

    @Nonnull
    @Override
    public Condition in(Collection<?> values) {
        return compare(values, Comparators.IN);
    }

    @Nonnull
    @Override
    public Condition in(Object... values) {
        return compare(values, Comparators.IN);
    }

    @Nonnull
    @Override
    public Condition notIn(SqlColumn sqlColumn) {
        return compare(sqlColumn, Comparators.NOT_IN);
    }

    @Nonnull
    @Override
    public Condition notIn(Collection<?> values) {
        return compare(values, Comparators.NOT_IN);
    }

    @Nonnull
    @Override
    public Condition notIn(Object... values) {
        return compare(values, Comparators.NOT_IN);
    }

    @Nonnull
    @Override
    public Condition isNull() {
        return IsNullCondition.of(this);
    }

    @Nonnull
    @Override
    public Condition isNotNull() {
        return IsNotNullCondition.of(this);
    }

    @Override
    public <T> Condition between(T minValue, T maxValue) {
        return BetweenCondition.of(this, minValue, maxValue);
    }

    private Condition compare(SqlColumn sqlColumn, Comparator comparator) {
        return CompareCondition.of(comparator, this, sqlColumn);
    }

    private Condition compare(Object value, Comparator comparator) {
        if (value instanceof SqlColumn) {
            return compare((SqlColumn) value, comparator);
        } else {
            return CompareCondition.of(comparator, this, new DefaultValueColumn(value));
        }
    }

    // Expression


    @Nonnull
    @Override
    public SelectColumn plus(SqlColumn value) {
        return new DefaultSelectExpressionColumn(this, ADD, value);
    }

    @Nonnull
    @Override
    public SelectColumn plus(Number value) {
        return new DefaultSelectExpressionColumn(this, ADD, new DefaultValueColumn(value));
    }

    @Nonnull
    @Override
    public SelectColumn minus(SqlColumn value) {
        return new DefaultSelectExpressionColumn(this, SUBTRACT, value);
    }

    @Nonnull
    @Override
    public SelectColumn minus(Number value) {
        return new DefaultSelectExpressionColumn(this, SUBTRACT, new DefaultValueColumn(value));
    }

    @Nonnull
    @Override
    public SelectColumn times(SqlColumn value) {
        return new DefaultSelectExpressionColumn(this, MULTIPLY, value);
    }

    @Nonnull
    @Override
    public SelectColumn times(Number value) {
        return new DefaultSelectExpressionColumn(this, MULTIPLY, new DefaultValueColumn(value));
    }

    @Nonnull
    @Override
    public SelectColumn div(SqlColumn value) {
        return new DefaultSelectExpressionColumn(this, DIVIDE, value);
    }

    @Nonnull
    @Override
    public SelectColumn div(Number value) {
        return new DefaultSelectExpressionColumn(this, DIVIDE, new DefaultValueColumn(value));
    }

    @Nonnull
    @Override
    public SelectColumn mod(SqlColumn value) {
        return new DefaultSelectExpressionColumn(this, MODULO, value);
    }

    @Nonnull
    @Override
    public SelectColumn mod(Number value) {
        return new DefaultSelectExpressionColumn(this, MODULO, new DefaultValueColumn(value));
    }

    @Nonnull
    @Override
    public SelectColumn concat(SqlColumn value) {
        return new DefaultSelectExpressionColumn(this, CONCAT, value);
    }

    @Nonnull
    @Override
    public SelectColumn concat(String value) {
        return new DefaultSelectExpressionColumn(this, CONCAT, new DefaultValueColumn(value));
    }
}
