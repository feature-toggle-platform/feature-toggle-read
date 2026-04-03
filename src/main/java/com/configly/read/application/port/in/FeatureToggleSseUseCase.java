package com.configly.read.application.port.in;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;

public interface FeatureToggleSseUseCase {

    void establish(ProjectId projectId, EnvironmentId environmentId, SseConnection sseConnection);
}
