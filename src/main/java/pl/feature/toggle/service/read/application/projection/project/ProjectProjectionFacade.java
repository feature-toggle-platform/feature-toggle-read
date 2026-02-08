package pl.feature.toggle.service.read.application.projection.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;
import pl.feature.toggle.service.read.application.port.in.ProjectViewConsistency;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectProjectionFacade {

    public static ProjectProjection projectProjection() {
        return new ProjectProjectionHandler();
    }

    public static ProjectViewConsistency projectViewConsistency(ConfigurationClient configurationClient,
                                                                ProjectProjectionRepository projectionRepository,
                                                                ProjectQueryRepository projectQueryRepository) {
        return new DefaultProjectViewConsistency(configurationClient, projectionRepository, projectQueryRepository);
    }

}
