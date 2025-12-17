package pl.feature.toggle.service.application.handler;

import pl.feature.toggle.service.application.port.in.FeatureToggleProjectionUseCase;
import pl.feature.toggle.service.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleSnapshotRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static FeatureToggleProjectionUseCase featureToggleProjectionUseCase(
            FeatureToggleSnapshotRepository featureToggleSnapshotRepository
    ) {
        return new FeatureToggleProjectionHandler(featureToggleSnapshotRepository);
    }

    public static FeatureToggleReadUseCase featureToggleReadUseCase(
            FeatureToggleReadRepository featureToggleReadRepository
    ) {
        return new FeatureToggleReadHandler(featureToggleReadRepository);
    }

}
