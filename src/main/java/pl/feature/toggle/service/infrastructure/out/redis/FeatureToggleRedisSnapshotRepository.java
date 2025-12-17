package pl.feature.toggle.service.infrastructure.out.redis;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.application.port.out.FeatureToggleSnapshotRepository;
import pl.feature.toggle.service.domain.FeatureToggle;
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
