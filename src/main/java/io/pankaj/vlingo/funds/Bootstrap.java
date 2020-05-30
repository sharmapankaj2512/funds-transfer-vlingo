// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds;

import io.pankaj.vlingo.funds.infra.persistence.CommandModelJournalProvider;
import io.pankaj.vlingo.funds.infra.persistence.CommandModelStoreProvider;
import io.pankaj.vlingo.funds.infra.persistence.ProjectionDispatcherProvider;
import io.pankaj.vlingo.funds.infra.persistence.QueryModelStoreProvider;
import io.pankaj.vlingo.funds.resource.AccountResource;
import io.pankaj.vlingo.funds.resource.FundsTransferResource;
import io.vlingo.actors.World;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;

/**
 * Start the service with a Server.
 */
public class Bootstrap {
    private static int Port = 18080;

    public static void main(String[] args) {
        World world = World.startWithDefaults("funds-transfer");

        StatefulTypeRegistry registry = new StatefulTypeRegistry(world);
        SourcedTypeRegistry sourcedTypeRegistry = new SourcedTypeRegistry(world);

        QueryModelStoreProvider.using(world.stage(), registry);
        CommandModelStoreProvider.using(world.stage(), registry, ProjectionDispatcherProvider.using(world.stage()).storeDispatcher);
        CommandModelJournalProvider.using(world.stage(), sourcedTypeRegistry, ProjectionDispatcherProvider.using(world.stage()).storeDispatcher);

        AccountResource accountResource = new AccountResource(world);
        FundsTransferResource transferResource = new FundsTransferResource(world);

        Server server =
                Server.startWithAgent(
                        world.stage(),
                        Resources.are(
                                accountResource.routes(),
                                transferResource.routes()),
                        Port,
                        2);

        world.defaultLogger().info("============================================");
        world.defaultLogger().info("Started funds-transfer service on port " + Port);
        world.defaultLogger().info("============================================");
    }

}
