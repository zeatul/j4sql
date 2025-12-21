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

import glz.hawk.j4sql.dsl.select.after.AfterSelectOffsetClauseStep;
import glz.hawk.j4sql.support.ValueColumn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * This interface is responsible for
 *
 * @author Hawk
 */
public interface SelectOffsetStep {

    @Nonnull
    AfterSelectOffsetClauseStep offset(long offset);

    @Nonnull
    AfterSelectOffsetClauseStep offset(ValueColumn offset);

    /**
     * If the value of {@code conditionSupplier} is {@code false}, ignore offset.
     */
    @Nonnull
    AfterSelectOffsetClauseStep offset(@Nonnull Supplier<Boolean> conditionSupplier, Long offset);

    /**
     * If the value of {@code conditionSupplier} is {@code false}, ignore offset.
     */
    @Nonnull
    AfterSelectOffsetClauseStep offset(@Nonnull Supplier<Boolean> conditionSupplier, ValueColumn offset);

    /**
     * If the value of {@code condition} is {@code false}, ignore offset.
     */
    @Nonnull
    AfterSelectOffsetClauseStep offset(boolean condition, Long offset);

    /**
     * If the value of {@code condition} is {@code false}, ignore offset.
     */
    @Nonnull
    AfterSelectOffsetClauseStep offset(boolean condition, ValueColumn offset);
}
