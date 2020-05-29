package io.pankaj.vlingo.funds.model;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;
import io.vlingo.lattice.model.stateful.StatefulEntity;

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
            return apply(AccountState.has(id, userId), Operation.AccountOpened.name(), () -> state);
        } else {
            return completes().with(state);
        }
    }

    @Override
    public Completes<Outcome<RuntimeException, AccountState>> credit(float amount) {
        Outcome<RuntimeException, AccountState> outcome = state.deposit(amount);
        if (outcome instanceof Success)
            return apply(outcome.get(), Operation.AmountDeposited.name(), () -> outcome);
        else return completes().with(outcome);
    }

    @Override
    public Completes<Outcome<RuntimeException, AccountState>> debit(float amount) {
        Outcome<RuntimeException, AccountState> outcome = state.withdraw(amount);
        if (outcome instanceof Success)
            return apply(outcome.get(), Operation.AmountWithdrawn.name(), () -> outcome);
        else return completes().with(outcome);
    }

    @Override
    public void debit(float amount, FundsTransfer fundsTransfer, Account to) {
        Outcome<RuntimeException, AccountState> outcome = state.withdraw(amount);
        if (outcome instanceof Success) {
            apply(outcome.get(), Operation.AmountWithdrawn.name());
            fundsTransfer.amountDebited(to, amount);
        } else fundsTransfer.debitFailed();
    }

    @Override
    public void credit(float amount, FundsTransfer fundsTransfer) {
        Outcome<RuntimeException, AccountState> outcome = state.deposit(amount);
        if (outcome instanceof Success) {
            apply(outcome.get(), Operation.AmountDeposited.name());
            fundsTransfer.completed();
        } else fundsTransfer.creditFailed();
    }

    @Override
    protected void state(AccountState state) {
        this.state = state;
    }

    @Override
    protected Class<AccountState> stateType() {
        return AccountState.class;
    }
}
