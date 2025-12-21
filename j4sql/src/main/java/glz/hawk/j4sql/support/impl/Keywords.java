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

import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.Keyword;

/**
 * This enum is responsible for
 *
 * @author Hawk
 */
public enum Keywords implements Keyword {
    ASTERISK {
        public String toSql(Configuration configuration) {
            return "*";
        }
    },
    SELECT {
        public String toSql(Configuration configuration) {
            return "SELECT";
        }
    },
    COUNT {
        public String toSql(Configuration configuration) {
            return "COUNT";
        }
    },
    DISTINCT {
        public String toSql(Configuration configuration) {
            return "DISTINCT";
        }
    },
    INTO {
        public String toSql(Configuration configuration) {
            return "INTO";
        }
    },
    AS {
        public String toSql(Configuration configuration) {
            return "AS";
        }
    },
    FROM {
        public String toSql(Configuration configuration) {
            return "FROM";
        }
    },
    ON {
        public String toSql(Configuration configuration) {
            return "ON";
        }
    },
    WHERE {
        public String toSql(Configuration configuration) {
            return "WHERE";
        }
    },
    GROUP_BY {
        public String toSql(Configuration configuration) {
            return "GROUP BY";
        }
    },
    HAVING {
        public String toSql(Configuration configuration) {
            return "HAVING";
        }
    },
    ORDER_BY {
        public String toSql(Configuration configuration) {
            return "ORDER BY";
        }
    },
    LIMIT {
        public String toSql(Configuration configuration) {
            return "LIMIT";
        }
    },
    OFFSET {
        public String toSql(Configuration configuration) {
            return "OFFSET";
        }
    },
    FOR_UPDATE {
        public String toSql(Configuration configuration) {
            return "FOR UPDATE";
        }
    },
    NO_WAIT {
        public String toSql(Configuration configuration) {
            return "NO WAIT";
        }
    },
    WAIT {
        public String toSql(Configuration configuration) {
            return "WAIT";
        }
    },
    INSERT_INTO {
        public String toSql(Configuration configuration) {
            return "INSERT INTO";
        }
    },
    VALUES {
        public String toSql(Configuration configuration) {
            return "VALUES";
        }
    },
    DELETE {
        public String toSql(Configuration configuration) {
            return "DELETE";
        }
    },
    UPDATE {
        public String toSql(Configuration configuration) {
            return "UPDATE";
        }
    },
    SET {
        public String toSql(Configuration configuration) {
            return "SET";
        }
    },
    EXISTS {
        public String toSql(Configuration configuration) {
            return "EXISTS";
        }
    },
    NOT {
        public String toSql(Configuration configuration) {
            return "NOT";
        }
    },
    IS_NULL {
        public String toSql(Configuration configuration) {
            return "IS NULL";
        }
    },
    IS_NOT_NULL {
        public String toSql(Configuration configuration) {
            return "IS NOT NULL";
        }
    },
    BETWEEN {
        public String toSql(Configuration configuration) {
            return "BETWEEN";
        }
    },
    AND {
        public String toSql(Configuration configuration) {
            return "AND";
        }
    }


}
