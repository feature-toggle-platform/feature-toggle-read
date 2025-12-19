package pl.feature.toggle.service.read.domain.exception;


import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

public class FeatureToggleNotFoundException extends RuntimeException {
    public FeatureToggleNotFoundException(FeatureToggleId featureToggleId) {
        super("FeatureToggle with id " + featureToggleId.idAsString() + " not found");
    }
}
