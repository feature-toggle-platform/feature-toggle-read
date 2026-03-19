package pl.feature.toggle.service.read.application.projection.project.event;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.correlation.CorrelationId;

public record ProjectViewRebuildRequested(
        ProjectId projectId,
        CorrelationId correlationId
) {
}
