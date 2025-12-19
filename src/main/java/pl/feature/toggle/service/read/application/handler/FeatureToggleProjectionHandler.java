package pl.feature.toggle.service.read.application.handler;


import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjectionUseCase;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleSnapshotRepository;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
class FeatureToggleProjectionHandler implements FeatureToggleProjectionUseCase {

    private final FeatureToggleSnapshotRepository repository;

    @Override
    @Transactional
    public void handle(FeatureToggleCreated event) {
        var featureToggle = FeatureToggle.from(event);
        repository.save(featureToggle);
    }

    @Override
    public void handle(FeatureToggleDeleted event) {
        repository.delete(FeatureToggleId.create(event.id()));
    }

    @Override
    public void handle(FeatureToggleUpdated event) {
        // TODO
    }
}
