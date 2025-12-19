package pl.feature.toggle.service.read.infrastructure.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleSnapshotRepository;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class FakeFeatureToggleSnapshotRepository implements FeatureToggleSnapshotRepository {

    private final FakeFeatureToggleReadRepository readRepository;

    private final Map<FeatureToggleId, FeatureToggle> featureToggles = new HashMap<>();

    @Override
    public void save(FeatureToggle featureToggle) {
        featureToggles.put(featureToggle.id(), featureToggle);
        readRepository.insert(featureToggle);
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
