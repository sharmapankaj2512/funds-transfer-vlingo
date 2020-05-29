package io.pankaj.vlingo.funds.resource;

import io.pankaj.vlingo.funds.infra.AccountData;
import io.pankaj.vlingo.funds.infra.persistence.Queries;
import io.pankaj.vlingo.funds.infra.persistence.QueryModelStoreProvider;
import io.vlingo.actors.AddressFactory;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.Failure;
import io.vlingo.common.serialization.JsonSerialization;
import io.pankaj.vlingo.funds.model.Account;
import io.pankaj.vlingo.funds.model.AccountEntity;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.headers;
import static io.vlingo.http.resource.ResourceBuilder.*;

public class AccountResource {
    private final World world;
    private final Queries queries;
    private final AddressFactory addressFactory;

    public AccountResource(World world) {
        this.world = world;
        this.addressFactory = world.addressFactory();
        this.queries = QueryModelStoreProvider.instance().queries;
    }

    public Resource<?> routes() {
        return resource("Account Resource",
                post("/accounts").body(AccountData.class).handle(this::openAccount),
                get("/accounts/{accountId}").param(String.class).handle(this::getAccount),
                patch("/accounts/{accountId}/balance/{amount}").param(String.class).param(Float.class).handle(this::deposit),
                delete("/accounts/{accountId}/balance/{amount}").param(String.class).param(Float.class).handle(this::withdraw));
    }

    private Completes<Response> openAccount(AccountData data) {
        return Account.openFor(world.stage(), data.userId).andThenTo(state ->
                Completes.withSuccess(Response.of(
                        Created,
                        headers(ResponseHeader.of(Location, "/accounts/" + state.id)),
                        serialized(AccountData.from(state)))));
    }

    private Completes<Response> deposit(String id, Float amount) {
        return resolve(id).andThenTo(account -> account.credit(amount))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenTo(maybeAccount -> Completes.withSuccess(maybeAccount.resolve(
                        ex -> Response.of(BadRequest, serialized(ex.getMessage())),
                        state -> Response.of(Ok, serialized(state)))))
                .otherwise(noGreeting -> Response.of(NotFound, "/accounts/" + id));
    }

    private Completes<Response> withdraw(String id, Float amount) {
        return resolve(id).andThenTo(account -> account.debit(amount))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenTo(maybeAccount -> Completes.withSuccess(maybeAccount.resolve(
                        ex -> Response.of(BadRequest, serialized(ex.getMessage())),
                        state -> Response.of(Ok, serialized(state)))))
                .otherwise(noGreeting -> Response.of(NotFound, "/accounts/" + id));
    }

    private Completes<Response> getAccount(String id) {
        return queries.accountOf(id)
                .andThenTo(data -> Completes.withSuccess(Response.of(Ok, JsonSerialization.serialized(data))))
                .otherwise(noData -> Response.of(NotFound, "/accounts/" + id));
    }

    private Completes<Account> resolve(final String id) {
        return world.stage().actorOf(Account.class, addressFactory.from(id), AccountEntity.class);
    }
}
