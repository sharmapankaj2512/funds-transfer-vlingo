package io.pankaj.vlingo.funds.model;

import io.pankaj.vlingo.funds.infra.persistence.CommandModelStoreProvider;
import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

public class AccountEntityTest {
    private MockTextDispatcher dispatcher;
    private StatefulTypeRegistry registry;
    private StateStore store;
    private World world;
    private Account account;
    private final String UserId = "some-user-id";

    @Before
    public void setUpWorld() {
        world = World.startWithDefaults("stateful-entity");
        dispatcher = new MockTextDispatcher();

        registry = new StatefulTypeRegistry(world);
        store = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Arrays.asList(dispatcher));
        new CommandModelStoreProvider(world.stage(), registry, store, null);
        account = world.stage().actorFor(Account.class,
                Definition.has(AccountEntity.class, Definition.parameters(UUID.randomUUID().toString())));
    }

    @After
    public void tearDownWorld() {
        world.terminate();
    }

    @Test
    public void openFor_shouldOpenAnUserAccountWithZeroBalance() {
        AccountState state = account.openFor(UserId).await();

        Assertions.assertThat(state.userId).isEqualTo(UserId);
        Assertions.assertThat(state.balance).isEqualTo(0);
    }

    @Test
    public void credit_shouldIncreaseBalanceByGivenAmount() {
        account.openFor(UserId).await();
        Outcome<RuntimeException, AccountState> outcome = account.credit(10).await();
        float balanceAfterCredit = outcome.get().balance;

        Assertions.assertThat(balanceAfterCredit).isEqualTo(10);
    }

    @Test
    public void debit_shouldDecreaseBalanceByGivenAmount() {
        account.openFor(UserId).await();
        account.credit(10).await();
        Outcome<RuntimeException, AccountState> outcome = account.debit(10).await();

        float balanceAfterDebit = outcome.get().balance;

        Assertions.assertThat(balanceAfterDebit).isEqualTo(0);
    }

}