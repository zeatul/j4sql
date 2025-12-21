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

package glz.hawk.j4sql.util;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.Connector;
import glz.hawk.j4sql.condition.impl.CombinedCondition;
import glz.hawk.j4sql.condition.impl.DefaultConnectCondition;
import glz.hawk.j4sql.condition.impl.NotCondition;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ConditionBuilder {

    private Condition condition;

    private ConditionBuilder(Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(condition, connectConditions);
    }

    public static ConditionBuilder builder(Condition condition, ConnectCondition... connectConditions) {
        return new ConditionBuilder(condition, connectConditions);
    }

    public Condition build() {
        return condition;
    }

    public ConditionBuilder and(Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.of(Connector.AND, CombinedCondition.of(condition, connectConditions)));
        return this;
    }

    public ConditionBuilder andNot(Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.and(NotCondition.of(CombinedCondition.of(condition, connectConditions))));
        return this;
    }

    public ConditionBuilder or(Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.of(Connector.OR, CombinedCondition.of(condition, connectConditions)));
        return this;
    }

    public ConditionBuilder orNot(Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.or(NotCondition.of(CombinedCondition.of(condition, connectConditions))));
        return this;
    }
}
