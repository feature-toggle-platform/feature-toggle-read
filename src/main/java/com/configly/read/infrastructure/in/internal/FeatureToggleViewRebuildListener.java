package com.configly.read.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import com.configly.event.processing.api.CorrelationScope;
import com.configly.read.application.port.in.FeatureToggleViewConsistency;
import com.configly.read.application.projection.featuretoggle.event.FeatureToggleViewRebuildRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class FeatureToggleViewRebuildListener {

    private final FeatureToggleViewConsistency consistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(FeatureToggleViewRebuildRequested event) {
        CorrelationScope.run(
                event.correlationId(),
                () -> consistency.rebuild(event.featureToggleId())
        );
    }

}
