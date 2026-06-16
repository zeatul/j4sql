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
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.Connector;
import glz.hawk.j4sql.condition.impl.CombinedCondition;
import glz.hawk.j4sql.condition.impl.DefaultConnectCondition;
import glz.hawk.j4sql.condition.impl.NotCondition;
import glz.hawk.j4sql.support.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SelectQueryImpl extends AbstractConditionSupport implements SelectQuery {
    private final List<SelectColumn> selectColumns = new ArrayList<>();
    private final List<SqlTable> fromTables = new ArrayList<>();
    private final List<SqlColumn> groupByColumns = new ArrayList<>();
    private final List<OrderColumn> orderByColumns = new ArrayList<>();
    private String hint = null;
    private SqlTable intoTable = null;
    private Condition havingCondition = null;
    private ValueColumn limit = null;
    private ValueColumn offset = null;
    private boolean forUpdate = false;
    private boolean noWait = false;
    private Integer secondsOfWait = null;
    private boolean count = false;
    private boolean distinct = false;

    @Override
    public void addSelect(SelectColumn... selectColumns) {
        this.selectColumns.addAll(Arrays.asList(argNoNullElement(selectColumns,"selectColumns")));
    }

    @Nonnull
    @Override
    public List<SelectColumn> getSelect() {
        return Collections.unmodifiableList(this.selectColumns);
    }

    @Override
    public void addFrom(SqlTable... fromTables) {
        this.fromTables.addAll(Arrays.asList(argNotEmptyAndNoNulElement(fromTables, "fromTables")));
    }

    @Nonnull
    @Override
    public List<SqlTable> getFrom() {
        return Collections.unmodifiableList(fromTables);
    }

    @Nullable
    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public void setHint(String hint) {
        this.hint = argNotBlank(hint, "hint");
    }

    @Override
    public SqlTable getInto() {
        return intoTable;
    }

    @Override
    public void setInto(SqlTable intoTable) {
        this.intoTable = argNotNull(intoTable, "intoTable");
    }


    protected void addHavingCondition(Connector connector, Condition condition, ConnectCondition... connectConditions){
        if (this.havingCondition == null) {
            throw new IllegalStateException("The havingCondition wasn't set before"); //TODO:输出sqlTable名
        }
        this.havingCondition = CombinedCondition.of(this.havingCondition, DefaultConnectCondition.of(connector,CombinedCondition.of(condition, connectConditions)));
    }

    protected void addHavingNotCondition(Connector connector, Condition condition, ConnectCondition... connectConditions){
        if (this.havingCondition == null) {
            throw new IllegalStateException("The havingCondition wasn't set before"); //TODO:输出sqlTable名
        }
        this.havingCondition = CombinedCondition.of(this.havingCondition, DefaultConnectCondition.and(NotCondition.of(CombinedCondition.of(condition, connectConditions))));
    }


    @Override
    public void addGroupBy(SelectColumn... groupByColumns) {
        this.groupByColumns.addAll(Arrays.asList(argNotEmptyAndNoNulElement(groupByColumns, "groupByColumns")));
    }

    @Nonnull
    @Override
    public List<SqlColumn> getGroupBy() {
        return Collections.unmodifiableList(groupByColumns);
    }

    @Override
    public void addHaving(Condition condition, ConnectCondition... connectConditions) {
        if (this.havingCondition != null) {
            throw new IllegalStateException("The havingCondition must be null!");
        }
        this.havingCondition = CombinedCondition.of(condition, connectConditions);
        this.conditionStep = ConditionStep.HAVING;
    }

    @Override
    public Condition getHaving() {
        return havingCondition;
    }

    @Override
    public void addOrderBy(OrderColumn... orderByColumns) {
        this.orderByColumns.addAll(Arrays.asList(argNotEmptyAndNoNulElement(orderByColumns, "orderByColumns")));
    }

    @Nonnull
    @Override
    public List<OrderColumn> getOrderBy() {
        return Collections.unmodifiableList(orderByColumns);
    }

    @Override
    public void addLimit(ValueColumn limit) {
        this.limit = limit;
    }

    @Override
    public ValueColumn getLimit() {
        return limit;
    }

    @Override
    public void addOffset(ValueColumn offset) {
        this.offset = offset;
    }

    @Override
    public ValueColumn getOffset() {
        return offset;
    }

    @Override
    public void setForUpdate() {
        this.forUpdate = true;
    }

    @Override
    public boolean isForUpdate() {
        return forUpdate;
    }

    @Override
    public void setNoWait() {
        if (!this.forUpdate) {
            throw new IllegalStateException("The forUpdate must be set before.");
        }
        this.noWait = true;
        this.secondsOfWait = null;
    }

    @Override
    public boolean isNoWait() {
        return noWait;
    }

    @Override
    public void setWait(int secondsOfWait) {
        if (!this.forUpdate) {
            throw new IllegalStateException("The forUpdate must be set before.");
        }
        this.secondsOfWait = argument(secondsOfWait, n -> n > 0, n -> "the secondsOfWait must be greater than zero.");
        this.noWait = false;
    }

    @Override
    public Integer getWait() {
        return secondsOfWait;
    }

    @Override
    public boolean isCount() {
        return count;
    }

    @Override
    public void setCount(boolean count) {
        this.count = count;
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
}
