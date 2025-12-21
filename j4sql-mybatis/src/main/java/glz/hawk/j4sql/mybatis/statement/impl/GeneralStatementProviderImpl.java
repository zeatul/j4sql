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

import glz.hawk.j4sql.mybatis.statement.GeneralStatementProvider;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class GeneralStatementProviderImpl implements GeneralStatementProvider {

    private final String statement;
    private final Object params;

    private GeneralStatementProviderImpl(AbstractBuilder builder) {
        this.statement = builder.statement;
        this.params = builder.getParams();
    }

    /**
     * The param is an object.
     *
     * @param statement
     * @param params
     * @return
     */
    public static ObjectBuilder builder(String statement, Object params) {
        return new ObjectBuilder(statement, params);
    }

    /**
     * The param is map.
     *
     * @param statement
     * @return
     */
    public static MapBuilder builder(String statement) {
        return new MapBuilder(statement);
    }

    @Nonnull
    @Override
    public String getStatement() {
        return statement;
    }

    @Nonnull
    @Override
    public Object getParams() {
        return params;
    }

    public static abstract class AbstractBuilder {
        private final String statement;

        private AbstractBuilder(String statement) {
            this.statement = argNotBlank(statement, "statement");
        }

        protected abstract Object getParams();
    }

    public static class ObjectBuilder extends AbstractBuilder {
        private final Object params;

        public ObjectBuilder(String statement, Object params) {
            super(statement);
            this.params = argNotNull(params, "params");
        }

        @Override
        protected Object getParams() {
            return params;
        }
    }

    public static class MapBuilder extends AbstractBuilder {
        private final Map<String, Object> params = new HashMap<>();

        private MapBuilder(String statement) {
            super(statement);
        }

        @Override
        protected Object getParams() {
            return Collections.unmodifiableMap(params);
        }

        public MapBuilder addParam(String paramKey, Object paramValue) {
            if (params.put(argNotBlank(paramKey, "paramKey"), argNotNull(paramValue, "paramValue")) != null) {
                throw new IllegalArgumentException(String.format("The paramKey: %s was added before.", paramKey));
            }
            return this;
        }

        /**
         * If the value of {@code condition} is {@code false}, ignore adding param
         */
        public MapBuilder addParam(boolean condition, String paramKey, Object paramValue) {
            if (condition) {
                if (params.put(argNotBlank(paramKey, "paramKey"), argNotNull(paramValue, "paramValue")) != null) {
                    throw new IllegalArgumentException(String.format("The paramKey: %s was added before.", paramKey));
                }
            }
            return this;
        }

        public MapBuilder addParams(Map<String, Object> params) {
            argNotNull(params, "params").forEach(this::addParam);
            return this;
        }

        /**
         * If the value of {@code condition} is {@code false}, ignore adding params
         */
        public MapBuilder addParams(boolean condition, Map<String, Object> params) {
            if (condition) {
                argNotNull(params, "params").forEach(this::addParam);
            }
            return this;
        }

        public GeneralStatementProviderImpl build() {
            return new GeneralStatementProviderImpl(this);
        }

    }
}
