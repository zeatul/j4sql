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
import glz.hawk.j4sql.dsl.cases.CaseCaseStep;
import glz.hawk.j4sql.dsl.cases.CaseWhen;
import glz.hawk.j4sql.dsl.cases.after.AfterCaseClauseStep;
import glz.hawk.j4sql.dsl.cases.after.AfterCaseElseClauseStep;
import glz.hawk.j4sql.dsl.cases.after.AfterCaseThenClauseStep;
import glz.hawk.j4sql.dsl.cases.after.AfterCaseWhenClauseStep;
import glz.hawk.j4sql.support.AliasedSelectColumn;
import glz.hawk.j4sql.support.CaseWhenColumn;
import glz.hawk.j4sql.support.CaseWhenQuery;
import glz.hawk.j4sql.support.SqlColumn;
import org.checkerframework.checker.nullness.qual.NonNull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

public class CaseWhenImpl implements CaseWhen, CaseCaseStep, AfterCaseClauseStep, AfterCaseWhenClauseStep, AfterCaseThenClauseStep, AfterCaseElseClauseStep {

    private WhenThenPairImpl whenThenPair;

    private final CaseWhenQuery caseWhenQuery;

    public CaseWhenImpl() {
        this.caseWhenQuery = new CaseWhenQueryImpl();
    }

    @Override
    public @NonNull CaseWhenQuery getCaseWhenQuery() {
        return caseWhenQuery;
    }

    @Override
    public @NonNull AfterCaseClauseStep cases(@NonNull SqlColumn column) {
        caseWhenQuery.setCase(column);
        return this;
    }

    @Override
    public @NonNull AfterCaseClauseStep cases() {
        return this;
    }

    @Override
    public @NonNull AfterCaseWhenClauseStep when(@NonNull Condition whenCondition) {
        argNotNull(whenCondition, "whenCondition");
        whenThenPair = new WhenThenPairImpl();
        whenThenPair.setWhenCondition(whenCondition);
        return this;
    }

    @Override
    public @NonNull AfterCaseWhenClauseStep when(@NonNull SqlColumn whenColumn) {
        argNotNull(whenColumn, "whenColumn");
        whenThenPair = new WhenThenPairImpl();
        whenThenPair.setWhenColumn(whenColumn);
        return this;
    }

    @Override
    public @NonNull AfterCaseThenClauseStep then(@NonNull SqlColumn thenColumn) {
        if (whenThenPair == null) throw new IllegalStateException("The filed['whenThePair'] should not be null now.");
        whenThenPair.setThenColumn(argNotNull(thenColumn, "thenColumn"));
        caseWhenQuery.addWhenThenPair(whenThenPair);
        whenThenPair = null;
        return this;
    }


    @Override
    public @NonNull AfterCaseElseClauseStep elses(@NonNull SqlColumn elseColumn) {
        caseWhenQuery.setElse(argNotNull(elseColumn, "elseColumn"));
        return this;
    }

    @Override
    public @NonNull CaseWhen build() {
        return this;
    }

    @Override
    public @NonNull CaseWhenColumn asColumn() {
        return new DefaultCaseWhenColumn(this);
    }

    @Override
    public @NonNull AliasedSelectColumn<CaseWhenColumn> asColumn(String aliasName) {
        return new DefaultAliasedSelectColumn<CaseWhenColumn>(new DefaultCaseWhenColumn(this), aliasName);
    }
}
