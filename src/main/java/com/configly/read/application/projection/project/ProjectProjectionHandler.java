package com.configly.read.application.projection.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.event.project.ProjectStatusChanged;
import com.configly.contracts.event.project.ProjectUpdated;
import com.configly.contracts.shared.EventId;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.event.processing.api.RevisionProjectionPlan;
import com.configly.event.processing.internal.RevisionApplierResult;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.web.correlation.CorrelationId;
import com.configly.read.application.port.in.ProjectProjection;
import com.configly.read.application.port.out.ProjectProjectionRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;
import com.configly.read.application.projection.project.event.ProjectViewRebuildRequested;
import com.configly.read.domain.ProjectView;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
@Slf4j
class ProjectProjectionHandler implements ProjectProjection {

    private final ProjectProjectionRepository projectionRepository;
    private final ProjectQueryRepository projectQueryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(ProjectCreated event) {
        var result = applyCreate(event);
        if (result.wasApplied()) {
            log.info("Project projection created: projectId={}, revision={}", event.projectId(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(ProjectUpdated event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());

        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                incoming,
                projectId,
                projectionRepository::updateBasicFields,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Project projection updated: projectId={}, revision={}", event.projectId(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(ProjectStatusChanged event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());

        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                incoming,
                projectId,
                projectionRepository::updateStatus,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Project projection status changed: projectId={}, newStatus={}, revision={}", event.projectId(), event.status(), event.revision());
        }
    }

    private RevisionApplierResult applyCreate(ProjectCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var view = ProjectView.create(event);
        var correlationId = CorrelationId.of(event.correlationId());
        var rebuildEvent = new ProjectViewRebuildRequested(projectId, correlationId);

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectView>forIncoming(incoming)
                        .eventId(event.eventId())
                        .findCurrentUsing(() -> projectQueryRepository.find(projectId))
                        .onMissing(() -> projectionRepository.insert(view))
                        .extractCurrentRevisionUsing(ProjectView::revision)
                        .applyUpdateWhenApplicable(current -> projectionRepository.upsert(view))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(projectId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }

    private RevisionApplierResult applyUpdate(
            String correlationId,
            EventId eventId,
            Revision incoming,
            ProjectId projectId,
            Consumer<ProjectView> persist,
            UnaryOperator<ProjectView> mutate
    ) {
        var rebuildEvent = new ProjectViewRebuildRequested(projectId, CorrelationId.of(correlationId));

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectView>forIncoming(incoming)
                        .eventId(eventId)
                        .findCurrentUsing(() -> projectQueryRepository.find(projectId))
                        .onMissing(() -> eventPublisher.publishEvent(rebuildEvent))
                        .extractCurrentRevisionUsing(ProjectView::revision)
                        .applyUpdateWhenApplicable(current -> persist.accept(mutate.apply(current)))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(projectId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }
}
