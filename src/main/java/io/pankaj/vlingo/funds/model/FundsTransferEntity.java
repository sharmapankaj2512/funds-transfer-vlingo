package io.pankaj.vlingo.funds.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.Command;
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
    public Completes<FundsTransferState> initiate(FundsTransferCommand command) {
        if (state == null) {
            return apply(FundsTransferState.has(id), () -> {
                command.from.debit(command);
                return state;
            });
        } else {
            return completes().with(state);
        }
    }

    @Override
    public void amountDebited(FundsTransferCommand command) {
        apply(FundsTransferState.debitSucceeded(id));
        command.to.credit(command);
    }

    @Override
    public void debitFailed(FundsTransferCommand command) {
        apply(FundsTransferState.debitFailed(id));
    }

    @Override
    public void completed(FundsTransferCommand command) {
        apply(FundsTransferState.completed(id));
    }

    @Override
    public void creditFailed(FundsTransferCommand command) {
        apply(FundsTransferState.creditFailed(id));
    }
}
