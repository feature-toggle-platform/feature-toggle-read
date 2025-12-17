package pl.feature.toggle.service.domain.exception;

import com.ftaas.domain.featuretoggle.FeatureToggleId;

public class FeatureToggleNotFoundException extends RuntimeException {
    public FeatureToggleNotFoundException(FeatureToggleId featureToggleId) {
        super("FeatureToggle with id " + featureToggleId.idAsString() + " not found");
    }
}
