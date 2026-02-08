package pl.feature.toggle.service.read.application.port.in;


import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;

public interface FeatureToggleProjection {

    void handle(FeatureToggleCreated event);

    void handle(FeatureToggleUpdated event);

    void handle(FeatureToggleValueChanged event);

    void handle(FeatureToggleStatusChanged event);

}
