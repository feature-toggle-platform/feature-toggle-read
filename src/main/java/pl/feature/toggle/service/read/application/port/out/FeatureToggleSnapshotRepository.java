package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggle;

public interface FeatureToggleSnapshotRepository {

    void save(FeatureToggle featureToggle);

    void delete(FeatureToggleId featureToggleId);
}
