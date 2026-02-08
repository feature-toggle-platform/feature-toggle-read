package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectDescription;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectName;
import pl.feature.toggle.service.model.project.ProjectStatus;

public record ProjectView(
        ProjectId id,
        ProjectName name,
        ProjectDescription description,
        ProjectStatus status,
        CreatedAt createdAt,
        Revision revision,
        boolean consistent
) {
}
