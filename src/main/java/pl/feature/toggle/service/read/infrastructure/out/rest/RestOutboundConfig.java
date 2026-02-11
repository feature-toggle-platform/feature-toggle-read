package pl.feature.toggle.service.read.infrastructure.out.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.WriteClient;

@Configuration
class RestOutboundConfig {

    @Bean
    WriteClient writeClient() {
        return new RestWriteClient();
    }

    @Bean
    ConfigurationClient configurationClient() {
        return new RestConfigurationClient();
    }

}
