package com.configly.read.application.port.out;

import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.read.domain.FeatureToggleView;

public interface FeatureToggleClient {

    FeatureToggleView fetchFeatureToggle(FeatureToggleId featureToggleId);
}
