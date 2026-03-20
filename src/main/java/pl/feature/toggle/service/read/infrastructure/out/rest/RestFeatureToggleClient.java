package pl.feature.toggle.service.read.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.web.client.InternalRestClient;
import pl.feature.toggle.service.web.client.ServiceId;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleClient;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.infrastructure.out.rest.dto.FeatureToggleViewDto;
import pl.feature.toggle.service.read.infrastructure.out.rest.exception.ConfigurationServiceResponseException;

import java.util.Map;

@AllArgsConstructor
class RestFeatureToggleClient implements FeatureToggleClient {

    private final InternalRestClient internalRestClient;

    @Override
    public FeatureToggleView fetchFeatureToggle(FeatureToggleId featureToggleId) {
        return internalRestClient.get(
                        ServiceId.WRITE,
                        "/internal/feature-toggles/{featureToggleId}/view",
                        FeatureToggleViewDto.class,
                        featureToggleId.idAsString()
                )
                .toDomain();
    }
}
