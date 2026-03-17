package pl.feature.toggle.service.read.application.projection.featuretoggle;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.event.processing.internal.RevisionApplierResult;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.projection.featuretoggle.event.FeatureToggleViewRebuildRequested;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
@Slf4j
class FeatureToggleProjectionHandler implements FeatureToggleProjection {

    private final FeatureToggleProjectionRepository projectionRepository;
    private final FeatureToggleQueryRepository queryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void handle(FeatureToggleCreated event) {
        var result = applyCreate(event);
        if (result.wasApplied()) {
            log.info("Feature-Toggle projection created: environmentId={}, featureToggleId={}, revision={}",
                    event.environmentId(), event.id(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(FeatureToggleUpdated event) {
        var incoming = Revision.from(event.revision());
        var featureToggleId = FeatureToggleId.create(event.id());
        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                incoming,
                featureToggleId,
                projectionRepository::updateBasicFields,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Feature-Toggle projection updated: environmentId={}, featureToggleId={}, revision={}",
                    event.environmentId(), event.id(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(FeatureToggleValueChanged event) {
        var incoming = Revision.from(event.revision());
        var featureToggleId = FeatureToggleId.create(event.id());
        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                incoming,
                featureToggleId,
                projectionRepository::updateValue,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Feature-Toggle projection value changed: environmentId={}, featureToggleId={}, newValue={} revision={}",
                    event.environmentId(), event.id(), event.value(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(FeatureToggleStatusChanged event) {
        var incoming = Revision.from(event.revision());
        var featureToggleId = FeatureToggleId.create(event.id());
        var result = applyUpdate(
                event.correlationId(),
                event.eventId(),
                incoming,
                featureToggleId,
                projectionRepository::updateStatus,
                current -> current.apply(event)
        );
        if (result.wasApplied()) {
            log.info("Feature-Toggle projection status changed: environmentId={}, featureToggleId={}, newStatus={}, revision={}",
                    event.environmentId(), event.id(), event.status(), event.revision());
        }
    }

    private RevisionApplierResult applyCreate(FeatureToggleCreated event) {
        var featureToggleId = FeatureToggleId.create(event.id());
        var incoming = Revision.from(event.revision());
        var view = FeatureToggleView.create(event);
        var correlationId = CorrelationId.of(event.correlationId());
        var rebuildEvent = new FeatureToggleViewRebuildRequested(featureToggleId, correlationId);

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<FeatureToggleView>forIncoming(incoming)
                        .eventId(event.eventId())
                        .findCurrentUsing(() -> queryRepository.find(featureToggleId))
                        .onMissing(() -> projectionRepository.insert(view))
                        .extractCurrentRevisionUsing(FeatureToggleView::revision)
                        .applyUpdateWhenApplicable(current -> projectionRepository.upsert(view))
                        .markInconsistentWhenGapDetectedIfNotMarked(() -> projectionRepository.markInconsistentIfNotMarked(featureToggleId))
                        .publishRebuildWhenGapDetected(() -> eventPublisher.publishEvent(rebuildEvent))
                        .build()
        );
    }

    private RevisionApplierResult applyUpdate(
            String correlationId,
            EventId eventId,
            Revision incoming,
            FeatureToggleId featureToggleId,
            Consumer<FeatureToggleView> persist,
            UnaryOperator<FeatureToggleView> mutate
    ) {
        var rebuildEvent = new FeatureToggleViewRebuildRequested(featureToggleId, CorrelationId.of(correlationId));

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<FeatureToggleView>forIncoming(incoming)
                        .eventId(eventId)
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
