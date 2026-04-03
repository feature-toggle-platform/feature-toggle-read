package com.configly.read.application.projection.project.event;

import com.configly.model.project.ProjectId;
import com.configly.web.correlation.CorrelationId;

public record ProjectViewRebuildRequested(
        ProjectId projectId,
        CorrelationId correlationId
) {
}
