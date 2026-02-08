package pl.feature.toggle.service.read.application.projection.featuretoggle;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;

class FeatureToggleProjectionHandler implements FeatureToggleProjection {
    @Override
    public void handle(FeatureToggleCreated event) {

    }

    @Override
    public void handle(FeatureToggleUpdated event) {

    }

    @Override
    public void handle(FeatureToggleValueChanged event) {

    }

    @Override
    public void handle(FeatureToggleStatusChanged event) {

    }
}
