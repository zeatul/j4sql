package glz.hawk.j4sql.support;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface CaseWhenQuery {

    @Nullable
    SqlColumn getCase();

    @Nonnull
    List<WhenTenPair> getWhenThenPairs();

    @Nullable
    SqlColumn getElse();

    void setCase(@Nonnull SqlColumn caseColumn);

    void setElse(@Nonnull SqlColumn elseColumn);

    void addWhenThenPair(@Nonnull WhenTenPair whenTenPair);

}
