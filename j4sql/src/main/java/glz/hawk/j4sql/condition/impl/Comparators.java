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
package glz.hawk.j4sql.condition.impl;

import glz.hawk.j4sql.condition.Comparator;
import glz.hawk.j4sql.support.Configuration;

/**
 * This enum is responsible for
 *
 * @author Hawk
 */
public enum Comparators implements Comparator {
    EQUALS {
        public String toSql(Configuration configuration) {
            return "=";
        }
    },
    NOT_EQUALS{
        public String toSql(Configuration configuration) {
            return "<>";
        }
    },
    LESS{
        public String toSql(Configuration configuration) {
            return "<";
        }
    },
    LESS_OR_EQUAL{
        public String toSql(Configuration configuration) {
            return "<=";
        }
    },
    GREATER{
        public String toSql(Configuration configuration) {
            return ">";
        }
    },
    GREATER_OR_EQUAL{
        public String toSql(Configuration configuration) {
            return ">=";
        }
    },
    IN{
        public String toSql(Configuration configuration) {
            return "IN";
        }
    },
    NOT_IN{
        public String toSql(Configuration configuration) {
            return "NOT IN";
        }
    },
    LIKE{
        public String toSql(Configuration configuration) {
            return "LIKE";
        }
    },
    NOT_LIKE{
        public String toSql(Configuration configuration) {
            return "NOT LIKE";
        }
    },

}
