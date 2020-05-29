package io.pankaj.vlingo.funds.model;

import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;

public interface Account {
    static Completes<AccountState> openFor(Stage stage, String userId) {
        Address address = stage.world().addressFactory().uniquePrefixedWith("a-");
        Account account = stage.actorFor(Account.class,
                Definition.has(
                        AccountEntity.class,
                        Definition.parameters(address.idString())), address);
        return account.openFor(userId);
    }

    Completes<AccountState> openFor(String userId);

    Completes<Outcome<RuntimeException, AccountState>> deposit(float amount);

    Completes<Outcome<RuntimeException, AccountState>> withdraw(float amount);

    enum Operation {
        AccountOpened,
        AmountDeposited,
        AmountWithdrawn;
    }
}
