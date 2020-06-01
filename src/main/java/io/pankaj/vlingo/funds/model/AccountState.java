package io.pankaj.vlingo.funds.model;

import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;

public class AccountState {
    public final String id;
    public final String userId;
    public final float balance;

    private static final int INITIAL_BALANCE = 0;

    public AccountState(String id, String userId, float balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public AccountState(String id, String userId) {
        this(id, userId, INITIAL_BALANCE);
    }

    public static AccountState has(String id, String userId) {
        return new AccountState(id, userId);
    }

    public Outcome<RuntimeException, AccountState> deposit(float amount) {
        if (amount <= 0)
            return Failure.of(new IllegalArgumentException("Invalid amount"));
        return Success.of(new AccountState(id, userId, balance + amount));
    }

    public Outcome<RuntimeException, AccountState> withdraw(float amount) {
        if (balance < amount) return Failure.of(new IllegalArgumentException("Insufficient balance"));
        if (amount <= 0) return Failure.of(new IllegalArgumentException("Invalid amount"));
        return Success.of(new AccountState(id, userId, balance - amount));
    }
}
