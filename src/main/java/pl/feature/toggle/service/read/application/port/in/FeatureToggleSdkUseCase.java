package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.infrastructure.in.rest.dto.FeatureToggleSdkSnapshot;

public interface FeatureToggleSdkUseCase {

    FeatureToggleSdkSnapshot fetchSnapshot(ProjectId projectId, EnvironmentId environmentId);

}
