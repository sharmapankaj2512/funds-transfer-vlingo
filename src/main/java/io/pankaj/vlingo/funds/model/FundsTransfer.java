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

    class FundsTransferEvents extends DomainEvent {
        public final String id;

        FundsTransferEvents(String id) {
            this.id = id;
        }

        public static class FundsTransferInitiated extends FundsTransferEvents {
            FundsTransferInitiated(String id) {
                super(id);
            }
        }

        public static class AmountDebitedFromSource extends FundsTransferEvents {
            AmountDebitedFromSource(String id) {
                super(id);
            }
        }

        public static class DebitFromSourceFailed extends FundsTransferEvents {
            DebitFromSourceFailed(String id) {
                super(id);
            }
        }

        public static class FundsTransferCompleted extends FundsTransferEvents {
            FundsTransferCompleted(String id) {
                super(id);
            }
        }

        public static class CreditToBeneficiaryFailed extends FundsTransferEvents {
            CreditToBeneficiaryFailed(String id) {
                super(id);
            }
        }

        public static class RollingBackDebitFromSource extends FundsTransferEvents {
            RollingBackDebitFromSource(String id) {
                super(id);
            }
        }
    }
}
