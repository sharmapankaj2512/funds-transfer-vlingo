package io.pankaj.vlingo.funds.model;

import io.pankaj.vlingo.funds.model.FundsTransfer.FundsTransferCommand;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.stateful.StatefulEntity;

import static java.util.Collections.singletonList;

public class AccountEntity extends StatefulEntity<AccountState> implements Account {
    private AccountState state;

    // when new
    public AccountEntity(String id) {
        super(id);
    }

    // when existing
    public AccountEntity() {
        super();
    }

    @Override
    public Completes<AccountState> openFor(String userId) {
        if (state == null) {
            return applyHelper(AccountState.has(id, userId), new AccountOpened());
        } else {
            return completes().with(state);
        }
    }

    @Override
    public Completes<Outcome<RuntimeException, AccountState>> credit(float amount) {
        Outcome<RuntimeException, AccountState> outcome = state.deposit(amount);
        if (outcome instanceof Success)
            return applyHelper(outcome, new AmountCredited());
        else return completes().with(outcome);
    }

    @Override
    public Completes<Outcome<RuntimeException, AccountState>> debit(float amount) {
        Outcome<RuntimeException, AccountState> outcome = state.withdraw(amount);
        if (outcome instanceof Success)
            return applyHelper(outcome, new AmountDebited());
        else return completes().with(outcome);
    }

    @Override
    public void debit(FundsTransferCommand command) {
        Outcome<RuntimeException, AccountState> outcome = state.withdraw(command.amount);
        if (outcome instanceof Success) {
            apply(outcome.get(), singletonList(new AmountDebited()), AmountDebited.class.getSimpleName());
            command.transfer.amountDebited(command);
        } else command.transfer.debitFailed(command);
    }

    @Override
    public void credit(FundsTransferCommand command) {
        Outcome<RuntimeException, AccountState> outcome = state.deposit(command.amount);
        if (outcome instanceof Success) {
            apply(outcome.get(), singletonList(new AmountCredited()), AmountCredited.class.getSimpleName());
            command.transfer.completed(command);
        } else command.transfer.creditFailed(command);
    }

    @Override
    protected void state(AccountState state) {
        this.state = state;
    }

    @Override
    protected Class<AccountState> stateType() {
        return AccountState.class;
    }

    private Completes<AccountState> applyHelper(AccountState state, DomainEvent event) {
        return apply(state, singletonList(event), event.getClass().getSimpleName(), () -> state);
    }

    private Completes<Outcome<RuntimeException, AccountState>> applyHelper(
            Outcome<RuntimeException, AccountState> outcome, DomainEvent event) {
        return apply(outcome.get(), singletonList(event), event.getClass().getSimpleName(), () -> outcome);
    }
}
