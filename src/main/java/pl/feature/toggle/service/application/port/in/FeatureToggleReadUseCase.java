package pl.feature.toggle.service.application.port.in;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.infrastructure.in.rest.view.FeatureToggleView;

import java.util.List;

public interface FeatureToggleReadUseCase {

    FeatureToggleView getFeatureToggle(FeatureToggleId id);

    List<FeatureToggleView> getAll();
}
