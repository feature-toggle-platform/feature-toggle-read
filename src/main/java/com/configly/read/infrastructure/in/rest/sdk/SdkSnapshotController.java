package com.configly.read.infrastructure.in.rest.sdk;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.in.FeatureToggleSdkUseCase;
import com.configly.read.infrastructure.in.rest.sdk.dto.FeatureToggleSdkSnapshot;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/sdk")
class SdkSnapshotController {

    private final FeatureToggleSdkUseCase sdkUseCase;

    @GetMapping("/projects/{projectId}/environments/{environmentId}/feature-toggles")
    public FeatureToggleSdkSnapshot fetchFeatureTogglesSnapshot(@PathVariable String projectId, @PathVariable String environmentId) {
        return sdkUseCase.fetchSnapshot(ProjectId.create(projectId), EnvironmentId.create(environmentId));
    }
}
