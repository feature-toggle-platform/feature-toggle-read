package pl.feature.toggle.service.read.application.handler;

import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.read.application.projection.featuretoggle.FeatureToggleProjectionHandler;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static FeatureToggleProjection featureToggleProjection(
            FeatureToggleProjectionRepository featureToggleSnapshotRepository
    ) {
        return new FeatureToggleProjectionHandler(featureToggleSnapshotRepository);
    }

    public static FeatureToggleReadUseCase featureToggleReadUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository
    ) {
        return new FeatureToggleReadHandler(featureToggleQueryRepository);
    }

}
