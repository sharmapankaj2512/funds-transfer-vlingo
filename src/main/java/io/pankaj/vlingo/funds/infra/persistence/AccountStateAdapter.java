// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.model.AccountState;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.StateAdapter;

public class AccountStateAdapter implements StateAdapter<AccountState, TextState> {

    @Override
    public int typeVersion() {
        return 1;
    }

    @Override
    public AccountState fromRawState(final TextState raw) {
        return JsonSerialization.deserialized(raw.data, raw.typed());
    }

    @Override
    public TextState toRawState(String id, AccountState state, int stateVersion, Metadata metadata) {
        final String serialization = JsonSerialization.serialized(state);
        return new TextState(id, AccountState.class, typeVersion(), serialization, stateVersion, metadata);
    }

    @Override
    public <ST> ST fromRawState(TextState raw, Class<ST> stateType) {
        return JsonSerialization.deserialized(raw.data, stateType);
    }
}
