package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentName;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;

public record EnvironmentView(
        EnvironmentId id,
        ProjectId projectId,
        EnvironmentName name,
        String type,
        EnvironmentStatus status,
        CreatedAt createdAt,
        Revision revision,
        boolean consistent
) {
}
