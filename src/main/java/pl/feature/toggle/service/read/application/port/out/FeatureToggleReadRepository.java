package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggle;

import java.util.List;

public interface FeatureToggleReadRepository {

    FeatureToggle getById(FeatureToggleId id);

    List<FeatureToggle> getAll();
}
