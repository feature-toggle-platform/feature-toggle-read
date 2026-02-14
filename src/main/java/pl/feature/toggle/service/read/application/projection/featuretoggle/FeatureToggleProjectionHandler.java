package pl.feature.toggle.service.read.application.projection.featuretoggle;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.projection.featuretoggle.event.FeatureToggleViewRebuildRequested;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
class FeatureToggleProjectionHandler implements FeatureToggleProjection {

    private final FeatureToggleProjectionRepository projectionRepository;
    private final FeatureToggleQueryRepository queryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handle(FeatureToggleCreated event) {
        applyCreate(event);
    }

    @Override
    public void handle(FeatureToggleUpdated event) {
        var incoming = Revision.from(event.revision());
        var featureToggleId = FeatureToggleId.create(event.id());
        applyUpdate(
                incoming,
                featureToggleId,
                projectionRepository::updateBasicFields,
                current -> current.apply(event)
        );
    }

    @Override
    public void handle(FeatureToggleValueChanged event) {
        var incoming = Revision.from(event.revision());
        var featureToggleId = FeatureToggleId.create(event.id());
        applyUpdate(
                incoming,
                featureToggleId,
                projectionRepository::updateValue,
                current -> current.apply(event)
        );
    }

    @Override
    public void handle(FeatureToggleStatusChanged event) {
        var incoming = Revision.from(event.revision());
        var featureToggleId = FeatureToggleId.create(event.id());
        applyUpdate(
                incoming,
                featureToggleId,
                projectionRepository::updateStatus,
                current -> current.apply(event)
        );
    }

    private void applyCreate(FeatureToggleCreated event) {
        var featureToggleId = FeatureToggleId.create(event.id());
        var incoming = Revision.from(event.revision());
        var view = FeatureToggleView.create(event);
        var rebuildEvent = new FeatureToggleViewRebuildRequested(featureToggleId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<FeatureToggleView>forIncoming(incoming)
                        .findCurrentUsing(() -> queryRepository.find(featureToggleId))
                        .onMissing(() -> projectionRepository.insert(view))
                        .extractCurrentRevisionUsing(FeatureToggleView::revision)
                        .applyUpdateWhenApplicable(current -> projectionRepository.upsert(view))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(featureToggleId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }

    private void applyUpdate(
            Revision incoming,
            FeatureToggleId featureToggleId,
            Consumer<FeatureToggleView> persist,
            UnaryOperator<FeatureToggleView> mutate
    ) {
        var rebuildEvent = new FeatureToggleViewRebuildRequested(featureToggleId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<FeatureToggleView>forIncoming(incoming)
                        .findCurrentUsing(() -> queryRepository.find(featureToggleId))
                        .onMissing(() -> eventPublisher.publishEvent(rebuildEvent))
                        .extractCurrentRevisionUsing(FeatureToggleView::revision)
                        .applyUpdateWhenApplicable(current -> persist.accept(mutate.apply(current)))
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> projectionRepository.markInconsistentIfNotMarked(featureToggleId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }
}
