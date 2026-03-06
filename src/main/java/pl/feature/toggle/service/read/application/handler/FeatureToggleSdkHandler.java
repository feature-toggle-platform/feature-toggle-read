package pl.feature.toggle.service.read.application.handler;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleSdkUseCase;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.infrastructure.in.rest.sdk.dto.FeatureToggleSdkSnapshot;

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
                .orElseThrow(() -> new IllegalStateException("Project not found: " + projectId));

        var environmentView = environmentQueryRepository.find(projectId, environmentId)
                .orElseThrow(() -> new IllegalStateException("Environment not found: " + environmentId));

        var featureToggleViews = featureToggleQueryRepository.findByEnvironment(projectId, environmentId);

        var scope = FeatureToggleSdkSnapshot.createScope(projectView, environmentView);
        var status = FeatureToggleSdkSnapshot.createStatus(projectView, environmentView);

        return new FeatureToggleSdkSnapshot(
                scope,
                status,
                environmentView.revision().value(),
                Instant.now(clock),
                featureToggleViews.stream()
                        .map(FeatureToggleSdkSnapshot::createToggle)
                        .toList()
        );
    }

}
