// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.model.Account;
import io.pankaj.vlingo.funds.model.FundsTransfer;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.Stage;
import io.vlingo.lattice.model.projection.ProjectionDispatcher;
import io.vlingo.lattice.model.projection.ProjectionDispatcher.ProjectToDescription;
import io.vlingo.lattice.model.projection.TextProjectionDispatcherActor;
import io.vlingo.symbio.store.dispatch.Dispatcher;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ProjectionDispatcherProvider {
    private static ProjectionDispatcherProvider instance;

    public final ProjectionDispatcher projectionDispatcher;
    public final Dispatcher storeDispatcher;

    public static ProjectionDispatcherProvider instance() {
        return instance;
    }

    public static ProjectionDispatcherProvider using(final Stage stage) {
        if (instance != null) return instance;

        final List<ProjectToDescription> descriptions =
                Arrays.asList(
                        new ProjectToDescription(
                                AccountProjectionActor.class,
                                Account.AccountOpened.class.getSimpleName(),
                                Account.AmountCredited.class.getSimpleName(),
                                Account.AmountDebited.class.getSimpleName()),
                        new ProjectToDescription(
                                FundsTransferProjectionActor.class,
                                FundsTransfer.FundsTransferEvents.FundsTransferInitiated.class.getName(),
                                FundsTransfer.FundsTransferEvents.AmountDebitedFromSource.class.getName(),
                                FundsTransfer.FundsTransferEvents.DebitFromSourceFailed.class.getName(),
                                FundsTransfer.FundsTransferEvents.FundsTransferCompleted.class.getName(),
                                FundsTransfer.FundsTransferEvents.CreditToBeneficiaryFailed.class.getName(),
                                FundsTransfer.FundsTransferEvents.RollingBackDebitFromSource.class.getName()));

        final Protocols dispatcherProtocols =
                stage.actorFor(
                        new Class<?>[]{Dispatcher.class, ProjectionDispatcher.class},
                        Definition.has(TextProjectionDispatcherActor.class, Definition.parameters(descriptions)));

        final Protocols.Two<Dispatcher, ProjectionDispatcher> dispatchers = Protocols.two(dispatcherProtocols);

        return new ProjectionDispatcherProvider(dispatchers._1, dispatchers._2);
    }

    private ProjectionDispatcherProvider(final Dispatcher storeDispatcher, final ProjectionDispatcher projectionDispatcher) {
        this.storeDispatcher = storeDispatcher;
        this.projectionDispatcher = projectionDispatcher;
    }
}
