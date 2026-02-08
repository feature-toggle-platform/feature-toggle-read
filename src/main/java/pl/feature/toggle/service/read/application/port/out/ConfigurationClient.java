package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.ProjectView;

public interface ConfigurationClient {

    ProjectView fetchProject(ProjectId projectId);

    EnvironmentView fetchEnvironment(ProjectId projectId, EnvironmentId environmentId);

}
