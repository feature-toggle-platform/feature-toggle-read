package com.configly.read.application.projection.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.read.application.port.in.ProjectProjection;
import com.configly.read.application.port.in.ProjectViewConsistency;
import com.configly.read.application.port.out.ConfigurationClient;
import com.configly.read.application.port.out.ProjectProjectionRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectProjectionFacade {

    public static ProjectProjection projectProjection(
            ProjectProjectionRepository projectionRepository,
            ProjectQueryRepository projectQueryRepository,
            RevisionProjectionApplier revisionProjectionApplier,
            ApplicationEventPublisher eventPublisher
    ) {
        return new ProjectProjectionHandler(projectionRepository,
                projectQueryRepository,
                revisionProjectionApplier,
                eventPublisher);
    }

    public static ProjectViewConsistency projectViewConsistency(ConfigurationClient configurationClient,
                                                                ProjectProjectionRepository projectionRepository,
                                                                ProjectQueryRepository projectQueryRepository) {
        return new DefaultProjectViewConsistency(configurationClient,
                projectionRepository,
                projectQueryRepository);
    }

}
