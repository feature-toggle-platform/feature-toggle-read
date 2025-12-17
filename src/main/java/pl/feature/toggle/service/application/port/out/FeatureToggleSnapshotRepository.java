package pl.feature.toggle.service.application.port.out;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.domain.FeatureToggle;

public interface FeatureToggleSnapshotRepository {

    void save(FeatureToggle featureToggle);

    void delete(FeatureToggleId featureToggleId);
}
