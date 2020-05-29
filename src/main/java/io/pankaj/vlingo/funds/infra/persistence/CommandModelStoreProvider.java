// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.Stage;
import io.pankaj.vlingo.funds.model.AccountState;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;

import java.util.Arrays;

public class CommandModelStoreProvider {
    private static CommandModelStoreProvider instance;

    public final DispatcherControl dispatcherControl;
    public final StateStore store;

    public static CommandModelStoreProvider instance() {
        return instance;
    }

    @SuppressWarnings("rawtypes")
    public static CommandModelStoreProvider using(Stage stage, StatefulTypeRegistry registry, Dispatcher dispatcher) {
        if (instance != null) return instance;

        StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
        stateAdapterProvider.registerAdapter(AccountState.class, new AccountStateAdapter());
        new EntryAdapterProvider(stage.world()); // future

        Protocols storeProtocols =
                stage.actorFor(
                        new Class<?>[]{StateStore.class, DispatcherControl.class},
                        Definition.has(InMemoryStateStoreActor.class, Definition.parameters(Arrays.asList(dispatcher))));

        Protocols.Two<StateStore, DispatcherControl> storeWithControl = Protocols.two(storeProtocols);

        instance = new CommandModelStoreProvider(registry, storeWithControl._1, storeWithControl._2);

        return instance;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private CommandModelStoreProvider(StatefulTypeRegistry registry, StateStore store, DispatcherControl dispatcherControl) {
        this.store = store;
        this.dispatcherControl = dispatcherControl;

        registry.register(new Info(store, AccountState.class, AccountState.class.getSimpleName()));
    }
}
