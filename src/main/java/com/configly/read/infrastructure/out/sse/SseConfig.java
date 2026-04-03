package com.configly.read.infrastructure.out.sse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.configly.read.application.port.out.sse.SseClients;

@Configuration
class SseConfig {

    @Bean
    SseClients sseClients() {
        return InMemorySseClients.create();
    }
}
