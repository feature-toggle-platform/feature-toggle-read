package com.configly.read.domain.exception;


import com.configly.model.featuretoggle.FeatureToggleId;

public class FeatureToggleNotFoundException extends RuntimeException {
    public FeatureToggleNotFoundException(FeatureToggleId featureToggleId) {
        super("FeatureToggle with id " + featureToggleId.idAsString() + " not found");
    }
}
