package glz.hawk.j4sql.util;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.impl.IgnorableCondition;
import glz.hawk.j4sql.condition.impl.IgnorableConnectCondition;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class DeleteWrapper {

    private final Condition condition;

    private DeleteWrapper(Builder builder) {
        this.condition = builder.condition;
    }

    public static Builder builder() {
        return new Builder();
    }

    public @Nullable Condition getCondition() {
        return condition;
    }

    public static class Builder {
        private Condition condition;

        public Builder setCondition(Supplier<Condition> conditionSupplier) {
            this.condition = conditionSupplier.get();
            return this;
        }

        public Builder setCondition(ConditionBuilder conditionBuilder) {
            this.condition = conditionBuilder.build();
            return this;
        }

        public Builder setCondition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public Builder setCondition(Condition condition, ConnectCondition... connectConditions) {
            this.condition = ConditionBuilder.builder(condition, connectConditions).build();
            return this;
        }

        public Builder setCondition(Condition condition, IgnorableConnectCondition... ignorableConnectCondition) {
            this.condition = ConditionBuilder.builder(condition, ignorableConnectCondition).build();
            return this;
        }

        public Builder setCondition(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectCondition) {
            this.condition = ConditionBuilder.builder(ignorableCondition, ignorableConnectCondition).build();
            return this;
        }

        public DeleteWrapper build() {
            return new DeleteWrapper(this);
        }
    }
}
