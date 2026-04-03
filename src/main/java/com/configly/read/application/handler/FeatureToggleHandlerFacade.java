package com.configly.read.application.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import com.configly.read.application.port.in.FeatureToggleSdkUseCase;
import com.configly.read.application.port.in.FeatureToggleSseUseCase;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;
import com.configly.read.application.port.out.sse.SseClients;

import java.time.Clock;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static FeatureToggleSdkUseCase featureToggleSdkUseCase(
            ProjectQueryRepository projectQueryRepository,
            EnvironmentQueryRepository environmentQueryRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            Clock clock
    ) {
        return new FeatureToggleSdkHandler(projectQueryRepository, environmentQueryRepository, featureToggleQueryRepository, clock);
    }

    public static FeatureToggleSseUseCase featureToggleSseUseCase(SseClients sseClients) {
        return new FeatureToggleSseHandler(sseClients);
    }
}
