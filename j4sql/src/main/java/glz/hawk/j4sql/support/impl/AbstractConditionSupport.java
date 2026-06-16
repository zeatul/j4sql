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
import glz.hawk.j4sql.support.ConditionSupport;
import glz.hawk.j4sql.support.JoinType;
import glz.hawk.j4sql.support.SqlTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractConditionSupport implements ConditionSupport {
    private final List<JoinInfo> joinInfos = new ArrayList<>();
    protected ConditionStep conditionStep = null;
    private Condition whereCondition = null;

    public void addJoin(JoinType joinType, SqlTable joinTable) {
        joinInfos.add(new JoinInfo(joinTable, joinType));
    }

    public void addOn(Condition condition, ConnectCondition... connectConditions) {
        JoinInfo joinInfo = lastJoinInfo();
        if (joinInfo.getCondition() != null) {
            throw new IllegalStateException("The on condition was set before"); //TODO:输出sqlTable名
        }
        joinInfo.setCondition(CombinedCondition.of(condition, connectConditions));
        this.conditionStep = ConditionStep.ON;
    }

    @Nonnull
    public List<JoinInfo> getJoinInfos() {
        return Collections.unmodifiableList(joinInfos);
    }

    public void addCondition(Connector connector, Condition condition, ConnectCondition... connectConditions) {
        if (conditionStep == null) {
            throw new IllegalStateException("The conditionStep is null.");
        }

        switch (conditionStep) {
            case ON:
                JoinInfo joinInfo = lastJoinInfo();
                if (joinInfo.getCondition() == null) {
                    throw new IllegalStateException("The on condition wasn't set before"); //TODO:输出sqlTable名
                }
                joinInfo.setCondition(CombinedCondition.of(joinInfo.getCondition(), DefaultConnectCondition.of(connector, CombinedCondition.of(condition, connectConditions))));
                break;
            case WHERE:
                if (this.whereCondition == null) {
                    throw new IllegalStateException("The whereCondition wasn't set before"); //TODO:输出sqlTable名
                }
                this.whereCondition = CombinedCondition.of(this.whereCondition, DefaultConnectCondition.of(connector, CombinedCondition.of(condition, connectConditions)));
                break;
            case HAVING:
                addHavingCondition(connector, condition, connectConditions);
                break;
            default:
                throw new IllegalStateException(String.format("Unsupported conditionStep:%s", conditionStep));
        }
    }

    protected void addHavingCondition(Connector connector, Condition condition, ConnectCondition... connectConditions) {
        throw new UnsupportedOperationException();
    }

    public void addNotCondition(Connector connector, Condition condition, ConnectCondition... connectConditions) {
        if (conditionStep == null) {
            throw new IllegalStateException("The conditionStep is null.");
        }

        switch (conditionStep) {
            case ON:
                JoinInfo joinInfo = lastJoinInfo();
                if (joinInfo.getCondition() == null) {
                    throw new IllegalStateException("The on condition wasn't set before"); //TODO:输出sqlTable名
                }
                joinInfo.setCondition(CombinedCondition.of(joinInfo.getCondition(), DefaultConnectCondition.and(NotCondition.of(CombinedCondition.of(condition, connectConditions)))));
                break;
            case WHERE:
                if (this.whereCondition == null) {
                    throw new IllegalStateException("The whereCondition wasn't set before"); //TODO:输出sqlTable名
                }
                this.whereCondition = CombinedCondition.of(this.whereCondition, DefaultConnectCondition.and(NotCondition.of(CombinedCondition.of(condition, connectConditions))));
                break;
            case HAVING:
                addHavingNotCondition(connector, condition, connectConditions);
                break;
            default:
                throw new IllegalStateException(String.format("Unsupported conditionStep:%s", conditionStep));
        }
    }

    protected void addHavingNotCondition(Connector connector, Condition condition, ConnectCondition... connectConditions) {
        throw new UnsupportedOperationException();
    }

    public void addWhere(@Nullable Condition condition, ConnectCondition... connectConditions) {
        if (this.whereCondition != null) {
            throw new IllegalStateException("The whereCondition must be null!");
        }
        if (condition != null) {
            this.whereCondition = CombinedCondition.of(condition, connectConditions);
        }
        this.conditionStep = ConditionStep.WHERE;
    }

    public Condition getWhere() {
        return whereCondition;
    }


    protected JoinInfo lastJoinInfo() {
        if (joinInfos.isEmpty()) {
            throw new IllegalStateException("Found no joinSqlTable.");
        }
        return joinInfos.get(joinInfos.size() - 1);
    }
}
