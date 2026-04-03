package com.configly.read.application.port.in;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.infrastructure.in.rest.sdk.dto.FeatureToggleSdkSnapshot;

public interface FeatureToggleSdkUseCase {

    FeatureToggleSdkSnapshot fetchSnapshot(ProjectId projectId, EnvironmentId environmentId);

}
