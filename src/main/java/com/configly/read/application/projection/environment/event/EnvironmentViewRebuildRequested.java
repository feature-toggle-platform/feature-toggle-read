package com.configly.read.application.projection.environment.event;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.web.model.correlation.CorrelationId;

public record EnvironmentViewRebuildRequested(
        ProjectId projectId,
        EnvironmentId environmentId,
        CorrelationId correlationId
) {
}
