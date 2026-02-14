package pl.feature.toggle.service.read.application.projection.project;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.application.projection.project.event.ProjectViewRebuildRequested;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
class ProjectProjectionHandler implements ProjectProjection {

    private final ProjectProjectionRepository projectionRepository;
    private final ProjectQueryRepository projectQueryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(ProjectCreated event) {
        applyCreate(event);
    }

    @Override
    @Transactional
    public void handle(ProjectUpdated event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());

        applyUpdate(
                incoming,
                projectId,
                projectionRepository::updateBasicFields,
                current -> current.apply(event)
        );
    }

    @Override
    @Transactional
    public void handle(ProjectStatusChanged event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());

        applyUpdate(
                incoming,
                projectId,
                projectionRepository::updateStatus,
                current -> current.apply(event)
        );
    }

    private void applyCreate(ProjectCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var view = ProjectView.create(event);
        var rebuildEvent = new ProjectViewRebuildRequested(projectId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectView>forIncoming(incoming)
                        .findCurrentUsing(() -> projectQueryRepository.find(projectId))
                        .onMissing(() -> projectionRepository.insert(view))
                        .extractCurrentRevisionUsing(ProjectView::revision)
                        .applyUpdateWhenApplicable(current -> projectionRepository.upsert(view))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(projectId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }

    private void applyUpdate(
            Revision incoming,
            ProjectId projectId,
            Consumer<ProjectView> persist,
            UnaryOperator<ProjectView> mutate
    ) {
        var rebuildEvent = new ProjectViewRebuildRequested(projectId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectView>forIncoming(incoming)
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
