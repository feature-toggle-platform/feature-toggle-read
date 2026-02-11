package pl.feature.toggle.service.read.infrastructure.out.rest;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.WriteClient;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

class RestWriteClient implements WriteClient {
    @Override
    public FeatureToggleView fetchFeatureToggle(FeatureToggleId featureToggleId) {
        return null;
    }
}
