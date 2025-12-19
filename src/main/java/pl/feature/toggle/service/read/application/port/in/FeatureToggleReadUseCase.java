package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.infrastructure.in.rest.view.FeatureToggleView;

import java.util.List;

public interface FeatureToggleReadUseCase {

    FeatureToggleView getFeatureToggle(FeatureToggleId id);

    List<FeatureToggleView> getAll();
}
