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

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawkframework.core.support.ArgumentSupport;

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
public class CombinedCondition implements Condition {

    private final Condition condition;

    private final List<ConnectCondition> connectConnections = new ArrayList<>();

    private CombinedCondition(Condition condition, ConnectCondition... connectConditions) {
        this.condition = condition;
        this.connectConnections.addAll(Arrays.asList(connectConditions));
    }

    public static Condition of(Condition condition, ConnectCondition... connectConditions) {
        argument(argNotNull(condition, "condition"), c -> !(c instanceof EmptyCondition), c -> "The condition must not be an EmptyCondition.");
        if (connectConditions.length == 0) return condition;
        return new CombinedCondition(condition,  argNotEmptyAndNoNulElement(connectConditions, "connectConditions"));
    }


    public Condition getCondition() {
        return this.condition;
    }

    public List<ConnectCondition> getConnectConnections() {
        return Collections.unmodifiableList(connectConnections);
    }
}
