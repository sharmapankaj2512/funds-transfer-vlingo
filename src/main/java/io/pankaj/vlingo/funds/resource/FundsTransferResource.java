package io.pankaj.vlingo.funds.resource;

import io.pankaj.vlingo.funds.infra.FundsTransferData;
import io.pankaj.vlingo.funds.infra.persistence.Queries;
import io.pankaj.vlingo.funds.infra.persistence.QueryModelStoreProvider;
import io.pankaj.vlingo.funds.model.Account;
import io.pankaj.vlingo.funds.model.AccountEntity;
import io.pankaj.vlingo.funds.model.FundsTransfer;
import io.vlingo.actors.AddressFactory;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.headers;
import static io.vlingo.http.resource.ResourceBuilder.*;

public class FundsTransferResource {
    private final World world;
    private final AddressFactory addressFactory;
    private final Queries queries;

    public FundsTransferResource(World world) {
        this.world = world;
        this.addressFactory = world.addressFactory();
        this.queries = QueryModelStoreProvider.instance().queries;
    }

    public Resource<?> routes() {
        return resource("Funds transfer Resource",
                post("/transfers").body(FundsTransferData.class).handle(this::transfer),
                get("/transfers/{id}").param(String.class).handle(this::transferDetails));
    }

    private Completes<Response> transferDetails(String id) {
        return queries.transaction(id)
                .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(state.data))))
                .otherwise(noState -> Response.of(NotFound));
    }

    private Completes<Response> transfer(FundsTransferData data) {
        return resolve(data.from)
                .andThenTo(from -> resolve(data.to)
                        .andThenTo(to -> FundsTransfer.initiate(world.stage(), from, to, data.amount)))
                .andThenTo(state -> Completes.withSuccess(Response.of(
                        Created,
                        headers(ResponseHeader.of(Location, "/transfers/" + state)),
                        serialized(state))));
    }

    private Completes<Account> resolve(final String id) {
        return world.stage().actorOf(Account.class, addressFactory.from(id), AccountEntity.class);
    }
}
