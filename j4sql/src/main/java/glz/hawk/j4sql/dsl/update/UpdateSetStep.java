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

package glz.hawk.j4sql.dsl.update;

import glz.hawk.j4sql.dsl.update.after.AfterUpdateSetClauseStep;
import glz.hawk.j4sql.support.AliasedSelectColumn;
import glz.hawk.j4sql.support.NamedColumn;
import glz.hawk.j4sql.support.SqlColumn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public interface UpdateSetStep {

    @Nonnull
    AfterUpdateSetClauseStep set(NamedColumn updateColumn, SqlColumn valueColumn);

    @Nonnull
    AfterUpdateSetClauseStep set(NamedColumn updateColumn, Object value);

    @Nonnull
    AfterUpdateSetClauseStep set(AliasedSelectColumn<NamedColumn> aliasedUpdateColumn, SqlColumn valueColumn);

    @Nonnull
    AfterUpdateSetClauseStep set(AliasedSelectColumn<NamedColumn> aliasedUpdateColumn, Object value);

    @Nonnull
    AfterUpdateSetClauseStep set(Consumer<UpdateSetStep> consumer);
}
