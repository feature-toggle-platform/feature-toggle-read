package com.configly.read.infrastructure.out.rest.dto;

import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentName;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.read.domain.EnvironmentView;

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
