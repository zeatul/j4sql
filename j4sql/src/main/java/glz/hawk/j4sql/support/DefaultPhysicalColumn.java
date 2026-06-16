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

package glz.hawk.j4sql.support;

import glz.hawk.j4sql.support.impl.AbstractSelectColumn;
import glz.hawk.j4sql.support.impl.DefaultPhysicalTable;
import org.checkerframework.checker.nullness.qual.NonNull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultPhysicalColumn extends AbstractSelectColumn implements PhysicalColumn {

    private final PhysicalTable table;

    private final String columnName;

    public DefaultPhysicalColumn(PhysicalTable table, String columnName) {
        this.table = argNotNull(table, "table");
        this.columnName = argNotBlank(columnName, "columnName");
    }

    public DefaultPhysicalColumn(String schema, String tableName, String columnName) {
        this(new DefaultPhysicalTable(schema, tableName), columnName);
    }


    @Override
    public @NonNull PhysicalTable getTable() {
        return table;
    }

    @Override
    public @NonNull String getColumnName() {
        return columnName;
    }
}
