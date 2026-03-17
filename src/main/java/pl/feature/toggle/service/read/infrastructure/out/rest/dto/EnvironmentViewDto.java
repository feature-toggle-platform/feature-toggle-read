package pl.feature.toggle.service.read.infrastructure.out.rest.dto;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentName;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.EnvironmentView;

import java.time.LocalDateTime;

public record EnvironmentViewDto(
        String id,
        String projectId,
        String name,
        String type,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long revision
) {

    public EnvironmentView toDomain() {
        return new EnvironmentView(
                EnvironmentId.create(id),
                ProjectId.create(projectId),
                EnvironmentName.create(name),
                type,
                EnvironmentStatus.valueOf(status),
                CreatedAt.of(createdAt),
                UpdatedAt.of(updatedAt),
                Revision.from(revision),
                true
        );
    }
}
