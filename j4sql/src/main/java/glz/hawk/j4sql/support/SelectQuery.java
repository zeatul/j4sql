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

package glz.hawk.j4sql.support;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This interface defines the all required elements of a <code>SELECT</code> clause.
 *
 * @author Hawk
 */
public interface SelectQuery extends ConditionSupport{

    void addSelect(SelectColumn... selectColumns);

    void setCount(boolean count);

    boolean isCount();

    void setDistinct(boolean distinct);

    boolean isDistinct();

    @Nonnull
    List<SelectColumn> getSelect();

    void addFrom(SqlTable... fromTables);

    @Nonnull
    List<SqlTable> getFrom();

    @Nullable
    String getHint();

    void setHint(String hint);

    SqlTable getInto();

    void setInto(SqlTable intoTable);

    void addGroupBy(SelectColumn... groupByColumns);

    @Nonnull
    List<SqlColumn> getGroupBy();

    void addHaving(Condition condition, ConnectCondition... connectConditions);

    Condition getHaving();

    void addOrderBy(OrderColumn... orderByColumns);

    @Nonnull
    List<OrderColumn> getOrderBy();

    void addLimit(ValueColumn limit);

    ValueColumn getLimit();

    void addOffset(ValueColumn offset);

    ValueColumn getOffset();

    void setForUpdate();

    boolean isForUpdate();

    void setNoWait();

    boolean isNoWait();

    Integer getWait();

    void setWait(int secondsOfWait);

}
