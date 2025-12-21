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

package glz.hawk.j4sql.support;

import glz.hawk.j4sql.condition.Condition;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * This interface is responsible for
 * SqlColumn可以是物理字段，也可以是计算结果,也可以是一个动态参数，也可以是一个静态值，也可以是*号，也可以是一段查询结果中的字段。
 *
 * @author Hawk
 */
public interface SqlColumn {

    // condition

    @Nonnull
    Condition eq(Object value);

    @Nonnull
    Condition eq(SqlColumn sqlColumn);

    @Nonnull
    Condition ne(Object value);

    @Nonnull
    Condition ne(SqlColumn sqlColumn);

    @Nonnull
    Condition gt(Object value);

    @Nonnull
    Condition gt(SqlColumn sqlColumn);

    @Nonnull
    Condition ge(Object value);

    @Nonnull
    Condition ge(SqlColumn sqlColumn);

    @Nonnull
    Condition lt(Object value);

    @Nonnull
    Condition lt(SqlColumn sqlColumn);

    @Nonnull
    Condition le(Object value);

    @Nonnull
    Condition le(SqlColumn sqlColumn);

    @Nonnull
    Condition like(String value);

    @Nonnull
    Condition like(SqlColumn sqlColumn);

    @Nonnull
    Condition notLike(String value);

    @Nonnull
    Condition notLike(SqlColumn sqlColumn);

    @Nonnull
    Condition in(SqlColumn sqlColumn);

    @Nonnull
    Condition in(Collection<?> values);

    @Nonnull
    Condition in(Object... values);

    @Nonnull
    Condition notIn(SqlColumn sqlColumn);

    @Nonnull
    Condition notIn(Collection<?> values);

    @Nonnull
    Condition notIn(Object... values);

    @Nonnull
    Condition isNull();

    @Nonnull
    Condition isNotNull();

    <T> Condition between(T minValue, T maxValue);

    // expression

    /**
     * Addition
     */
    @Nonnull
    SelectColumn plus(SqlColumn value);

    /**
     * Addition
     */
    @Nonnull
    SelectColumn plus(Number value);

    /**
     * Subtraction
     */
    @Nonnull
    SelectColumn minus(SqlColumn value);

    /**
     * Subtraction
     */
    @Nonnull
    SelectColumn minus(Number value);

    /**
     * Multiplication
     */
    @Nonnull
    SelectColumn times(SqlColumn value);

    /**
     * Multiplication
     */
    @Nonnull
    SelectColumn times(Number value);

    /**
     * Division
     */
    @Nonnull
    SelectColumn div(SqlColumn value);

    /**
     * Division
     */
    @Nonnull
    SelectColumn div(Number value);

    /**
     * Modulo
     */
    @Nonnull
    SelectColumn mod(SqlColumn value);

    /**
     * Modulo
     */
    @Nonnull
    SelectColumn mod(Number value);

    /**
     * Concatenation
     */
    @Nonnull
    SelectColumn concat(SqlColumn value);

    /**
     * Concatenation
     */
    @Nonnull
    SelectColumn concat(String value);

}
