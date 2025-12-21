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

import glz.hawk.j4sql.dsl.select.after.AfterSelectUnionClauseStep;

import javax.annotation.Nonnull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public interface SelectUnionStep {

    /**
     * Apply the @{code UNION }set operation.
     */
    @Nonnull
    AfterSelectUnionClauseStep union(Select select);

    /**
     * Apply the @{code UNION ALL }set operation.
     */
    @Nonnull
    AfterSelectUnionClauseStep unionAll(Select select);

    /**
     * Apply the <code>EXCEPT</code> (or <code>MINUS</code>) set operation.
     */
    @Nonnull
    AfterSelectUnionClauseStep except(Select select);

    /**
     * Apply the <code>EXCEPT ALL</code> set operation.
     */
    @Nonnull
    AfterSelectUnionClauseStep exceptAll(Select select);

    /**
     * Apply the <code>INTERSECT</code> set operation.
     */
    @Nonnull
    AfterSelectUnionClauseStep intersect(Select select);

    /**
     * Apply the <code>INTERSECT ALL</code> set operation.
     */
    @Nonnull
    AfterSelectUnionClauseStep intersectAll(Select select);
}
