package com.configly.read.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import com.configly.event.processing.api.CorrelationScope;
import com.configly.read.application.port.in.EnvironmentViewConsistency;
import com.configly.read.application.projection.environment.event.EnvironmentViewRebuildRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class EnvironmentViewRebuildListener {

    private final EnvironmentViewConsistency consistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(EnvironmentViewRebuildRequested event) {
        CorrelationScope.run(
                event.correlationId(),
                () -> consistency.rebuild(event.projectId(), event.environmentId())
        );
    }

}
