package io.pankaj.vlingo.funds.infra;

public class FundsTransferData {
    public final String id;
    public final String from;
    public final String to;
    public final float amount;

    public FundsTransferData(String id, String from, String to, float amount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
