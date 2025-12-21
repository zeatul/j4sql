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

package glz.hawk.j4sql.mybatis.writer;

import glz.hawk.j4sql.support.impl.DefaultBuilderContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MybatisBuilderContextImpl extends DefaultBuilderContext implements MybatisBuilderContext {

    /**
     * {@code DUMMY_INSTANCE} only can be used in scenarios where automatic SQL parameter generation is not required.
     */
    public static MybatisBuilderContextImpl DUMMY_INSTANCE = new MybatisBuilderContextImpl();

    private final String paramPrefix;
    private int parameterIndex = 0;
    private final Map<String, Object> parameters = new HashMap<>();

    public MybatisBuilderContextImpl(String paramPrefix) {
        this.paramPrefix = argNotBlank(paramPrefix, "paramPrefix");
    }

    private MybatisBuilderContextImpl() {
        this.paramPrefix = null;
    }

    @Override
    public String getParamPrefix() {
        return paramPrefix;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public String nextParameterName() {
        return "param" + parameterIndex++;
    }

    @Override
    public void addParameter(String parameterName, Object parameterValue) {
        if (parameters.putIfAbsent(parameterName, parameterValue) != null) {
            throw new IllegalArgumentException(String.format("Duplicated parameterName: %s", parameterName));
        }
    }
}
