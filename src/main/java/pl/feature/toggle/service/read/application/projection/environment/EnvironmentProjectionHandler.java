package pl.feature.toggle.service.read.application.projection.environment;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentTypeChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentUpdated;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.application.projection.environment.event.EnvironmentViewRebuildRequested;
import pl.feature.toggle.service.read.domain.EnvironmentView;

@AllArgsConstructor
class EnvironmentProjectionHandler implements EnvironmentProjection {

    private final EnvironmentProjectionRepository projectionRepository;
    private final EnvironmentQueryRepository queryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(EnvironmentCreated event) {
        applyCreate(event);
    }

    @Override
    @Transactional
    public void handle(EnvironmentUpdated event) {
        applyUpdate(
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                Revision.from(event.revision()),
                projectionRepository::updateName,
                current -> current.apply(event)
        );
    }

    @Override
    @Transactional
    public void handle(EnvironmentStatusChanged event) {
        applyUpdate(
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                Revision.from(event.revision()),
                projectionRepository::updateStatus,
                current -> current.apply(event)
        );
    }

    @Override
    @Transactional
    public void handle(EnvironmentTypeChanged event) {
        applyUpdate(
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                Revision.from(event.revision()),
                projectionRepository::updateType,
                current -> current.apply(event)
        );
    }

    private void applyCreate(EnvironmentCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var environmentId = EnvironmentId.create(event.environmentId());
        var incoming = Revision.from(event.revision());

        var view = EnvironmentView.create(event);
        var rebuildEvent = new EnvironmentViewRebuildRequested(projectId, environmentId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<EnvironmentView>forIncoming(incoming)
                        .findCurrentUsing(() -> queryRepository.find(projectId, environmentId))
                        .onMissing(() -> projectionRepository.insert(view))
                        .extractCurrentRevisionUsing(EnvironmentView::revision)
                        .applyUpdateWhenApplicable(current -> projectionRepository.upsert(view))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(environmentId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }

    private void applyUpdate(
            EnvironmentId environmentId,
            ProjectId projectId,
            Revision incoming,
            java.util.function.Consumer<EnvironmentView> persist,
            java.util.function.UnaryOperator<EnvironmentView> mutate
    ) {
        var rebuildEvent = new EnvironmentViewRebuildRequested(projectId, environmentId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<EnvironmentView>forIncoming(incoming)
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
