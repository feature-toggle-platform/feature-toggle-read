package com.configly.read.infrastructure.out.rest.dto;

import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.read.domain.FeatureToggleView;
import com.configly.value.FeatureToggleValueBuilder;
import com.configly.value.FeatureToggleValueSnapshot;

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
