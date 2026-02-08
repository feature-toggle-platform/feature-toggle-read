package pl.feature.toggle.service.read.infrastructure.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class FakeFeatureToggleSnapshotRepository implements FeatureToggleProjectionRepository {

    private final FakeFeatureToggleReadRepository readRepository;

    private final Map<FeatureToggleId, FeatureToggleView> featureToggles = new HashMap<>();

    @Override
    public void save(FeatureToggleView featureToggleView) {
        featureToggles.put(featureToggleView.id(), featureToggleView);
        readRepository.insert(featureToggleView);
    }

    @Override
    public void delete(FeatureToggleId featureToggleId) {
        featureToggles.remove(featureToggleId);
        readRepository.deleteById(featureToggleId);
    }

    public void clear() {
        featureToggles.clear();
    }
}
