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

import glz.hawk.j4sql.support.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class UpdateQueryImpl extends AbstractConditionSupport implements UpdateQuery {

    private final List<UpdateColumnValuePair> updateColumnValuePairs = new ArrayList<>();
    private final Map<String, UpdateColumnValuePair> filterMap = new HashMap<>();
    private SqlTable updateTable;

    @Nonnull
    @Override
    public SqlTable getUpdateTable() {
        return updateTable;
    }

    @Override
    public void addUpdateTable(PhysicalTable updateTable) {
        this.updateTable = argNotNull(updateTable, "updateTable");
    }

    @Override
    public void addUpdateTable(AliasedSqlTable<PhysicalTable> aliasedUpdateTable) {
        this.updateTable = argNotNull(aliasedUpdateTable, "aliasedUpdateTable");
    }

    @Override
    public void addSet(NamedColumn updateColumn, SqlColumn valueColumn) {
        addSetTemplate(updateColumn, valueColumn == null ? new DefaultValueColumn(NullValue.INSTANCE) : valueColumn, (c, v) -> new UpdateColumnValuePairImpl(c, (ValueColumn) v), c -> ((NamedColumn) c).getColumnName());
    }

    @Override
    public void addSet(NamedColumn updateColumn, Object value) {
        addSetTemplate(updateColumn, value, (c, v) -> new UpdateColumnValuePairImpl(c, new DefaultValueColumn(v == null ? NullValue.INSTANCE : v)), c -> ((NamedColumn) c).getColumnName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addSet(AliasedSelectColumn<NamedColumn> aliasedUpdateColumn, SqlColumn valueColumn) {
        addSetTemplate(aliasedUpdateColumn, valueColumn == null ? new DefaultValueColumn(NullValue.INSTANCE) : valueColumn, (c, v) -> new UpdateColumnValuePairImpl(c, (SqlColumn) v), c -> ((AliasedSelectColumn<NamedColumn>) c).getSourceColumn().getColumnName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addSet(AliasedSelectColumn<NamedColumn> aliasedUpdateColumn, Object value) {
        addSetTemplate(aliasedUpdateColumn, value, (c, v) -> new UpdateColumnValuePairImpl(c, new DefaultValueColumn(v == null ? NullValue.INSTANCE : v)), c -> ((AliasedSelectColumn<NamedColumn>) c).getSourceColumn().getColumnName());
    }

    private void addSetTemplate(SqlColumn updateColumn, Object value, BiFunction<SqlColumn, Object, UpdateColumnValuePairImpl> f1, Function<SqlColumn, String> f2) {
        UpdateColumnValuePairImpl updateColumnValuePair = f1.apply(updateColumn, value);
        String key = f2.apply(updateColumn);
        if (filterMap.putIfAbsent(key, updateColumnValuePair) != null) {
            throw new IllegalStateException(String.format("The %s exists in filterMap.", key));
        }
        updateColumnValuePairs.add(updateColumnValuePair);
    }

    @Nonnull
    @Override
    public List<UpdateColumnValuePair> getUpdateColumnValuePairs() {
        return Collections.unmodifiableList(updateColumnValuePairs);
    }

}
