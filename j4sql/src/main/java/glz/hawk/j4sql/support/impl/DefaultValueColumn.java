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

import glz.hawk.j4sql.support.ValueColumn;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Collection;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultValueColumn extends AbstractSqlColumn implements ValueColumn {

    private final Object value;

    public DefaultValueColumn(Object value) {
        this.value = argNotNull(value, "value");
        if (value.getClass().isArray() && Array.getLength(value) == 0) {
            throw new IllegalArgumentException("The value must not be empty.");
        }
        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
            throw new IllegalArgumentException("The value must not be empty.");
        }
        /**
         * TODO: 检查类型是否是简单类型，如果是集合，检查集合项是否简单类型
         */
    }

    @Nonnull
    @Override
    public Object getValue() {
        return value;
    }

}
