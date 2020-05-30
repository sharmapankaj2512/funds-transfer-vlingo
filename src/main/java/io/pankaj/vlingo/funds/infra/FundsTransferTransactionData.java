package io.pankaj.vlingo.funds.infra;

import io.pankaj.vlingo.funds.model.FundsTransferState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FundsTransferTransactionData {
    public final String id;
    public final List<String> data;

    public FundsTransferTransactionData() {
        this("", new ArrayList<>());
    }

    public FundsTransferTransactionData(String id, List<String> data) {
        this.id = id;
        this.data = data;
    }

    public static FundsTransferTransactionData empty() {
        return new FundsTransferTransactionData();
    }

    public static FundsTransferTransactionData from(FundsTransferState state) {
        return new FundsTransferTransactionData(state.id, new ArrayList<>());
    }

    public FundsTransferTransactionData mergeWith(String id, String typeName) {
        List<String> updated = Stream.concat(data.stream(), Stream.of(typeName)).collect(toList());
        return new FundsTransferTransactionData(id, updated);
    }
}
