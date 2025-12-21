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

import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.support.InsertQuery;
import glz.hawk.j4sql.support.NamedColumn;
import glz.hawk.j4sql.support.PhysicalTable;
import glz.hawk.j4sql.support.ValueColumn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class InsertQueryImpl implements InsertQuery {

    private final List<NamedColumn> intoColumns = new ArrayList<>();
    private final List<List<ValueColumn>> valueColumnses = new ArrayList<>();
    private PhysicalTable intoTable;
    private Select select;

    @Nonnull
    @Override
    public PhysicalTable getIntoTable() {
        return intoTable;
    }

    @Override
    public void addIntoTable(PhysicalTable intoTable) {
        this.intoTable = argNotNull(intoTable, "intoTable");
    }

    @Nonnull
    @Override
    public List<NamedColumn> getIntoColumns() {
        return Collections.unmodifiableList(intoColumns);
    }

    @Override
    public void addIntoColumns(NamedColumn... intoColumns) {
        this.intoColumns.addAll(Arrays.asList(argNoNullElement(intoColumns, "intoColumns")));
    }

    @Nonnull
    @Override
    public List<List<ValueColumn>> getValueColumnses() {
        return Collections.unmodifiableList(valueColumnses);
    }

    @Override
    public void addValues(ValueColumn... valueColumns) {
        List<ValueColumn> valueList = Arrays.asList(argNotEmptyAndNoNulElement(valueColumns, "valueColumns"));
        argument(valueList, v -> v.size() == intoColumns.size(), v -> "The size of valueColumns must be equal to the size of intoColumns");
        valueColumnses.add(valueList);
    }

    @Nonnull
    @Override
    public Select getSelect() {
        return select;
    }

    @Override
    public void addSelect(Select select) {
        this.select = argNotNull(select,"select");
    }
}
