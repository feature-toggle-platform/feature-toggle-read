package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

public interface WriteClient {

    FeatureToggleView fetchFeatureToggle(FeatureToggleId featureToggleId);
}
