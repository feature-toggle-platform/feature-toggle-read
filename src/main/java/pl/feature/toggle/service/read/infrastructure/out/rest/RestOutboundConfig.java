package pl.feature.toggle.service.read.infrastructure.out.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleClient;

@Configuration
class RestOutboundConfig {

    @Bean
    @Qualifier("configurationRestClient")
    RestClient configurationRestClient(@Value("${client.configuration.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    @Qualifier("featureToggleRestClient")
    RestClient featureToggleRestClient(@Value("${client.feature-toggle.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    FeatureToggleClient writeClient(@Qualifier("featureToggleRestClient") RestClient restClient, CorrelationProvider correlationProvider) {
        return new RestFeatureToggleClient(restClient, correlationProvider);
    }

    @Bean
    ConfigurationClient configurationClient(@Qualifier("configurationRestClient") RestClient restClient, CorrelationProvider correlationProvider) {
        return new RestConfigurationClient(restClient, correlationProvider);
    }

}
