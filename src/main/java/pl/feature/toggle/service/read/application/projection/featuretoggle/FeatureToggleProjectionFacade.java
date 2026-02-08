package pl.feature.toggle.service.read.application.projection.featuretoggle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleViewConsistency;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.WriteClient;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleProjectionFacade {

    public static FeatureToggleProjection featureToggleProjection() {
        return new FeatureToggleProjectionHandler();
    }

    public static FeatureToggleViewConsistency featureToggleViewConsistency(WriteClient writeClient,
                                                                            FeatureToggleProjectionRepository featureToggleProjectionRepository,
                                                                            FeatureToggleQueryRepository queryRepository) {
        return new DefaultFeatureToggleViewConsistency(writeClient, featureToggleProjectionRepository, queryRepository);
    }
}
