package pl.feature.toggle.service.read.infrastructure.out.rest;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.ProjectView;

class RestConfigurationClient implements ConfigurationClient {
    @Override
    public ProjectView fetchProject(ProjectId projectId) {
        return null;
    }

    @Override
    public EnvironmentView fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        return null;
    }
}
