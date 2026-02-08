package pl.feature.toggle.service.read.infrastructure;

import pl.feature.toggle.service.read.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("featureToggleReadConfiguration")
class Config {

    @Bean
    FeatureToggleReadUseCase featureToggleReadUseCase(FeatureToggleQueryRepository repository) {
        return FeatureToggleHandlerFacade.featureToggleReadUseCase(repository);
    }

    @Bean
    FeatureToggleProjection featureToggleProjectionUseCase(FeatureToggleProjectionRepository repository) {
        return FeatureToggleHandlerFacade.featureToggleProjectionUseCase(repository);
    }
}
