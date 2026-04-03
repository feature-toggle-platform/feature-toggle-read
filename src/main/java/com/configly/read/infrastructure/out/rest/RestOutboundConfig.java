package com.configly.read.infrastructure.out.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.configly.web.client.InternalRestClient;
import com.configly.read.application.port.out.ConfigurationClient;
import com.configly.read.application.port.out.FeatureToggleClient;

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
