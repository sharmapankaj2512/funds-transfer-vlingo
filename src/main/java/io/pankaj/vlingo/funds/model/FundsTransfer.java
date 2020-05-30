package io.pankaj.vlingo.funds.model;

import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.lattice.model.Command;
import io.vlingo.lattice.model.DomainEvent;

public interface FundsTransfer {
    static Completes<String> initiate(Stage stage, Account from, Account to, float amount) {
        Address address = stage.world().addressFactory().uniquePrefixedWith("a-");
        FundsTransfer transfer = stage.actorFor(FundsTransfer.class,
                Definition.has(
                        FundsTransferEntity.class,
                        Definition.parameters(address.idString())), address);
        return transfer.initiate(new FundsTransferCommand(from, to, amount, transfer));
    }

    Completes<String> initiate(FundsTransferCommand command);

    void amountDebited(FundsTransferCommand command);

    void debitFailed(FundsTransferCommand command);

    void completed(FundsTransferCommand command);

    void creditFailed(FundsTransferCommand command);

    class FundsTransferCommand extends Command {
        public final Account from;
        public final Account to;
        public final float amount;
        public final FundsTransfer transfer;

        public FundsTransferCommand(Account from, Account to, float amount, FundsTransfer transfer) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.transfer = transfer;
        }
    }

    class FundsTransferEvents {
        static class FundsTransferInitiated extends DomainEvent {}
        static class AmountDebitedFromSource extends DomainEvent {}
        static class DebitFromSourceFailed extends DomainEvent {}
        static class FundsTransferCompleted extends DomainEvent {}
        static class CreditToBeneficiaryFailed extends DomainEvent {}
        static class RollingBackDebitFromSource extends DomainEvent {}
    }
}
