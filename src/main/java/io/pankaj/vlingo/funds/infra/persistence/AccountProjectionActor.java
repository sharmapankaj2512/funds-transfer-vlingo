// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.infra.AccountData;
import io.pankaj.vlingo.funds.model.Account.AccountOpened;
import io.pankaj.vlingo.funds.model.Account.AmountCredited;
import io.pankaj.vlingo.funds.model.Account.AmountDebited;
import io.pankaj.vlingo.funds.model.AccountState;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;

public class AccountProjectionActor extends StateStoreProjectionActor<AccountData> {
    private String becauseOf;

    public AccountProjectionActor() {
        super(QueryModelStoreProvider.instance().store);
    }

    @Override
    protected AccountData currentDataFor(Projectable projectable) {
        becauseOf = projectable.becauseOf()[0];
        final AccountState state = projectable.object();
        final AccountData current = AccountData.from(state);
        return current;
    }

    @Override
    protected AccountData merge(AccountData previousData, int previousVersion, AccountData currentData, int currentVersion) {
        AccountData merged;
        if (AccountOpened.class.getSimpleName().equals(becauseOf)) {
            merged = currentData;
        } else if (AmountCredited.class.getSimpleName().equals(becauseOf)) {
            merged = AccountData.from(previousData.id, previousData.userId, currentData.balance);
        } else if (AmountDebited.class.getSimpleName().equals(becauseOf)) {
            merged = AccountData.from(previousData.id, previousData.userId, currentData.balance);
        } else {
            merged = currentData;
        }

        return merged;
    }
}
