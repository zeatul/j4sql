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

import glz.hawk.j4sql.support.Function;
import glz.hawk.j4sql.support.FunctionName;
import glz.hawk.j4sql.support.SqlColumn;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultFunction implements Function {

    private final FunctionName functionName;
    private final SqlColumn[] params;

    public DefaultFunction(FunctionName functionName, Object... params) {
        this.functionName = argNotNull(functionName, "functionName");
        this.params = params == null ? new SqlColumn[0] :
            Arrays.stream(params).map(p -> p instanceof SqlColumn ? (SqlColumn) p : new DefaultValueColumn(p)).toArray(SqlColumn[]::new);
    }

    @Nonnull
    @Override
    public FunctionName getFunctionName() {
        return functionName;
    }

    @Nonnull
    @Override
    public SqlColumn[] getParams() {
        return params;
    }
}
