package pl.feature.toggle.service.read.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.client.InternalRestClient;
import pl.feature.toggle.service.web.client.ServiceId;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.ProjectView;
import pl.feature.toggle.service.read.infrastructure.out.rest.dto.EnvironmentViewDto;
import pl.feature.toggle.service.read.infrastructure.out.rest.dto.ProjectViewDto;
import pl.feature.toggle.service.read.infrastructure.out.rest.exception.ConfigurationServiceResponseException;

import java.util.Map;

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
