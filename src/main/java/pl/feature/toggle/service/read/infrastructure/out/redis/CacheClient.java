package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggle;

import java.util.List;
import java.util.Optional;

interface CacheClient {

    void save(FeatureToggle featureToggle);

    Optional<FeatureToggle> read(FeatureToggleId id);

    void deleteById(FeatureToggleId featureToggleId);

    List<FeatureToggle> readAll();
}
