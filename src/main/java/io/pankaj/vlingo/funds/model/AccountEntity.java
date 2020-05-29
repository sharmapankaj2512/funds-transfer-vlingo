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
    public Completes<Outcome<RuntimeException, AccountState>> deposit(float amount) {
        Outcome<RuntimeException, AccountState> outcome = state.deposit(amount);
        if (outcome instanceof Success)
            return apply(outcome.get(), Operation.AmountDeposited.name(), () -> outcome);
        else return completes().with(outcome);
    }

    @Override
    public Completes<Outcome<RuntimeException, AccountState>> withdraw(float amount) {
        Outcome<RuntimeException, AccountState> outcome = state.withdraw(amount);
        if (outcome instanceof Success)
            return apply(outcome.get(), Operation.AmountWithdrawn.name(), () -> outcome);
        else return completes().with(outcome);
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
