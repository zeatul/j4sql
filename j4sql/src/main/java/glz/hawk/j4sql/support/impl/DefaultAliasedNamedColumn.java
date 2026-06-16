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

import glz.hawk.j4sql.support.Alias;
import glz.hawk.j4sql.support.AliasedNamedColumn;
import glz.hawk.j4sql.support.AliasedSelectColumn;
import glz.hawk.j4sql.support.NamedColumn;

import java.util.Map;
import java.util.Optional;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultAliasedNamedColumn extends DefaultAliasedSelectColumn<NamedColumn> implements AliasedNamedColumn {
    public DefaultAliasedNamedColumn(NamedColumn sourceColumn, Alias alias) {
        super(sourceColumn, alias);
    }

    public DefaultAliasedNamedColumn(AliasedSelectColumn<NamedColumn> aliasedSourceColumn, Alias alias) {
        super(aliasedSourceColumn, alias);
    }

    public DefaultAliasedNamedColumn(NamedColumn sourceColumn, String aliasName) {
        super(sourceColumn, aliasName);
    }

    public DefaultAliasedNamedColumn(String columnName, String aliasName) {
        super(new DefaultNamedColumn(columnName), aliasName);
    }

    public DefaultAliasedNamedColumn(String tableName, String columnName, String aliasName) {
        super(new DefaultNamedColumn(tableName, columnName), aliasName);
    }

    public DefaultAliasedNamedColumn(String columnName, String aliasName, Map<String, Object> extensions) {
        super(new DefaultNamedColumn(columnName, extensions), aliasName);
    }

    public DefaultAliasedNamedColumn(String tableName, String columnName, String aliasName, Map<String, Object> extensions) {
        super(new DefaultNamedColumn(tableName, columnName, extensions), aliasName);
    }

    @Override
    public <T> Optional<T> getExtension(String key, Class<T> tClass) {
        return getSourceColumn().getExtension(key, tClass);
    }

    @Override
    public Optional<Object> getExtension(String key) {
        return getSourceColumn().getExtension(key);
    }
}
