package pl.feature.toggle.service.read.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.feature.toggle.service.read.application.port.in.EnvironmentViewConsistency;
import pl.feature.toggle.service.read.application.projection.environment.event.EnvironmentViewRebuildRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class EnvironmentViewRebuildListener {

    private final EnvironmentViewConsistency consistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(EnvironmentViewRebuildRequested event) {
        consistency.rebuild(event.projectId(), event.environmentId());
    }

}
