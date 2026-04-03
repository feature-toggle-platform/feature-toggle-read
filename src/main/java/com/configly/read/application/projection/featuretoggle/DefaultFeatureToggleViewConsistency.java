package com.configly.read.application.projection.featuretoggle;

import lombok.AllArgsConstructor;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.read.application.port.in.FeatureToggleViewConsistency;
import com.configly.read.application.port.out.FeatureToggleProjectionRepository;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.application.port.out.FeatureToggleClient;
import com.configly.read.domain.FeatureToggleView;

@AllArgsConstructor
class DefaultFeatureToggleViewConsistency implements FeatureToggleViewConsistency {

    private final FeatureToggleClient featureToggleClient;
    private final FeatureToggleProjectionRepository projectionRepository;
    private final FeatureToggleQueryRepository queryRepository;

    @Override
    public FeatureToggleView getTrusted(FeatureToggleId featureToggleId) {
        return queryRepository.findConsistent(featureToggleId)
                .orElseGet(() -> fetchAndSaveFeatureToggle(featureToggleId));
    }

    @Override
    public void rebuild(FeatureToggleId featureToggleId) {
        fetchAndSaveFeatureToggle(featureToggleId);
    }

    private FeatureToggleView fetchAndSaveFeatureToggle(FeatureToggleId featureToggleId) {
        var featureToggleView = featureToggleClient.fetchFeatureToggle(featureToggleId);
        projectionRepository.upsert(featureToggleView);
        return featureToggleView;
    }
}
