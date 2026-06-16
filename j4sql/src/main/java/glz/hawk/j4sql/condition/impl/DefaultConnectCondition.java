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
import glz.hawk.j4sql.condition.Connector;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultConnectCondition implements ConnectCondition {

    private final Connector connector;
    private final Condition condition;

    private DefaultConnectCondition(Connector connector, Condition condition) {
        this.connector = argNotNull(connector, "connector");
        this.condition = argument(argNotNull(condition, "condition"), c -> !(c instanceof EmptyCondition), c -> "The condition must not be an EmptyCondition.");
    }

    public static DefaultConnectCondition and(Condition condition) {
        return new DefaultConnectCondition(Connector.AND, condition);
    }

    public static DefaultConnectCondition or(Condition condition) {
        return new DefaultConnectCondition(Connector.OR, condition);
    }

    public static DefaultConnectCondition of(Connector connector, Condition condition) {
        return new DefaultConnectCondition(connector, condition);
    }

    @Nonnull
    @Override
    public Connector getConnector() {
        return this.connector;
    }

    @Nonnull
    @Override
    public Condition getCondition() {
        return this.condition;
    }
}
