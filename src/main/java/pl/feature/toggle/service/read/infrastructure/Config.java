package pl.feature.toggle.service.read.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.read.application.port.in.*;
import pl.feature.toggle.service.read.application.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.read.application.projection.environment.EnvironmentProjectionFacade;
import pl.feature.toggle.service.read.application.projection.featuretoggle.FeatureToggleProjectionFacade;
import pl.feature.toggle.service.read.application.projection.project.ProjectProjectionFacade;

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
    FeatureToggleViewConsistency featureToggleViewConsistency(WriteClient writeClient,
                                                              FeatureToggleProjectionRepository featureToggleProjectionRepository,
                                                              FeatureToggleQueryRepository queryRepository) {
        return FeatureToggleProjectionFacade.featureToggleViewConsistency(writeClient,
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
}
