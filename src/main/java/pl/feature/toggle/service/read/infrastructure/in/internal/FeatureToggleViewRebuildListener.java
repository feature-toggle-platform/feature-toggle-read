package pl.feature.toggle.service.read.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleViewConsistency;
import pl.feature.toggle.service.read.application.projection.featuretoggle.event.FeatureToggleViewRebuildRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class FeatureToggleViewRebuildListener {

    private final FeatureToggleViewConsistency consistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(FeatureToggleViewRebuildRequested event) {
        consistency.rebuild(event.featureToggleId());
    }

}
