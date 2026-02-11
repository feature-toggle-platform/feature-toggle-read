package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.value.FeatureToggleValue;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.raw.FeatureToggleRawValue;

public record FeatureToggleView(
        FeatureToggleId id,
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        FeatureToggleStatus status,
        Revision revision,
        CreatedAt createdAt,
        UpdatedAt updatedAt,
        boolean consistent
) {


    public static FeatureToggleView create(FeatureToggleCreated event) {
        return new FeatureToggleView(
                FeatureToggleId.create(event.id()),
                ProjectId.create(event.projectId()),
                EnvironmentId.create(event.environmentId()),
                FeatureToggleName.create(event.name()),
                FeatureToggleDescription.create(event.description()),
                FeatureToggleValueBuilder.from(FeatureToggleRawValue.of(event.value()), event.type()),
                FeatureToggleStatus.valueOf(event.status()),
                Revision.from(event.revision()),
                CreatedAt.of(event.createdAt()),
                UpdatedAt.of(event.updatedAt()),
                true
        );
    }

    public FeatureToggleView apply(FeatureToggleUpdated event) {
        return new FeatureToggleView(
                this.id,
                this.projectId,
                this.environmentId,
                FeatureToggleName.create(event.name()),
                FeatureToggleDescription.create(event.description()),
                this.value,
                this.status,
                Revision.from(event.revision()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                this.consistent
        );
    }

    public FeatureToggleView apply(FeatureToggleValueChanged event) {
        return new FeatureToggleView(
                this.id,
                this.projectId,
                this.environmentId,
                this.name,
                this.description,
                FeatureToggleValueBuilder.from(FeatureToggleRawValue.of(event.value()), event.type()),
                this.status,
                Revision.from(event.revision()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                this.consistent
        );
    }

    public FeatureToggleView apply(FeatureToggleStatusChanged event) {
        return new FeatureToggleView(
                this.id,
                this.projectId,
                this.environmentId,
                this.name,
                this.description,
                this.value,
                FeatureToggleStatus.valueOf(event.status()),
                Revision.from(event.revision()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                this.consistent
        );
    }
}
