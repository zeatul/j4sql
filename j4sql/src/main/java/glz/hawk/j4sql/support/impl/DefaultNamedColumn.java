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

import glz.hawk.j4sql.support.NamedColumn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultNamedColumn extends AbstractSelectColumn implements NamedColumn {

    private final String tableName;
    private final String columnName;

    public DefaultNamedColumn(String tableName, String columnName) {
        this.tableName = argNotBlank(tableName, "tableName");
        this.columnName = argNotBlank(columnName, "columnName");
    }

    public DefaultNamedColumn(String columnName) {
        this.tableName = null;
        this.columnName = columnName;
    }

    public DefaultNamedColumn(String tableName, String columnName, Map<String, Object> extensions) {
        super(extensions);
        this.tableName = argNotBlank(tableName, "tableName");
        this.columnName = argNotBlank(columnName, "columnName");
    }

    public DefaultNamedColumn(String columnName, Map<String, Object> extensions) {
        super(extensions);
        this.tableName = null;
        this.columnName = columnName;
    }

    @Nullable
    @Override
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    @Override
    public String getColumnName() {
        return columnName;
    }
}
