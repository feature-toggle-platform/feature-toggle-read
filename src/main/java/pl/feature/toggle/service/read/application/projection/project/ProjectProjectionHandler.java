package pl.feature.toggle.service.read.application.projection.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.event.processing.internal.RevisionApplierResult;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.application.projection.project.event.ProjectViewRebuildRequested;
import pl.feature.toggle.service.read.domain.ProjectView;

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
