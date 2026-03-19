package pl.feature.toggle.service.read.application.projection.environment.event;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.correlation.CorrelationId;

public record EnvironmentViewRebuildRequested(
        ProjectId projectId,
        EnvironmentId environmentId,
        CorrelationId correlationId
) {
}
