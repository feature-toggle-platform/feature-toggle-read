package pl.feature.toggle.service.infrastructure.out.redis;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.domain.FeatureToggle;
import pl.feature.toggle.service.domain.exception.FeatureToggleNotFoundException;
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
