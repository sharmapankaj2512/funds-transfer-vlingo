package io.pankaj.vlingo.funds.infra.persistence;

import io.pankaj.vlingo.funds.model.FundsTransferEntity;
import io.vlingo.actors.Stage;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class CommandModelJournalProvider {
    private static CommandModelJournalProvider instance;

    public static CommandModelJournalProvider instance() {
        return instance;
    }

    @SuppressWarnings("rawtypes")
    public static CommandModelJournalProvider using(Stage stage, SourcedTypeRegistry registry, Dispatcher storeDispatcher) {
        if (instance != null) return instance;

        Journal journal = Journal.using(stage, InMemoryJournalActor.class, storeDispatcher);
        return new CommandModelJournalProvider(journal, registry);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private CommandModelJournalProvider(Journal journal, SourcedTypeRegistry registry) {
        registry.register(new SourcedTypeRegistry.Info(journal,
                FundsTransferEntity.class,
                FundsTransferEntity.class.getSimpleName()));
    }
}
