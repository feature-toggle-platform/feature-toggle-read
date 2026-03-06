package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.infrastructure.in.rest.featuretoggle.dto.FeatureToggleForProjectView;

import java.util.List;

public interface FeatureToggleReadUseCase {

    FeatureToggleForProjectView getFeatureToggle(FeatureToggleId id);

    List<FeatureToggleForProjectView> getAll();
}
