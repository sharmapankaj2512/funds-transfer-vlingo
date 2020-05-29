// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.infra.AccountData;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.StateAdapter;

public class AccountDataStateAdapter implements StateAdapter<AccountData, TextState> {

  @Override
  public int typeVersion() {
    return 1;
  }

  @Override
  public TextState toRawState(String id, AccountData state, int stateVersion, Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(id, AccountData.class, typeVersion(), serialization, stateVersion, metadata);
  }

  @Override
  public TextState toRawState(AccountData state, int stateVersion, Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(state.id, AccountData.class, typeVersion(), serialization, stateVersion, metadata);
  }

  @Override
  public AccountData fromRawState(final TextState raw) {
    return JsonSerialization.deserialized(raw.data, raw.typed());
  }

  @Override
  public <ST> ST fromRawState(TextState raw, Class<ST> stateType) {
    return JsonSerialization.deserialized(raw.data, stateType);
  }
}
