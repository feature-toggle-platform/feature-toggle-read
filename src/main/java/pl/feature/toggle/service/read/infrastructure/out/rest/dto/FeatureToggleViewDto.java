package pl.feature.toggle.service.read.infrastructure.out.rest.dto;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;

import java.time.LocalDateTime;

public record FeatureToggleViewDto(
        String id,
        String environmentId,
        String name,
        String description,
        String value,
        String type,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long revision
) {

    public FeatureToggleView toDomain() {
        return new FeatureToggleView(
                FeatureToggleId.create(id),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(name),
                FeatureToggleDescription.create(description),
                FeatureToggleValueBuilder.from(FeatureToggleValueSnapshot.of(value), type),
                FeatureToggleStatus.valueOf(status),
                Revision.from(revision),
                CreatedAt.of(createdAt),
                UpdatedAt.of(updatedAt),
                true
        );
    }
}
