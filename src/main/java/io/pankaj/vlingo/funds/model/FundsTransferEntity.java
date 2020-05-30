package io.pankaj.vlingo.funds.model;

import io.pankaj.vlingo.funds.model.FundsTransfer.FundsTransferEvents.*;
import io.vlingo.common.Completes;
import io.vlingo.lattice.model.sourcing.EventSourced;

import java.util.function.BiConsumer;

public class FundsTransferEntity extends EventSourced implements FundsTransfer {
    private FundsTransferState state;

    // when new
    public FundsTransferEntity(String id) {
        super(id);
    }

    // when existing
    public FundsTransferEntity() {
        super();
    }

    @Override
    public Completes<String> initiate(FundsTransferCommand command) {
        if (state == null) {
            apply(new FundsTransferInitiated());
            command.from.debit(command);
        }
        return completes().with(streamName);
    }

    @Override
    public void amountDebited(FundsTransferCommand command) {
        apply(new AmountDebitedFromSource());
        command.to.credit(command);
    }

    @Override
    public void debitFailed(FundsTransferCommand command) {
         apply(new DebitFromSourceFailed());
    }

    @Override
    public void completed(FundsTransferCommand command) {
        apply(new FundsTransferCompleted());
    }

    @Override
    public void creditFailed(FundsTransferCommand command) {
        apply(new CreditToBeneficiaryFailed());
        apply(new RollingBackDebitFromSource());
        command.from.credit(command);
    }

    static {
        BiConsumer<FundsTransferEntity, FundsTransferInitiated> transferInitiated = FundsTransferEntity::applyCreated;
        EventSourced.registerConsumer(FundsTransferEntity.class, FundsTransferInitiated.class, transferInitiated);

        BiConsumer<FundsTransferEntity, AmountDebitedFromSource> amountDebited = FundsTransferEntity::applyAmountDebited;
        EventSourced.registerConsumer(FundsTransferEntity.class, AmountDebitedFromSource.class, amountDebited);

        BiConsumer<FundsTransferEntity, DebitFromSourceFailed> debitFailed = FundsTransferEntity::applyDebitFailed;
        EventSourced.registerConsumer(FundsTransferEntity.class, DebitFromSourceFailed.class, debitFailed);

        BiConsumer<FundsTransferEntity, FundsTransferCompleted> completed = FundsTransferEntity::applyCompleted;
        EventSourced.registerConsumer(FundsTransferEntity.class, FundsTransferCompleted.class, completed);

        BiConsumer<FundsTransferEntity, CreditToBeneficiaryFailed> creditFailed = FundsTransferEntity::applyCreditFailed;
        EventSourced.registerConsumer(FundsTransferEntity.class, CreditToBeneficiaryFailed.class, creditFailed);

        BiConsumer<FundsTransferEntity, RollingBackDebitFromSource> debitRolledBack = FundsTransferEntity::applyDebitRolledBack;
        EventSourced.registerConsumer(FundsTransferEntity.class, RollingBackDebitFromSource.class, debitRolledBack);
    }

    void applyCreated(FundsTransferInitiated event) {
        this.state = FundsTransferState.has(streamName);
    }

    void applyAmountDebited(AmountDebitedFromSource event) {
        this.state = FundsTransferState.debitSucceeded(streamName);
    }

    void applyDebitFailed(DebitFromSourceFailed event) {
        this.state = FundsTransferState.debitFailed(streamName);
    }

    void applyCompleted(FundsTransferCompleted event) {
        this.state = FundsTransferState.completed(streamName);
    }

    void applyCreditFailed(CreditToBeneficiaryFailed event) {
        this.state = FundsTransferState.creditFailed(streamName);
    }

    void applyDebitRolledBack(RollingBackDebitFromSource event) {
        this.state = FundsTransferState.debitRollbackInitiated(streamName);
    }
}
