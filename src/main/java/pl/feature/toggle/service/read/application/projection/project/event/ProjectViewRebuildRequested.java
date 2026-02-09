package pl.feature.toggle.service.read.application.projection.project.event;

import pl.feature.toggle.service.model.project.ProjectId;

public record ProjectViewRebuildRequested(
        ProjectId projectId
) {
}
