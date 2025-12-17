package pl.feature.toggle.service.application.handler;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.infrastructure.in.rest.view.FeatureToggleView;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
class FeatureToggleReadHandler implements FeatureToggleReadUseCase {

    private final FeatureToggleReadRepository repository;


    @Override
    public FeatureToggleView getFeatureToggle(FeatureToggleId id) {
        var featureToggle = repository.getById(id);
        return FeatureToggleView.from(featureToggle);
    }


    @Override
    public List<FeatureToggleView> getAll() {
        return repository.getAll()
                .stream()
                .map(FeatureToggleView::from)
                .toList();
    }
}
