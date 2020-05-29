package io.pankaj.vlingo.funds.model;

import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.DomainEvent;

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

    Completes<Outcome<RuntimeException, AccountState>> credit(float amount);

    Completes<Outcome<RuntimeException, AccountState>> debit(float amount);

    void debit(float balance, FundsTransfer fundsTransfer, Account to);

    void credit(float amount, FundsTransfer fundsTransfer);

    class AccountOpened extends DomainEvent {}
    class AmountCredited extends DomainEvent {}
    class AmountDebited extends DomainEvent {}
}
