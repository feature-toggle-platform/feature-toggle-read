package com.configly.read.application.port.in;

import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.read.domain.FeatureToggleView;

public interface FeatureToggleViewConsistency {

    FeatureToggleView getTrusted(FeatureToggleId featureToggleId);

    void rebuild(FeatureToggleId featureToggleId);
}
