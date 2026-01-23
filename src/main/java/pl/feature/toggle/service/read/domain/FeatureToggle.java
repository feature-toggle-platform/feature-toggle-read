package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.*;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValue;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.model.project.ProjectId;

public record FeatureToggle(
        FeatureToggleId id,
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        CreatedAt createdAt,
        UpdatedAt updatedAt
) {

    public static FeatureToggle from(FeatureToggleCreated event) {
        return new FeatureToggle(
                FeatureToggleId.create(event.id()),
                ProjectId.create(event.projectId()),
                EnvironmentId.create(event.environmentId()),
                FeatureToggleName.create(event.name()),
                FeatureToggleDescription.create(event.description()),
                FeatureToggleValueBuilder.from(event.value(), event.type()),
                CreatedAt.of(event.createdAt()),
                UpdatedAt.of(event.updatedAt())
        );
    }

}
