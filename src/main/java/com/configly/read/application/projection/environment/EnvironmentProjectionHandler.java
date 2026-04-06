package com.configly.read.application.projection.environment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;
import com.configly.contracts.event.environment.EnvironmentTypeChanged;
import com.configly.contracts.event.environment.EnvironmentUpdated;
import com.configly.contracts.shared.EventId;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.event.processing.api.RevisionProjectionPlan;
import com.configly.event.processing.internal.RevisionApplierResult;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.read.application.port.in.EnvironmentProjection;
import com.configly.read.application.port.out.EnvironmentProjectionRepository;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.application.projection.environment.event.EnvironmentViewRebuildRequested;
import com.configly.read.domain.EnvironmentView;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
@Slf4j
class EnvironmentProjectionHandler implements EnvironmentProjection {

    private final EnvironmentProjectionRepository projectionRepository;
    private final EnvironmentQueryRepository queryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(EnvironmentCreated event) {
        var result = applyCreate(event);
        if (result.wasApplied()) {
            log.info("Environment projection created: projectId={}, environmentId={}, revision={}",
                    event.projectId(), event.environmentId(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(EnvironmentUpdated event) {
        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                Revision.from(event.revision()),
                projectionRepository::updateName,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Environment projection updated: projectId={}, environmentId={}, revision={}",
                    event.projectId(), event.environmentId(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(EnvironmentStatusChanged event) {
        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                Revision.from(event.revision()),
                projectionRepository::updateStatus,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Environment projection status changed: projectId={}, environmentId={}, newStatus={} revision={}",
                    event.projectId(), event.environmentId(), event.status(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(EnvironmentTypeChanged event) {
        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                Revision.from(event.revision()),
                projectionRepository::updateType,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Environment projection type changed: projectId={}, environmentId={}, newType={}, revision={}",
                    event.projectId(), event.environmentId(), event.type(), event.revision());
        }
    }

    private RevisionApplierResult applyCreate(EnvironmentCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var environmentId = EnvironmentId.create(event.environmentId());
        var incoming = Revision.from(event.revision());
        var correlationId = event.correlationId();

        var view = EnvironmentView.create(event);
        var rebuildEvent = new EnvironmentViewRebuildRequested(projectId, environmentId, CorrelationId.of(correlationId));

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<EnvironmentView>forIncoming(incoming)
                        .eventId(event.eventId())
                        .findCurrentUsing(() -> queryRepository.find(projectId, environmentId))
                        .onMissing(() -> projectionRepository.insert(view))
                        .extractCurrentRevisionUsing(EnvironmentView::revision)
                        .applyUpdateWhenApplicable(current -> projectionRepository.upsert(view))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(environmentId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }

    private RevisionApplierResult applyUpdate(
            String correlationId,
            EventId eventId,
            EnvironmentId environmentId,
            ProjectId projectId,
            Revision incoming,
            Consumer<EnvironmentView> persist,
            UnaryOperator<EnvironmentView> mutate
    ) {
        var rebuildEvent = new EnvironmentViewRebuildRequested(projectId, environmentId, CorrelationId.of(correlationId));

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<EnvironmentView>forIncoming(incoming)
                        .eventId(eventId)
                        .findCurrentUsing(() -> queryRepository.find(projectId, environmentId))
                        .onMissing(() -> eventPublisher.publishEvent(rebuildEvent))
                        .extractCurrentRevisionUsing(EnvironmentView::revision)
                        .applyUpdateWhenApplicable(current ->
                                persist.accept(mutate.apply(current))
                        )
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> projectionRepository.markInconsistentIfNotMarked(environmentId)
                        )
                        .publishRebuildWhenGapDetected(
                                () -> eventPublisher.publishEvent(rebuildEvent)
                        )
                        .build()
        );
    }
}
