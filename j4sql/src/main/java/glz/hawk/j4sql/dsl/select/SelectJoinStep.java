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

import glz.hawk.j4sql.support.SqlTable;
import glz.hawk.j4sql.dsl.select.after.AfterSelectJoinClauseStep;

import javax.annotation.Nonnull;

/**
 * This interface is responsible for
 *
 * @author Hawk
 */
public interface SelectJoinStep {

    @Nonnull
    AfterSelectJoinClauseStep join(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep innerJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep crossJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep leftJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep leftOuterJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep rightJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep rightOuterJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep fullJoin(SqlTable joinSqlTable);

    @Nonnull
    AfterSelectJoinClauseStep fullOuterJoin(SqlTable joinSqlTable);


}
