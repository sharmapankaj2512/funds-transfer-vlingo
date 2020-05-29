package io.pankaj.vlingo.funds.model;

import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;

public interface FundsTransfer {
    static Completes<FundsTransferState> initiate(Stage stage, Account from, Account to, float amount) {
        Address address = stage.world().addressFactory().uniquePrefixedWith("a-");
        FundsTransfer transfer = stage.actorFor(FundsTransfer.class,
                Definition.has(
                        FundsTransferEntity.class,
                        Definition.parameters(address.idString())), address);
        return transfer.initiate(from, to, amount);
    }

    Completes<FundsTransferState> initiate(Account from, Account to, float amount);

    void amountDebited(Account to, float amount);

    void debitFailed();

    void completed();

    void creditFailed();
}
