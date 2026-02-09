package pl.feature.toggle.service.read.application.projection.environment.event;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

public record EnvironmentViewRebuildRequested(
        ProjectId projectId,
        EnvironmentId environmentId
) {
}
