package glz.hawk.j4sql.condition.impl;

import glz.hawk.j4sql.condition.Condition;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

public class IgnorableCondition {
    public final Supplier<Condition> conditionSupplier;
    public final boolean acceptable;

    private IgnorableCondition(boolean acceptable, Supplier<Condition> conditionSupplier) {
        this.conditionSupplier = argNotNull(conditionSupplier, "conditionSupplier");
        this.acceptable = acceptable;
    }

    public static IgnorableCondition of(@Nonnull Supplier<Boolean> acceptableSupplier, @Nonnull Supplier<Condition> conditionSupplier) {
        boolean acceptable = argNotNull(acceptableSupplier, "acceptableSupplier").get() != null && acceptableSupplier.get();
        return new IgnorableCondition(acceptable, conditionSupplier);
    }

    public static IgnorableCondition of(boolean acceptable, @Nonnull Supplier<Condition> conditionSupplier) {
        return new IgnorableCondition(acceptable, conditionSupplier);
    }
}
