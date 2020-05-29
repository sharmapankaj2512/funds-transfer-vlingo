// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.infra.AccountData;
import io.pankaj.vlingo.funds.model.AccountState;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;

import static io.pankaj.vlingo.funds.model.Account.Operation;

public class AccountProjectionActor extends StateStoreProjectionActor<AccountData> {
    private Operation becauseOf;

    public AccountProjectionActor() {
        super(QueryModelStoreProvider.instance().store);
    }

    @Override
    protected AccountData currentDataFor(Projectable projectable) {
        becauseOf = Operation.valueOf(projectable.becauseOf()[0]);
        final AccountState state = projectable.object();
        final AccountData current = AccountData.from(state);
        return current;
    }

    @Override
    protected AccountData merge(AccountData previousData, int previousVersion, AccountData currentData, int currentVersion) {
        AccountData merged;

        switch (becauseOf) {
            case AccountOpened:
                merged = currentData;
                break;
            case AmountDeposited:
                merged = AccountData.from(previousData.id, previousData.userId, currentData.balance);
                break;
            case AmountWithdrawn:
                merged = AccountData.from(previousData.id, previousData.userId, currentData.balance);
                break;
            default:
                merged = currentData;
        }

        return merged;
    }
}
