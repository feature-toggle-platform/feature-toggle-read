package pl.feature.toggle.service.read.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.feature.toggle.service.read.application.port.in.ProjectViewConsistency;
import pl.feature.toggle.service.read.application.projection.project.event.ProjectViewRebuildRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class ProjectViewRebuildListener {

    private final ProjectViewConsistency consistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(ProjectViewRebuildRequested event) {
        consistency.rebuild(event.projectId());
    }

}
