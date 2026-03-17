package pl.feature.toggle.service.read.infrastructure.out.rest.dto;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.project.ProjectDescription;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectName;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.time.LocalDateTime;

public record ProjectViewDto(
        String id,
        String name,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long revision
) {

    public ProjectView toDomain() {
        return new ProjectView(
                ProjectId.create(id),
                ProjectName.create(name),
                ProjectDescription.create(description),
                ProjectStatus.valueOf(status),
                CreatedAt.of(createdAt),
                UpdatedAt.of(updatedAt),
                Revision.from(revision),
                true
        );
    }
}
