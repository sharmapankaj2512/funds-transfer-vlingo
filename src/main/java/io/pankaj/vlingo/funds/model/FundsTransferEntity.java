package io.pankaj.vlingo.funds.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.stateful.StatefulEntity;

public class FundsTransferEntity extends StatefulEntity<FundsTransferState> implements FundsTransfer {
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
    protected void state(FundsTransferState state) {
        this.state = state;
    }

    @Override
    protected Class<FundsTransferState> stateType() {
        return FundsTransferState.class;
    }

    @Override
    public Completes<FundsTransferState> initiate(Account from, Account to, float amount) {
        if (state == null) {
            return apply(FundsTransferState.has(id), () -> {
                from.debit(amount, selfAs(FundsTransfer.class), to);
                return state;
            });
        } else {
            return completes().with(state);
        }
    }

    @Override
    public void amountDebited(Account to, float amount) {
        apply(FundsTransferState.debitSucceeded(id));
        to.credit(amount, selfAs(FundsTransfer.class));
    }

    @Override
    public void debitFailed() {
        apply(FundsTransferState.debitFailed(id));
    }

    @Override
    public void completed() {
        apply(FundsTransferState.completed(id));
    }

    @Override
    public void creditFailed() {
        apply(FundsTransferState.creditFailed(id));
    }
}
