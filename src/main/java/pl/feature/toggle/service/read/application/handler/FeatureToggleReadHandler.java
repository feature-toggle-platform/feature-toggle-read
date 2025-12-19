package pl.feature.toggle.service.read.application.handler;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.read.infrastructure.in.rest.view.FeatureToggleView;
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
