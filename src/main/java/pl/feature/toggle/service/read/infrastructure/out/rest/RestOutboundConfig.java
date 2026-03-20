package pl.feature.toggle.service.read.infrastructure.out.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import pl.feature.toggle.service.web.client.InternalRestClient;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleClient;

@Configuration
class RestOutboundConfig {

    @Bean
    FeatureToggleClient writeClient(InternalRestClient internalRestClient) {
        return new RestFeatureToggleClient(internalRestClient);
    }

    @Bean
    ConfigurationClient configurationClient(InternalRestClient internalRestClient) {
        return new RestConfigurationClient(internalRestClient);
    }

}
