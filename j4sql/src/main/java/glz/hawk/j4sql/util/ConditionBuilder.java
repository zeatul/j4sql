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
import glz.hawk.j4sql.condition.impl.*;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 * <p>Build complex conditions</p>
 *
 * @author Hawk
 */
public class ConditionBuilder {

    private Condition condition;

    /**
     * Create an {@link ConditionBuilder} instance.
     *
     * @param condition         the required condition. Never {@code null}.
     * @param connectConditions the required connectConditions.
     */
    private ConditionBuilder(Condition condition, ConnectCondition... connectConditions) {
        if (connectConditions.length == 0) {
            this.condition = argument(argNotNull(condition, "condition"), c -> !(c instanceof EmptyCondition), c -> "The condition must not be an EmptyCondition.");
        } else {
            this.condition = CombinedCondition.of(condition, connectConditions);
        }
    }

    /**
     * Obtains an {@link ConditionBuilder} instance.
     *
     * @param condition the required condition. Never {@code null}.
     * @return a ConditionBuilder instance.
     */
    public static ConditionBuilder builder(Condition condition) {
        return new ConditionBuilder(condition);
    }

    /**
     * Obtains an {@link ConditionBuilder} instance.
     *
     * @param condition         the required condition. Never {@code null}.
     * @param connectConditions the required connectConditions.
     * @return a ConditionBuilder instance.
     */
    public static ConditionBuilder builder(Condition condition, ConnectCondition... connectConditions) {
        return new ConditionBuilder(condition, connectConditions);
    }

    /**
     * Obtains an {@link ConditionBuilder} instance.
     *
     * @param condition                  the required condition. Never {@code null}.
     * @param ignorableConnectConditions the required ignorableConnectConditions.
     * @return a ConditionBuilder instance.
     */
    public static ConditionBuilder builder(Condition condition, IgnorableConnectCondition... ignorableConnectConditions) {
        return new ConditionBuilder(condition, Arrays.stream(ignorableConnectConditions).filter(icc -> icc.acceptable).map(icc -> icc.connectCondition).toArray(ConnectCondition[]::new));
    }

    /**
     * Obtains an {@link ConditionBuilder} instance.
     * <p><b>WARNING:</b></p>
     * <p>If no condition found in any parameters, return a ConditionBuilder holds an {@link EmptyCondition}. It could produce bug, perhaps.</p>
     *
     * @param ignorableCondition         the required ignorableCondition
     * @param ignorableConnectConditions the required ignorableConnectConditions
     * @return a ConditionBuilder instance.
     */
    public static ConditionBuilder builder(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        Condition condition = ignorableCondition.acceptable ? ignorableCondition.conditionSupplier.get() : null;
        ConnectCondition[] connectConditions = Arrays.stream(ignorableConnectConditions).filter(icc -> icc.acceptable).map(icc -> icc.connectCondition).toArray(ConnectCondition[]::new);
        if (condition != null) {
            return new ConditionBuilder(condition, connectConditions);
        } else if (connectConditions.length == 0) {
            throw new IllegalArgumentException("Found no acceptable ignorableCondition or ignorableConnectCondition");
        } else if (connectConditions.length == 1) {
            return new ConditionBuilder(connectConditions[0].getCondition());
        } else {
            return new ConditionBuilder(connectConditions[0].getCondition(), Arrays.copyOfRange(connectConditions, 1, connectConditions.length));
        }
    }

    /**
     * Build a condition
     *
     * @return the condition
     */
    public @Nonnull Condition build() {
        return condition;
    }

    public @Nonnull ConditionBuilder and(@Nonnull Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.and(builder(condition, connectConditions).build()));
        return this;
    }

    public @Nonnull ConditionBuilder and(@Nonnull IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.and(builder(ignorableCondition, ignorableConnectConditions).build()));
        return this;
    }

    public @Nonnull ConditionBuilder andNot(@Nonnull Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.and(NotCondition.of(builder(condition, connectConditions).build())));
        return this;
    }

    public @Nonnull ConditionBuilder andNot(@Nonnull IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.and(NotCondition.of(builder(ignorableCondition, ignorableConnectConditions).build())));
        return this;
    }

    public @Nonnull ConditionBuilder or(@Nonnull Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.or(builder(condition, connectConditions).build()));
        return this;
    }

    public @Nonnull ConditionBuilder or(@Nonnull IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.or(builder(ignorableCondition, ignorableConnectConditions).build()));
        return this;
    }

    public @Nonnull ConditionBuilder orNot(@Nonnull Condition condition, ConnectCondition... connectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.or(NotCondition.of(builder(condition, connectConditions).build())));
        return this;
    }

    public ConditionBuilder orNot(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectConditions) {
        this.condition = CombinedCondition.of(this.condition, DefaultConnectCondition.or(NotCondition.of(builder(ignorableCondition, ignorableConnectConditions).build())));
        return this;
    }
}
