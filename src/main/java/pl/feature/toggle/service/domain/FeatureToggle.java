package pl.feature.toggle.service.domain;

import com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated;
import com.ftaas.domain.featuretoggle.FeatureToggleType;
import com.ftaas.domain.CreatedAt;
import com.ftaas.domain.UpdatedAt;
import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.featuretoggle.*;
import com.ftaas.domain.project.ProjectId;

public record FeatureToggle(
        FeatureToggleId id,
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleType type,
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
                FeatureToggleType.valueOf(event.type()),
                FeatureToggleValueRecognizer.from(event.type(), event.value()),
                CreatedAt.of(event.createdAt()),
                UpdatedAt.of(event.updatedAt())
        );
    }

}
