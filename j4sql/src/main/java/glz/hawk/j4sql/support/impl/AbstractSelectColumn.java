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

import glz.hawk.j4sql.support.AliasedSelectColumn;
import glz.hawk.j4sql.support.OrderColumn;
import glz.hawk.j4sql.support.SelectColumn;
import glz.hawk.j4sql.support.SortType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractSelectColumn extends AbstractSqlColumn implements SelectColumn {

    private final Map<String, Object> extensions = new HashMap<>();

    protected AbstractSelectColumn() {
    }

    protected AbstractSelectColumn(Map<String, Object> extensions) {
        if (extensions != null) this.extensions.putAll(extensions);
    }

    @Nonnull
    @Override
    public OrderColumn asc() {
        return new DefaultOrderColumn(this, SortType.ASC);
    }

    @Nonnull
    @Override
    public OrderColumn desc() {
        return new DefaultOrderColumn(this, SortType.DESC);
    }

    @Nonnull
    @Override
    public OrderColumn sortDefault() {
        return new DefaultOrderColumn(this, SortType.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <T extends SelectColumn> AliasedSelectColumn<T> as(String aliasName) {
        return new DefaultAliasedSelectColumn<>((T) this, aliasName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> getExtension(String key, Class<T> tClass) {
        return Optional.ofNullable((T) extensions.get(argNotNull(key, "key")));
    }

    @Override
    public Optional<Object> getExtension(String key) {
        return Optional.ofNullable(extensions.get(argNotNull(key, "key")));
    }
}
