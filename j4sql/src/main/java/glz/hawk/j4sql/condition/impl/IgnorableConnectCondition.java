package glz.hawk.j4sql.condition.impl;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.Connector;

import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

public class IgnorableConnectCondition {

    public final ConnectCondition connectCondition;
    /**
     * If the value of acceptable is not {@code true}, ignore the connectCondition.
     */
    public final boolean acceptable;

    private IgnorableConnectCondition(boolean acceptable, ConnectCondition connectCondition) {
        this.connectCondition = connectCondition;
        this.acceptable = acceptable;
    }

    public static IgnorableConnectCondition of(boolean acceptable, Connector connector, Supplier<Condition> conditionSupplier) {
        if (!acceptable) return new IgnorableConnectCondition(false, null);
        return new IgnorableConnectCondition(acceptable, DefaultConnectCondition.of(connector, conditionSupplier.get()));
    }
}
