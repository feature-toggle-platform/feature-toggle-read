package pl.feature.toggle.service.application.handler;


import com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.application.port.in.FeatureToggleProjectionUseCase;
import pl.feature.toggle.service.application.port.out.FeatureToggleSnapshotRepository;
import pl.feature.toggle.service.domain.FeatureToggle;
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
