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
import glz.hawk.j4sql.support.NamedColumn;
import glz.hawk.j4sql.support.SqlColumn;
import glz.hawk.j4sql.support.UpdateColumnValuePair;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class UpdateColumnValuePairImpl implements UpdateColumnValuePair {
    private final SqlColumn updateColumn;
    private final SqlColumn valueColumn;

    public UpdateColumnValuePairImpl(SqlColumn updateColumn, SqlColumn valueColumn) {
        this.updateColumn = checkUpdateColumn(updateColumn);
        this.valueColumn = argNotNull(valueColumn,"valueColumn");
    }

    @Nonnull
    @Override
    public SqlColumn getUpdateColumn() {
        return updateColumn;
    }

    @Nonnull
    @Override
    public SqlColumn getValueColumn() {
        return valueColumn;
    }

    private SqlColumn checkUpdateColumn(SqlColumn updateColumn){
        argNotNull(updateColumn,"updateColumn");
        if (updateColumn instanceof NamedColumn) {
            return updateColumn;
        }
        if (updateColumn instanceof AliasedSelectColumn && ((AliasedSelectColumn<?>)updateColumn).getSourceColumn() instanceof  NamedColumn){
            return  updateColumn;
        }
        throw new UnsupportedOperationException("The parameter[updateColumn] must be instance of NamedColumn or AliasedSelectColumn<NamedColumn>");
    }
}
