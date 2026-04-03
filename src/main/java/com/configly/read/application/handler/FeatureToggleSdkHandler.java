package com.configly.read.application.handler;

import lombok.AllArgsConstructor;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.in.FeatureToggleSdkUseCase;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;
import com.configly.read.domain.exception.EnvironmentNotFoundException;
import com.configly.read.domain.exception.ProjectNotFoundException;
import com.configly.read.infrastructure.in.rest.sdk.dto.FeatureToggleSdkSnapshot;

import java.time.Clock;
import java.time.Instant;

@AllArgsConstructor
class FeatureToggleSdkHandler implements FeatureToggleSdkUseCase {

    private final ProjectQueryRepository projectQueryRepository;
    private final EnvironmentQueryRepository environmentQueryRepository;
    private final FeatureToggleQueryRepository featureToggleQueryRepository;
    private final Clock clock;

    @Override
    public FeatureToggleSdkSnapshot fetchSnapshot(ProjectId projectId, EnvironmentId environmentId) {
        var projectView = projectQueryRepository.find(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        var environmentView = environmentQueryRepository.find(projectId, environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(environmentId));

        var featureToggleViews = featureToggleQueryRepository.find(projectId, environmentId);

        var scope = FeatureToggleSdkSnapshot.createScope(projectView, environmentView);
        var status = FeatureToggleSdkSnapshot.createStatus(projectView, environmentView);

        return new FeatureToggleSdkSnapshot(
                scope,
                status,
                environmentView.revision().value(),
                Instant.now(clock),
                featureToggleViews
                        .stream()
                        .map(FeatureToggleSdkSnapshot::createToggle)
                        .toList()
        );
    }

}
