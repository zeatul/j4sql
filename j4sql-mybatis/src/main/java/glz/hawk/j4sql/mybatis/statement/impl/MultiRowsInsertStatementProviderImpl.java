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

import glz.hawk.j4sql.mybatis.statement.MultiRowsInsertStatementProvider;

import javax.annotation.Nonnull;
import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotEmptyAndNoNulElement;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MultiRowsInsertStatementProviderImpl<T> implements MultiRowsInsertStatementProvider<T> {

    private final List<T> rows = new ArrayList<>();
    private final String statement;

    private MultiRowsInsertStatementProviderImpl(Builder<T> builder) {
        this.statement = builder.statement;
        this.rows.addAll(builder.rows);
    }

    public  static  <Row> Builder<Row> builder(String statement){
        return new Builder<>(statement);
    }

    public  static  <Row> Builder<Row> builder(String statement,Collection<Row> rows){
        return new Builder<>(statement,rows);
    }

    @Nonnull
    @Override
    public String getStatement() {
        return statement;
    }

    @Nonnull
    @Override
    public List<T> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public static class Builder<T>{
        private final List<T> rows =new ArrayList<>();
        private final String statement;
        private Builder(String statement){
            this(statement,Collections.emptyList());
        }
        private Builder(String statement,Collection<T> rows){
            this.statement = argNotBlank(statement);
            this.rows.addAll(argNotEmptyAndNoNulElement(rows,"rows"));
        }
        public Builder<T> add(T... rows){
            add(Arrays.asList(rows));
            return this;
        }

        public Builder<T> add(Collection<T> rows){
            this.rows.addAll(argNotEmptyAndNoNulElement(rows,"rows"));
            return this;
        }

        public MultiRowsInsertStatementProviderImpl<T> build(){
            return new MultiRowsInsertStatementProviderImpl<>(this);
        }
    }
}
