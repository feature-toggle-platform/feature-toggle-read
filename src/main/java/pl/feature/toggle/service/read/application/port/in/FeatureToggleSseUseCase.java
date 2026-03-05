package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

public interface FeatureToggleSseUseCase {

    void establish(ProjectId projectId, EnvironmentId environmentId, SseConnection sseConnection);
}
