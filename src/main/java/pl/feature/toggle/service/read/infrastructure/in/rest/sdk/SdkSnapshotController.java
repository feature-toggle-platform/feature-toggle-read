package pl.feature.toggle.service.read.infrastructure.in.rest.sdk;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleSdkUseCase;
import pl.feature.toggle.service.read.infrastructure.in.rest.sdk.dto.FeatureToggleSdkSnapshot;

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
