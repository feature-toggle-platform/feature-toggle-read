package pl.feature.toggle.service.application.port.in;

import com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleUpdated;

public interface FeatureToggleProjectionUseCase {

    void handle(FeatureToggleCreated event);

    void handle(FeatureToggleDeleted event);

    void handle(FeatureToggleUpdated event);

}
