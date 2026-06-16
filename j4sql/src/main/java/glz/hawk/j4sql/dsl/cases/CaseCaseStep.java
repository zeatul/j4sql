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

package glz.hawk.j4sql.dsl.cases;

import glz.hawk.j4sql.dsl.cases.after.AfterCaseClauseStep;
import glz.hawk.j4sql.support.SqlColumn;
import glz.hawk.j4sql.support.impl.DefaultValueColumn;
import glz.hawk.j4sql.support.impl.NullValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CaseCaseStep {
    @Nonnull
    AfterCaseClauseStep cases(@Nonnull SqlColumn column);

    @Nonnull
    default AfterCaseClauseStep cases(@Nullable Object value) {
        return cases(value == null ? new DefaultValueColumn(NullValue.INSTANCE) : new DefaultValueColumn(value));
    }

    @Nonnull
    AfterCaseClauseStep cases();
}
