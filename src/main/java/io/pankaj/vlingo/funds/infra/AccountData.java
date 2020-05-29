package io.pankaj.vlingo.funds.infra;

import io.pankaj.vlingo.funds.model.AccountState;

public class AccountData {
    public final String id;
    public final String userId;
    public final float balance;

    public AccountData(String id, String userId, float balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public static AccountData from(AccountState state) {
        return new AccountData(state.id, state.userId, state.balance);
    }

    public static AccountData from(String id, String userId, float balance) {
        return new AccountData(id, userId, balance);
    }

    public static AccountData empty() {
        return new AccountData("", "", 0);
    }
}
