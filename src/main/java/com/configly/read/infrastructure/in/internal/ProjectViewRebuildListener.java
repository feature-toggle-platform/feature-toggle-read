package com.configly.read.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import com.configly.event.processing.api.CorrelationScope;
import com.configly.read.application.port.in.ProjectViewConsistency;
import com.configly.read.application.projection.project.event.ProjectViewRebuildRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class ProjectViewRebuildListener {

    private final ProjectViewConsistency consistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(ProjectViewRebuildRequested event) {
        CorrelationScope.run(
                event.correlationId(),
                () -> consistency.rebuild(event.projectId())
        );
    }

}
