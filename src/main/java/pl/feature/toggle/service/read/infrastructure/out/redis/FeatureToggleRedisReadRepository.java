package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import pl.feature.toggle.service.read.domain.exception.FeatureToggleNotFoundException;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
class FeatureToggleRedisReadRepository implements FeatureToggleReadRepository {

    private final CacheClient cacheClient;

    @Override
    public FeatureToggle getById(FeatureToggleId id) {
        return cacheClient.read(id)
                .orElseThrow(() -> new FeatureToggleNotFoundException(id));
    }

    @Override
    public List<FeatureToggle> getAll() {
        return cacheClient.readAll();
    }
}
