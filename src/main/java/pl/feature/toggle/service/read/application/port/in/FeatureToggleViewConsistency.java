package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

public interface FeatureToggleViewConsistency {

    FeatureToggleView getTrusted(FeatureToggleId featureToggleId);

    void rebuild(FeatureToggleId featureToggleId);
}
