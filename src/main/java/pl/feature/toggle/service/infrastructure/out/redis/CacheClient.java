package pl.feature.toggle.service.infrastructure.out.redis;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.domain.FeatureToggle;

import java.util.List;
import java.util.Optional;

interface CacheClient {

    void save(FeatureToggle featureToggle);

    Optional<FeatureToggle> read(FeatureToggleId id);

    void deleteById(FeatureToggleId featureToggleId);

    List<FeatureToggle> readAll();
}
