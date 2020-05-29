// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.vlingo.actors.Stage;
import io.pankaj.vlingo.funds.infra.AccountData;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;

import java.util.Arrays;

public class QueryModelStoreProvider {
    private static QueryModelStoreProvider instance;

    public final Queries queries;
    public final StateStore store;

    public static QueryModelStoreProvider instance() {
        return instance;
    }

    public static QueryModelStoreProvider using(final Stage stage, final StatefulTypeRegistry registry) {
        if (instance != null) return instance;

        final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
        stateAdapterProvider.registerAdapter(AccountData.class, new AccountDataStateAdapter());
        new EntryAdapterProvider(stage.world()); // future

        final StateStore store = stage.actorFor(StateStore.class, InMemoryStateStoreActor.class, Arrays.asList(new NoOpDispatcher()));

        final Queries queries = stage.actorFor(Queries.class, QueriesActor.class, store);

        instance = new QueryModelStoreProvider(registry, store, queries);

        return instance;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private QueryModelStoreProvider(final StatefulTypeRegistry registry, final StateStore store, final Queries queries) {
        this.store = store;
        this.queries = queries;

        registry.register(new Info(store, AccountData.class, AccountData.class.getSimpleName()));
    }
}
