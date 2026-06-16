package glz.hawk.j4sql.util;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.impl.IgnorableCondition;
import glz.hawk.j4sql.condition.impl.IgnorableConnectCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

public class UpdateWrapper<T> {

    private final T updateObject;

    private final Condition  condition;

    private UpdateWrapper(Builder<T> builder){
        this.updateObject = argNotNull(builder.updateObject,"updateObject");
        this.condition = builder.condition;
    }

    public static <T> Builder<T> builder(){
        return new Builder<>();
    }

    public @Nonnull T getUpdateObject() {
        return updateObject;
    }

    public @Nullable Condition getCondition() {
        return condition;
    }

    public static class Builder<T>{
        private T updateObject;
        private Condition condition;

        public Builder<T> setUpdateObject(T updateObject){
            this.updateObject = updateObject;
            return this;
        }

        public Builder<T> setUpdateObject(Supplier<T> updateObjectSupplier){
            this.updateObject = updateObjectSupplier.get();
            return this;
        }

        public Builder<T> setCondition(Supplier<Condition> conditionSupplier) {
            this.condition = conditionSupplier.get();
            return this;
        }

        public Builder<T> setCondition(ConditionBuilder conditionBuilder) {
            this.condition = conditionBuilder.build();
            return this;
        }

        public Builder<T> setCondition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public Builder<T> setCondition(Condition condition, ConnectCondition... connectConditions) {
            this.condition = ConditionBuilder.builder(condition, connectConditions).build();
            return this;
        }

        public Builder<T> setCondition(Condition condition, IgnorableConnectCondition... ignorableConnectCondition) {
            this.condition = ConditionBuilder.builder(condition, ignorableConnectCondition).build();
            return this;
        }

        public Builder<T> setCondition(IgnorableCondition ignorableCondition, IgnorableConnectCondition... ignorableConnectCondition) {
            this.condition = ConditionBuilder.builder(ignorableCondition, ignorableConnectCondition).build();
            return this;
        }

        public UpdateWrapper<T> build(){
            return new UpdateWrapper<>(this);
        }
    }
}
