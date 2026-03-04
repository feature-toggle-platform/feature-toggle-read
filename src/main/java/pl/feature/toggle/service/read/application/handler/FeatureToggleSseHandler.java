package pl.feature.toggle.service.read.application.handler;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleSseUseCase;

class FeatureToggleSseHandler implements FeatureToggleSseUseCase {

    @Override
    public void establishSSEConnection(ProjectId projectId, EnvironmentId environmentId) {

    }
}
