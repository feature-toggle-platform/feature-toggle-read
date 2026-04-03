package com.configly.read.infrastructure.out.rest.dto;

import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.project.ProjectDescription;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectName;
import com.configly.model.project.ProjectStatus;
import com.configly.read.domain.ProjectView;

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
