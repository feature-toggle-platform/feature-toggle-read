package pl.feature.toggle.service.read.infrastructure.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.read.domain.FeatureToggle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeFeatureToggleReadRepository implements FeatureToggleReadRepository {

    private final Map<FeatureToggleId, FeatureToggle> featureToggles = new HashMap<>();

    @Override
    public FeatureToggle getById(FeatureToggleId id) {
        return featureToggles.get(id);
    }

    @Override
    public List<FeatureToggle> getAll() {
        return featureToggles.values().stream().toList();
    }

    public void insert(FeatureToggle featureToggle) {
        featureToggles.put(featureToggle.id(), featureToggle);
    }

    public void clear(){
        featureToggles.clear();
    }

    public void deleteById(FeatureToggleId id){
        featureToggles.remove(id);
    }
}
