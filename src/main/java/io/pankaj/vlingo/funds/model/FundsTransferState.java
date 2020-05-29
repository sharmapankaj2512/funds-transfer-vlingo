package io.pankaj.vlingo.funds.model;

public class FundsTransferState {
    public final String id;
    public final Status status;

    public FundsTransferState(String id, Status status) {
        this.id = id;
        this.status = status;
    }

    public static FundsTransferState has(String id) {
        return new FundsTransferState(id, Status.Initiated);
    }

    public static FundsTransferState debitFailed(String id) {
        return new FundsTransferState(id, Status.DebitFailed);
    }

    public static FundsTransferState debitSucceeded(String id) {
        return new FundsTransferState(id, Status.DebitSucceeded);
    }

    public static FundsTransferState completed(String id) {
        return new FundsTransferState(id, Status.Completed);
    }

    public static FundsTransferState creditFailed(String id) {
        return new FundsTransferState(id, Status.CreditFailed);
    }

    public enum Status {
        Initiated, DebitFailed, DebitSucceeded, Completed, CreditFailed;
    }
}
