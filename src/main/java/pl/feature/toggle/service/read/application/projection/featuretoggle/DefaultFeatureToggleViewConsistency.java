package pl.feature.toggle.service.read.application.projection.featuretoggle;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleViewConsistency;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.WriteClient;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

@AllArgsConstructor
class DefaultFeatureToggleViewConsistency implements FeatureToggleViewConsistency {

    private final WriteClient writeClient;
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
        var featureToggleView = writeClient.fetchFeatureToggle(featureToggleId);
        projectionRepository.upsert(featureToggleView);
        return featureToggleView;
    }
}
