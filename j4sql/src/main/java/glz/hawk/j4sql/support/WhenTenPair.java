package glz.hawk.j4sql.support;

import glz.hawk.j4sql.condition.Condition;

import javax.annotation.Nonnull;

public interface WhenTenPair {
    /**
     * WhenCondition和WhenColumn是互斥的
     */
    Condition getWhenCondition();

    /**
     * WhenCondition和WhenColumn是互斥的
     */
    SqlColumn getWhenColumn();

    @Nonnull
    SqlColumn getThen();
}
