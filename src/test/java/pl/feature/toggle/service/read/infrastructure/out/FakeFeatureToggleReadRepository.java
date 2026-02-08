package pl.feature.toggle.service.read.infrastructure.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeFeatureToggleReadRepository implements FeatureToggleQueryRepository {

    private final Map<FeatureToggleId, FeatureToggleView> featureToggles = new HashMap<>();

    @Override
    public FeatureToggleView getById(FeatureToggleId id) {
        return featureToggles.get(id);
    }

    @Override
    public List<FeatureToggleView> getAll() {
        return featureToggles.values().stream().toList();
    }

    public void insert(FeatureToggleView featureToggleView) {
        featureToggles.put(featureToggleView.id(), featureToggleView);
    }

    public void clear(){
        featureToggles.clear();
    }

    public void deleteById(FeatureToggleId id){
        featureToggles.remove(id);
    }
}
