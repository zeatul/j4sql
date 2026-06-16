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

package glz.hawk.j4sql.dsl.select;

import glz.hawk.j4sql.dsl.select.after.AfterSelectFromClauseStep;
import glz.hawk.j4sql.dsl.select.after.AfterSelectHintClauseStep;
import glz.hawk.j4sql.support.SqlTable;

import javax.annotation.Nonnull;

/**
 * This interface defines the result of first step of SQL query: <code>SELECT(...)</code>
 *
 * @author Hawk
 */
public interface SelectFromStep {

    @Nonnull
    AfterSelectFromClauseStep from(SqlTable... sqlTables);

    @Nonnull
    AfterSelectFromClauseStep from(Select select);

    /**
     * Add an Oracle-style hint to the preceding select clause.
     */
    @Nonnull
    AfterSelectHintClauseStep hint(String hint);


}
