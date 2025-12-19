package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleSnapshotRepository;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class FeatureToggleRedisSnapshotRepository implements FeatureToggleSnapshotRepository {

    private final CacheClient cacheClient;

    @Override
    public void save(FeatureToggle featureToggle) {
        cacheClient.save(featureToggle);
    }

    @Override
    public void delete(FeatureToggleId featureToggleId) {
        cacheClient.deleteById(featureToggleId);
    }
}
