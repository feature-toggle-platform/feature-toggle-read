package pl.feature.toggle.service.read.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleClient;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.infrastructure.out.rest.dto.FeatureToggleViewDto;
import pl.feature.toggle.service.read.infrastructure.out.rest.exception.ConfigurationServiceResponseException;

import java.util.Map;

@AllArgsConstructor
class RestFeatureToggleClient implements FeatureToggleClient {

    private final RestClient restClient;
    private final CorrelationProvider correlationProvider;

    @Override
    public FeatureToggleView fetchFeatureToggle(FeatureToggleId featureToggleId) {
        var uri = "/internal/feature-toggles/{featureToggleId}/view";
        return fetch(uri, FeatureToggleViewDto.class, featureToggleId.idAsString())
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
