// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.infra.FundsTransferTransactionData;
import io.pankaj.vlingo.funds.model.FundsTransfer.FundsTransferEvents;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Source;

import java.util.ArrayList;
import java.util.List;

public class FundsTransferProjectionActor extends StateStoreProjectionActor<FundsTransferTransactionData> {
    private String dataId;
    private final List<FundsTransferEvents> events;
    private FundsTransferTransactionData Empty = FundsTransferTransactionData.empty();

    public FundsTransferProjectionActor() {
        super(QueryModelStoreProvider.instance().store);
        this.events = new ArrayList<>(2);
    }

    @Override
    protected FundsTransferTransactionData currentDataFor(Projectable projectable) {
        return Empty;
    }

    @Override
    protected String dataIdFor(final Projectable projectable) {
        dataId = events.get(0).id;
        return dataId;
    }

    @Override
    protected void prepareForMergeWith(final Projectable projectable) {
        events.clear();

        for (Entry<?> entry : projectable.entries()) {
            events.add(entryAdapter().anyTypeFromEntry(entry));
        }
    }

    @Override
    protected FundsTransferTransactionData merge(FundsTransferTransactionData previousData,
                                                 int previousVersion,
                                                 FundsTransferTransactionData currentData,
                                                 int currentVersion, List<Source<?>> sources) {
        if (previousVersion == currentVersion) return currentData;
        FundsTransferTransactionData merged = previousData != null ? previousData : Empty;
        for (final FundsTransferEvents event : events) {
            merged = merged.mergeWith(event.id, event.typeName());
        }
        return merged;
    }
}
