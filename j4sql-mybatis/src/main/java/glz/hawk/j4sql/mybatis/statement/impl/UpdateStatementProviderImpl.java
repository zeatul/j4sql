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

import glz.hawk.j4sql.mybatis.statement.UpdateStatementProvider;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class UpdateStatementProviderImpl implements UpdateStatementProvider {

    private final String statement;
    private final Object update;
    private final Object params;

    private UpdateStatementProviderImpl(AbstractBuilder builder){
        this.statement = builder.statement;
        this.update = builder.update;
        this.params = builder.getParams();
    }

    public static ObjectBuilder builder(String statement,Object update,Object params){
        return new ObjectBuilder(statement,update,params);
    }

    public static MapBuilder builder(String statement,Object update){
        return new MapBuilder(statement,update);
    }

    @Nonnull
    @Override
    public String getStatement() {
        return statement;
    }

    @Nonnull
    @Override
    public Object getUpdate() {
        return update;
    }

    @Nonnull
    @Override
    public Object getParams() {
        return params;
    }

    public static abstract class AbstractBuilder{
        private final String statement;
        private final Object update;
        public AbstractBuilder(String statement,Object update){
            this.statement = argNotBlank(statement,"statement");
            this.update = argNotNull(update,"update");
        }
        protected abstract Object getParams();
    }

    public static class ObjectBuilder extends AbstractBuilder{
        private final Object params;
        public ObjectBuilder(String statement, Object update,Object params) {
            super(statement, update);
            this.params = argNotNull(params);
        }

        @Override
        protected Object getParams() {
            return params;
        }

        public UpdateStatementProviderImpl build(){
            return new UpdateStatementProviderImpl(this);
        }
    }

    public static class MapBuilder extends AbstractBuilder{

        private Map<String,Object> params = new HashMap<>();

        public MapBuilder(String statement, Object update) {
            super(statement, update);
        }

        @Override
        protected Object getParams() {
            return Collections.unmodifiableMap(params);
        }

        public MapBuilder addParam(String paramKey, Object paramValue){
            if (params.put(argNotBlank(paramKey, "paramKey"), argNotNull(paramValue, "paramValue")) != null) {
                throw new IllegalArgumentException(String.format("The paramKey: %s was added before.", paramKey));
            }
            return this;
        }

        public MapBuilder addParams(Map<String, Object> params) {
            argNotNull(params, "params").forEach(this::addParam);
            return this;
        }

        public UpdateStatementProviderImpl build(){
            return new UpdateStatementProviderImpl(this);
        }

    }
}
