package com.configly.read.application.port.out;

import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.read.domain.FeatureToggleView;

public interface FeatureToggleProjectionRepository {

    void insert(FeatureToggleView featureToggleView);

    void updateStatus(FeatureToggleView featureToggleView);

    void updateBasicFields(FeatureToggleView featureToggleView);

    void updateValue(FeatureToggleView featureToggleView);

    void upsert(FeatureToggleView featureToggleView);

    boolean markInconsistentIfNotMarked(FeatureToggleId featureToggleId);
}
