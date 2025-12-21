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

import glz.hawk.j4sql.support.OrderColumn;
import glz.hawk.j4sql.support.SelectColumn;
import glz.hawk.j4sql.support.SortType;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultOrderColumn implements OrderColumn {

    private final SelectColumn selectColumn;
    private final SortType sortType;

    public DefaultOrderColumn(SelectColumn selectColumn, SortType sortType) {
        this.selectColumn = argNotNull(selectColumn, "selectColumn");
        this.sortType = argNotNull(sortType, "sortType");
    }

    @Nonnull
    @Override
    public SelectColumn getSelectColumn() {
        return selectColumn;
    }

    @Nonnull
    @Override
    public SortType getSortType() {
        return this.sortType;
    }
}
