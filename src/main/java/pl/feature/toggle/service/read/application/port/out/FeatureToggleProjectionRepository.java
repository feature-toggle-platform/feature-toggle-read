package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

public interface FeatureToggleProjectionRepository {

    void insert(FeatureToggleView featureToggleView);

    void updateStatus(FeatureToggleView featureToggleView);

    void updateBasicFields(FeatureToggleView featureToggleView);

    void updateValue(FeatureToggleView featureToggleView);

    void upsert(FeatureToggleView featureToggleView);

    boolean markInconsistentIfNotMarked(FeatureToggleId featureToggleId);
}
