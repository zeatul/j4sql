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

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.support.SqlColumn;
import glz.hawk.j4sql.support.WhenTenPair;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WhenThenPairImpl implements WhenTenPair {

    private Condition whenCondition;
    private SqlColumn whenColumn;
    private SqlColumn thenColumn;

    @Override
    public Condition getWhenCondition() {
        return whenCondition;
    }

    @Override
    public SqlColumn getWhenColumn() {
        return whenColumn;
    }

    @Override
    public @NonNull SqlColumn getThen() {
        return thenColumn;
    }

    public void setWhenCondition(Condition whenCondition) {
        this.whenCondition = whenCondition;
        this.whenColumn = null;
    }

    public void setWhenColumn(SqlColumn whenColumn) {
        this.whenColumn = whenColumn;
        this.whenCondition = null;
    }

    public SqlColumn getThenColumn() {
        return thenColumn;
    }

    public void setThenColumn(SqlColumn thenColumn) {
        this.thenColumn = thenColumn;
    }
}
