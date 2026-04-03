package com.configly.read.application.projection.featuretoggle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.read.application.port.in.FeatureToggleProjection;
import com.configly.read.application.port.in.FeatureToggleViewConsistency;
import com.configly.read.application.port.out.FeatureToggleProjectionRepository;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.application.port.out.FeatureToggleClient;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleProjectionFacade {

    public static FeatureToggleProjection featureToggleProjection(FeatureToggleProjectionRepository featureToggleProjectionRepository,
                                                                  FeatureToggleQueryRepository featureToggleQueryRepository,
                                                                  RevisionProjectionApplier revisionProjectionApplier,
                                                                  ApplicationEventPublisher eventPublisher) {
        return new FeatureToggleProjectionHandler(featureToggleProjectionRepository, featureToggleQueryRepository, revisionProjectionApplier, eventPublisher);
    }

    public static FeatureToggleViewConsistency featureToggleViewConsistency(FeatureToggleClient featureToggleClient,
                                                                            FeatureToggleProjectionRepository featureToggleProjectionRepository,
                                                                            FeatureToggleQueryRepository queryRepository) {
        return new DefaultFeatureToggleViewConsistency(featureToggleClient, featureToggleProjectionRepository, queryRepository);
    }
}
