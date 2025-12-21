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
import glz.hawk.j4sql.support.AliasedSelectColumn;
import glz.hawk.j4sql.support.SelectColumn;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultAliasedSelectColumn<T extends SelectColumn> extends AbstractSelectColumn implements AliasedSelectColumn<T> {

    private final T sourceColumn;
    private final Alias alias;

    public DefaultAliasedSelectColumn(T sourceColumn, Alias alias) {
        this.sourceColumn = argNotNull(sourceColumn, "selectColumn");
        this.alias = argNotNull(alias, "alias");
    }

    public DefaultAliasedSelectColumn(AliasedSelectColumn<T> aliasedSourceColumn, Alias alias) {
        this.sourceColumn = argNotNull(aliasedSourceColumn, "aliasedSourceColumn").getSourceColumn();
        this.alias = argNotNull(alias, "alias");
    }

    public DefaultAliasedSelectColumn(T sourceColumn, String aliasName) {
        this(sourceColumn, new DefaultAlias(aliasName));
    }

    @Nonnull
    @Override
    public Alias getAlias() {
        return alias;
    }

    @Override
    public T getSourceColumn() {
        return sourceColumn;
    }
}
