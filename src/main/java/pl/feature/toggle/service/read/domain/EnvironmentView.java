package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentTypeChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentUpdated;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
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
        UpdatedAt updatedAt,
        Revision revision,
        boolean consistent
) {

    public static EnvironmentView create(EnvironmentCreated event) {
        return new EnvironmentView(
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                EnvironmentName.create(event.environmentName()),
                event.type(),
                EnvironmentStatus.valueOf(event.status()),
                CreatedAt.of(event.createdAt()),
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                true
        );
    }

    public EnvironmentView apply(EnvironmentUpdated event) {
        return new EnvironmentView(
                this.id,
                this.projectId,
                EnvironmentName.create(event.environmentName()),
                this.type,
                this.status,
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                this.consistent
        );
    }

    public EnvironmentView apply(EnvironmentTypeChanged event) {
        return new EnvironmentView(
                this.id,
                this.projectId,
                this.name,
                event.type(),
                this.status,
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                this.consistent
        );
    }

    public EnvironmentView apply(EnvironmentStatusChanged event) {
        return new EnvironmentView(
                this.id,
                this.projectId,
                this.name,
                this.type,
                EnvironmentStatus.valueOf(event.status()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                Revision.from(event.revision()),
                this.consistent
        );
    }
}
