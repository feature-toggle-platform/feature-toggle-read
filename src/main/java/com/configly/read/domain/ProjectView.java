package com.configly.read.domain;

import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.event.project.ProjectStatusChanged;
import com.configly.contracts.event.project.ProjectUpdated;
import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.project.ProjectDescription;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectName;
import com.configly.model.project.ProjectStatus;

public record ProjectView(
        ProjectId id,
        ProjectName name,
        ProjectDescription description,
        ProjectStatus status,
        CreatedAt createdAt,
        UpdatedAt updatedAt,
        Revision revision,
        boolean consistent
) {

    public static ProjectView create(ProjectCreated event) {
        return new ProjectView(
                ProjectId.create(event.projectId()),
                ProjectName.create(event.projectName()),
                ProjectDescription.create(event.projectDescription()),
                ProjectStatus.valueOf(event.status()),
                CreatedAt.of(event.createdAt()),
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                true
        );
    }

    public ProjectView apply(ProjectStatusChanged event) {
        return new ProjectView(
                this.id,
                this.name,
                this.description,
                ProjectStatus.valueOf(event.status()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                this.consistent
        );
    }

    public ProjectView apply(ProjectUpdated event) {
        return new ProjectView(
                this.id,
                ProjectName.create(event.projectName()),
                ProjectDescription.create(event.projectDescription()),
                this.status,
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                this.consistent
        );
    }
}
