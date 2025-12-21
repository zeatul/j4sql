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

package glz.hawk.j4sql.util;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.support.OrderColumn;
import glz.hawk.j4sql.support.SelectColumn;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class QueryWrapper {
    private final boolean distinct;
    private final boolean count;
    private final SelectColumn[] columns;
    private final Condition condition;
    private final OrderColumn[] orderColumns;
    private final Long offset;
    private final Long limit;
    private final boolean forUpdate ;

    private QueryWrapper(Builder builder) {
        this.distinct = builder.distinct;
        this.count = builder.count;
        this.columns = builder.columns;
        this.condition = builder.condition;
        this.orderColumns = builder.orderColumns;
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.forUpdate = builder.forUpdate;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Builder getBuilder(){
        return new Builder()
            .setDistinct(distinct)
            .setCount(count)
            .setColumns(columns)
            .setCondition(condition)
            .setOrderColumns(orderColumns)
            .setOffsetAndLimit(offset,limit)
            .setForUpdate(forUpdate);
    }

    public SelectColumn[] getColumns() {
        return this.columns;
    }

    public boolean isDistinct() {
        return this.distinct;
    }

    public boolean isCount() {
        return this.count;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public Long getOffset() {
        return this.offset;
    }

    public Long getLimit() {
        return this.limit;
    }

    public boolean isForUpdate(){
        return this.forUpdate;
    }

    public OrderColumn[] getOrderColumns() {
        return this.orderColumns;
    }

    public static class Builder {
        private boolean distinct = false;
        private boolean count = false;
        private SelectColumn[] columns;
        private Condition condition;
        private OrderColumn[] orderColumns;
        private Long offset;
        private Long limit;
        private boolean forUpdate = false;

        public Builder setDistinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        public Builder setCount(boolean count) {
            this.count = count;
            return this;
        }

        public Builder setColumns(SelectColumn... columns) {
            this.columns = columns;
            return this;
        }

        public Builder setCondition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public Builder setOrderColumns(OrderColumn... orderColumns) {
            this.orderColumns = orderColumns;
            return this;
        }

        public Builder setOffsetAndLimit(Long offset, Long limit) {
            this.offset = offset;
            this.limit = limit;
            return this;
        }

        public Builder setForUpdate(boolean forUpdate){
            this.forUpdate = forUpdate;
            return this;
        }

        public QueryWrapper build() {
            return new QueryWrapper(this);
        }
    }

}
