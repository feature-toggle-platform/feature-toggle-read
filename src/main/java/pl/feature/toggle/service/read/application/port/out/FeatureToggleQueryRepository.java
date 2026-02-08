package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.Optional;

public interface FeatureToggleQueryRepository {

    Optional<FeatureToggleView> find(FeatureToggleId featureToggleId);

    Optional<FeatureToggleView> findConsistent(FeatureToggleId featureToggleId);
}
