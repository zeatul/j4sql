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

/**
 * This enum is responsible for
 *
 * @author Hawk
 */
public enum SqlClause {

    /**
     * select {@code SELECT_CLAUSE} from table
     */
    SELECT_CLAUSE,
    /**
     * select * into {@code INTO_CLAUSE}
     */
    INTO_CLAUSE,

    /**
     * select * from {@code SELECT_FROM_CLAUSE}
     */
    SELECT_FROM_CLAUSE,

    GROUP_BY_CLAUSE,

    JOIN_CLAUSE,
    ON_CLAUSE,
    WHERE_CLAUSE,
    ORDER_BY_CLAUSE,
    HAVING_CLAUSE,
    OFFSET_CLAUSE,
    LIMIT_CLAUSE,
    FOR_UPDATE_CLAUSE,
    NO_WAIT_CLAUSE,
    WAIT_CLAUSE,

    /**
     * select {@code SELECT_FUNCTION_COLUMN_CLAUSE} from table having {@code SELECT_FUNCTION_COLUMN_CLAUSE} > 1
     */
    SELECT_FUNCTION_COLUMN_CLAUSE,

    /**
     * select {@code SELECT_EXPRESSION_COLUMN_CLAUSE} from table
     */
    SELECT_EXPRESSION_COLUMN_CLAUSE,

    // insert into table() values()
    INTO_TABLE_CLAUSE,
    INTO_COLUMNS_CLAUSE,
    INTO_VALUES_CLAUSE,

    // update table set
    UPDATE_TABLE_CLAUSE,
    SET_CLAUSE,

    // delete from table
    DELETE_FROM_CLAUSE;

    public boolean outputColumnName() {
        return this == SELECT_CLAUSE || this == WHERE_CLAUSE || this == HAVING_CLAUSE || this == SET_CLAUSE
            || this == GROUP_BY_CLAUSE || this == SELECT_FUNCTION_COLUMN_CLAUSE || this == SELECT_EXPRESSION_COLUMN_CLAUSE;
    }

    public boolean outputColumnAlias() {
        return this == SELECT_CLAUSE || this == ON_CLAUSE;
    }

    public boolean parameterable() {
        return this == WHERE_CLAUSE || this == LIMIT_CLAUSE || this == OFFSET_CLAUSE || this == HAVING_CLAUSE;
    }
}
