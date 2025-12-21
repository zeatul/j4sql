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

package glz.hawk.j4sql.condition.impl;

import glz.hawk.j4sql.condition.Comparator;
import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.LikeEscapeStep;
import glz.hawk.j4sql.support.SqlColumn;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for defining compare conditions.
 *
 * @author Hawk
 */
public class CompareCondition implements Condition, LikeEscapeStep {

    private final SqlColumn leftColumn;
    private final SqlColumn rightColumn;
    private final Comparator comparator;
    private Character escape = null;

    private CompareCondition(SqlColumn leftColumn, SqlColumn rightColumn, Comparator comparator) {
        this.leftColumn = argNotNull(leftColumn, "leftColumn");
        this.rightColumn = argNotNull(rightColumn, "rightColumn");
        this.comparator = argNotNull(comparator, "comparator");
    }

    public static CompareCondition of(Comparator comparator, SqlColumn leftColumn, SqlColumn rightColumn) {
        return new CompareCondition(leftColumn, rightColumn, comparator);
    }

    public SqlColumn getLeftColumn() {
        return leftColumn;
    }

    public SqlColumn getRightColumn() {
        return rightColumn;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public Character getEscape() {
        return escape;
    }

    public void setEscape(Character escape) {
        this.escape = escape;
    }

    @Override
    public Condition escape(char c) {
        this.escape = c;
        return this;
    }
}
