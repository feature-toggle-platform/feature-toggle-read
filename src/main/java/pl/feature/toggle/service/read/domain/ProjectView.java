package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
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
                this.description,
                this.status,
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                this.consistent
        );
    }
}
