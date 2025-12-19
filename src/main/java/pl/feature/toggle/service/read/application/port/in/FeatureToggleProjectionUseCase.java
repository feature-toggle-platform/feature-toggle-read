package pl.feature.toggle.service.read.application.port.in;


import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;

public interface FeatureToggleProjectionUseCase {

    void handle(FeatureToggleCreated event);

    void handle(FeatureToggleDeleted event);

    void handle(FeatureToggleUpdated event);

}
