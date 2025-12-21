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

package glz.hawk.j4sql.writer.impl;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.support.Keyword;
import glz.hawk.j4sql.support.OrderColumn;
import glz.hawk.j4sql.support.SqlColumn;
import glz.hawk.j4sql.support.SqlTable;
import glz.hawk.codepoet.core.AbstractCodeBlock;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SqlCodeBlock extends AbstractCodeBlock {
    private SqlCodeBlock(Builder builder) {
        super(builder);
    }

    public static SqlCodeBlock of(String format, Object... args) {
        return new Builder().add(format, args).build();
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder extends AbstractCodeBlockBuilder<SqlCodeBlock, Builder> {
        final static Character[] NO_ARG_PLACE_HOLDERS = new Character[]{'$', '>', '<', '[', ']', 'W', 'R'};
        final static Character[] ARG_PLACE_HOLDERS = new Character[]{'K', 'k', 'L', 'T', 'C', 'B', 'O', 'S'};

        private Builder() {
            super(NO_ARG_PLACE_HOLDERS, ARG_PLACE_HOLDERS);
        }

        @Override
        protected void addArgument(String format, char c, Object arg) {
            switch (c) {
                case 'k': // parameter must be instanced of Keyword. 输出和关键字等长的空白字符串
                case 'K': // parameter must be instanced of Keyword. 输出关键字
                    this.args.add(toRequiredObject(arg, Keyword.class));
                    break;
//                case 'c': // parameter must be instanced of SqlColumn. 输出Column的名称
                case 'C': // parameter must be instanced of SqlColumn. 输出Column的名称和别名
                    this.args.add(toRequiredObject(arg, SqlColumn.class));
                    break;
                case 'O': // parameter must be instanced of OrderColumn.
                    this.args.add(toRequiredObject(arg, OrderColumn.class));
                    break;
                case 'L': // Literal
                    this.args.add(argToLiteral(arg));
                    break;
//                case 'P': // Parameter
//                    this.args.add(argToLiteral(arg));
//                    break;
                case 'T': // parameter must be instanced of SqlTable. 输出Table的名称和别名
                    this.args.add(toRequiredObject(arg, SqlTable.class));
                    break;
                case 'B': // parameter must be instanced of Condition.
                    this.args.add(toRequiredObject(arg, Condition.class));
                    break;
                case 'S': // parameter must be instanced of CharSequence.
                    this.args.add(toRequiredObject(arg, CharSequence.class));
                    break;
                default:
                    throw new IllegalArgumentException(
                        String.format("invalid format string: '%s'", format));
            }
        }

        @SuppressWarnings("unchecked")
        private <T> T toRequiredObject(Object object, Class<T> clazz) {
            return (T) argument(argNotNull(object, "object"), clazz::isInstance, o -> String.format("The parameter[object] must be the instance of %s", clazz.getCanonicalName()));
        }


        private Object argToLiteral(Object o) {
            return o;
        }


        public SqlCodeBlock build() {
            return new SqlCodeBlock(this);
        }
    }
}
