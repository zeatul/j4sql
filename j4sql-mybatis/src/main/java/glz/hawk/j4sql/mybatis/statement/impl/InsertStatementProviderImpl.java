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

package glz.hawk.j4sql.mybatis.statement.impl;

import glz.hawk.j4sql.mybatis.statement.InsertStatementProvider;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class InsertStatementProviderImpl<T> implements InsertStatementProvider<T> {

    private final String statement;
    private final T row;

    private InsertStatementProviderImpl(Builder<T> builder) {
        this.statement = builder.statement;
        this.row = builder.row;
    }

    public  static <Row> Builder<Row> builder(String statement, Row row){
        return new Builder<>(statement,row);
    }


    @Nonnull
    @Override
    public String getStatement() {
        return statement;
    }

    @Nonnull
    @Override
    public T getRow() {
        return row;
    }

    public static class Builder<T>{
        private final String statement;
        private final T row;
        private Builder(String statement,T row){
            this.statement = argNotBlank(statement, "statement");
            this.row = argNotNull(row, "row");
        }
        public InsertStatementProviderImpl<T> build(){
            return new InsertStatementProviderImpl<>(this);
        }
    }
}
