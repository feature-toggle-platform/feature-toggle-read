package com.configly.read.domain;

import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;
import com.configly.contracts.event.environment.EnvironmentTypeChanged;
import com.configly.contracts.event.environment.EnvironmentUpdated;
import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentName;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;

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
