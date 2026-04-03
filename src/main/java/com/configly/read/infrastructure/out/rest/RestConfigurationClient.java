package com.configly.read.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.web.client.InternalRestClient;
import com.configly.web.client.ServiceId;
import com.configly.read.application.port.out.ConfigurationClient;
import com.configly.read.domain.EnvironmentView;
import com.configly.read.domain.ProjectView;
import com.configly.read.infrastructure.out.rest.dto.EnvironmentViewDto;
import com.configly.read.infrastructure.out.rest.dto.ProjectViewDto;

@AllArgsConstructor
class RestConfigurationClient implements ConfigurationClient {

    private final InternalRestClient internalRestClient;

    @Override
    public ProjectView fetchProject(ProjectId projectId) {
        return internalRestClient.get(
                        ServiceId.CONFIGURATION,
                        "/internal/projects/{projectId}/view",
                        ProjectViewDto.class,
                        projectId.idAsString()
                )
                .toDomain();
    }

    @Override
    public EnvironmentView fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        return internalRestClient.get(
                        ServiceId.CONFIGURATION,
                        "/internal/projects/{projectId}/environments/{environmentId}/view",
                        EnvironmentViewDto.class,
                        projectId.idAsString(),
                        environmentId.idAsString()
                )
                .toDomain();
    }

}
