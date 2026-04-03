package com.configly.read.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.read.application.handler.FeatureToggleHandlerFacade;
import com.configly.read.application.handler.FeatureToggleSseNotifier;
import com.configly.read.application.port.in.*;
import com.configly.read.application.port.out.*;
import com.configly.read.application.port.out.sse.SseClients;
import com.configly.read.application.projection.environment.EnvironmentProjectionFacade;
import com.configly.read.application.projection.featuretoggle.FeatureToggleProjectionFacade;
import com.configly.read.application.projection.project.ProjectProjectionFacade;

import java.time.Clock;

@Configuration("featureToggleReadConfiguration")
class Config {

    @Bean
    FeatureToggleProjection featureToggleProjection(FeatureToggleProjectionRepository featureToggleProjectionRepository,
                                                    FeatureToggleQueryRepository featureToggleQueryRepository,
                                                    RevisionProjectionApplier revisionProjectionApplier,
                                                    ApplicationEventPublisher applicationEventPublisher) {
        return FeatureToggleProjectionFacade.featureToggleProjection(featureToggleProjectionRepository,
                featureToggleQueryRepository,
                revisionProjectionApplier,
                applicationEventPublisher);
    }

    @Bean
    FeatureToggleViewConsistency featureToggleViewConsistency(FeatureToggleClient featureToggleClient,
                                                              FeatureToggleProjectionRepository featureToggleProjectionRepository,
                                                              FeatureToggleQueryRepository queryRepository) {
        return FeatureToggleProjectionFacade.featureToggleViewConsistency(featureToggleClient,
                featureToggleProjectionRepository,
                queryRepository);
    }

    @Bean
    ProjectProjection projectProjection(ProjectProjectionRepository projectionRepository,
                                        ProjectQueryRepository projectQueryRepository,
                                        RevisionProjectionApplier revisionProjectionApplier,
                                        ApplicationEventPublisher applicationEventPublisher) {
        return ProjectProjectionFacade.projectProjection(projectionRepository,
                projectQueryRepository,
                revisionProjectionApplier,
                applicationEventPublisher);
    }

    @Bean
    ProjectViewConsistency projectViewConsistency(ConfigurationClient configurationClient,
                                                  ProjectProjectionRepository projectionRepository,
                                                  ProjectQueryRepository queryRepository) {
        return ProjectProjectionFacade.projectViewConsistency(configurationClient,
                projectionRepository,
                queryRepository);
    }

    @Bean
    EnvironmentProjection environmentProjection(EnvironmentProjectionRepository projectionRepository,
                                                EnvironmentQueryRepository queryRepository,
                                                RevisionProjectionApplier revisionProjectionApplier,
                                                ApplicationEventPublisher applicationEventPublisher) {
        return EnvironmentProjectionFacade.environmentProjection(projectionRepository,
                queryRepository,
                revisionProjectionApplier,
                applicationEventPublisher);
    }

    @Bean
    EnvironmentViewConsistency environmentViewConsistency(ConfigurationClient configurationClient,
                                                          EnvironmentProjectionRepository projectionRepository,
                                                          EnvironmentQueryRepository queryRepository) {
        return EnvironmentProjectionFacade.environmentViewConsistency(configurationClient,
                projectionRepository,
                queryRepository);
    }

    @Bean
    FeatureToggleSseUseCase featureToggleSseUseCase(SseClients sseClients) {
        return FeatureToggleHandlerFacade.featureToggleSseUseCase(sseClients);
    }

    @Bean
    FeatureToggleSdkUseCase featureToggleSdkUseCase(
            ProjectQueryRepository projectQueryRepository,
            EnvironmentQueryRepository environmentQueryRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository
    ) {
        return FeatureToggleHandlerFacade.featureToggleSdkUseCase(projectQueryRepository, environmentQueryRepository,
                featureToggleQueryRepository, Clock.systemUTC());
    }

    @Bean
    FeatureToggleSseNotifier featureToggleSseNotifier(SseClients sseClients) {
        return new FeatureToggleSseNotifier(sseClients);
    }
}
