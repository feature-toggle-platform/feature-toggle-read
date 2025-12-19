package pl.feature.toggle.service.read.infrastructure.out.db;

import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.read.application.port.out.ProcessedEventRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;

import static pl.feature.ftaas.jooq.tables.ProcessedEvents.PROCESSED_EVENTS;


@AllArgsConstructor
class ProcessedEventJooqRepository implements ProcessedEventRepository {

    private final DSLContext dslContext;

    @Override
    public boolean alreadyProcessed(EventId eventId) {
        return dslContext.fetchExists(PROCESSED_EVENTS, PROCESSED_EVENTS.ID.eq(eventId.id()));
    }

    @Override
    public void markProcessed(EventId eventId) {
        dslContext.insertInto(PROCESSED_EVENTS)
                .set(PROCESSED_EVENTS.ID, eventId.id())
                .execute();
    }
}
