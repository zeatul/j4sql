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

import glz.hawk.j4sql.dsl.insert.Insert;
import glz.hawk.j4sql.dsl.insert.InsertInsertIntoStep;
import glz.hawk.j4sql.dsl.insert.after.AfterInsertInsertIntoClauseStep;
import glz.hawk.j4sql.dsl.insert.after.AfterInsertSelectClauseStep;
import glz.hawk.j4sql.dsl.insert.after.AfterInsertValuesClauseStep;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.support.InsertQuery;
import glz.hawk.j4sql.support.NamedColumn;
import glz.hawk.j4sql.support.PhysicalTable;
import glz.hawk.j4sql.support.ValueColumn;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static glz.hawkframework.core.support.ArgumentSupport.argNotEmptyAndNoNulElement;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class InsertImpl implements Insert, InsertInsertIntoStep, AfterInsertInsertIntoClauseStep, AfterInsertValuesClauseStep, AfterInsertSelectClauseStep {

    private final InsertQuery insertQuery;

    public InsertImpl() {
        this.insertQuery = new InsertQueryImpl();
    }

    @Nonnull
    @Override
    public InsertQuery getInsertQuery() {
        return insertQuery;
    }

    @Override
    public AfterInsertInsertIntoClauseStep insertInto(PhysicalTable intoTable, NamedColumn... intoColumns) {
        insertQuery.addIntoTable(intoTable);
        insertQuery.addIntoColumns(intoColumns);
        return this;
    }

    @Override
    public AfterInsertSelectClauseStep select(Select select) {
        insertQuery.addSelect(select);
        return this;
    }

    @Override
    public AfterInsertValuesClauseStep values(Object... values) {
        insertQuery.addValues(Arrays.stream(values).map(value -> {
            if (value instanceof ValueColumn) {
                return (ValueColumn) value;
            } else {
                return new DefaultValueColumn(value);
            }
        }).toArray(ValueColumn[]::new));
        return this;
    }

    @Override
    public AfterInsertValuesClauseStep values(ValueColumn... values) {
        insertQuery.addValues(values);
        return this;
    }

    @Override
    public AfterInsertValuesClauseStep valueses(ValueColumn[]... valueses) {
        argNotEmptyAndNoNulElement(valueses, "values");
        for (ValueColumn[] values : valueses) {
            insertQuery.addValues(values);
        }
        return this;
    }

    @Nonnull
    @Override
    public Insert build() {
        return this;
    }
}
