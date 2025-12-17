package pl.feature.toggle.service.application.port.out;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.domain.FeatureToggle;

import java.util.List;

public interface FeatureToggleReadRepository {

    FeatureToggle getById(FeatureToggleId id);

    List<FeatureToggle> getAll();
}
