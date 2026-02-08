package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.EnvironmentView;

import java.util.Optional;

public interface EnvironmentQueryRepository {

    Optional<EnvironmentView> find(ProjectId projectId, EnvironmentId environmentId);

    Optional<EnvironmentView> findConsistent(ProjectId projectId, EnvironmentId environmentId);
}
