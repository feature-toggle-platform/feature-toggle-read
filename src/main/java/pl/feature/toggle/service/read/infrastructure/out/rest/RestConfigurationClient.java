package pl.feature.toggle.service.read.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
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

    private final RestClient restClient;
    private final CorrelationProvider correlationProvider;

    @Override
    public ProjectView fetchProject(ProjectId projectId) {
        var uri = "/internal/projects/{projectId}/view";
        return fetch(uri, ProjectViewDto.class, projectId.idAsString())
                .toDomain();
    }

    @Override
    public EnvironmentView fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        var uri = "/internal/projects/{projectId}/environments/{environmentId}/view";
        return fetch(uri, EnvironmentViewDto.class, projectId.idAsString(), environmentId.idAsString())
                .toDomain();
    }

    private <T> T fetch(String uri, Class<T> responseClass, Object... parameters) {
        try {
            var result = restClient.get()
                    .uri(uri, parameters)
                    .header(CorrelationId.headerName(), correlationProvider.current().value())
                    .retrieve()
                    .body(responseClass);
            if (result == null) {
                throw new ConfigurationServiceResponseException(
                        "Empty response from configuration service",
                        context(uri, null)
                );
            }
            return result;
        } catch (RestClientException ex) {
            throw new ConfigurationServiceResponseException(
                    "Failed to fetch reference from configuration service",
                    context(uri, ex)
            );
        }
    }

    private Map<String, Object> context(String uri, Exception ex) {
        var contextBuilder = ContextBuilder.create()
                .with("uri", uri)
                .with("correlationId", correlationProvider.current());
        if (ex != null) {
            contextBuilder.with("exception", ex);
            if (ex instanceof HttpClientErrorException httpEx) {
                contextBuilder.with("statusCode", httpEx.getStatusCode());
            }
        }
        return contextBuilder.build();
    }

}
