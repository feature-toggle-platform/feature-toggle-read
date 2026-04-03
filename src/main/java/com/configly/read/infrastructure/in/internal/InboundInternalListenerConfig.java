package com.configly.read.infrastructure.in.internal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.configly.read.application.port.in.EnvironmentViewConsistency;
import com.configly.read.application.port.in.FeatureToggleViewConsistency;
import com.configly.read.application.port.in.ProjectViewConsistency;

@Configuration
class InboundInternalListenerConfig {

    @Bean
    EnvironmentViewRebuildListener environmentViewRebuildListener(EnvironmentViewConsistency consistency) {
        return new EnvironmentViewRebuildListener(consistency);
    }

    @Bean
    ProjectViewRebuildListener projectViewRebuildListener(ProjectViewConsistency consistency) {
        return new ProjectViewRebuildListener(consistency);
    }

    @Bean
    FeatureToggleViewRebuildListener featureToggleViewRebuildListener(FeatureToggleViewConsistency consistency) {
        return new FeatureToggleViewRebuildListener(consistency);
    }
}
