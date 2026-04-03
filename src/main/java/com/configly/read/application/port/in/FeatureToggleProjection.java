package com.configly.read.application.port.in;


import com.configly.contracts.event.featuretoggle.FeatureToggleCreated;
import com.configly.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import com.configly.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.configly.contracts.event.featuretoggle.FeatureToggleValueChanged;

public interface FeatureToggleProjection {

    void handle(FeatureToggleCreated event);

    void handle(FeatureToggleUpdated event);

    void handle(FeatureToggleValueChanged event);

    void handle(FeatureToggleStatusChanged event);

}
