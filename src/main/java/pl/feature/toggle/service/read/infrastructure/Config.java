package pl.feature.toggle.service.read.infrastructure;

import pl.feature.toggle.service.read.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjectionUseCase;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleSnapshotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("featureToggleReadConfiguration")
class Config {

    @Bean
    FeatureToggleReadUseCase featureToggleReadUseCase(FeatureToggleReadRepository repository) {
        return FeatureToggleHandlerFacade.featureToggleReadUseCase(repository);
    }

    @Bean
    FeatureToggleProjectionUseCase featureToggleProjectionUseCase(FeatureToggleSnapshotRepository repository) {
        return FeatureToggleHandlerFacade.featureToggleProjectionUseCase(repository);
    }
}
