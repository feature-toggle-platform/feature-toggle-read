package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.*;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import pl.feature.toggle.service.read.infrastructure.out.redis.dto.FeatureToggleRedisDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class FeatureToggleRedisMapper {

    static FeatureToggleRedisDto toDto(FeatureToggle domain) {
        return new FeatureToggleRedisDto(
                domain.id().idAsString(),
                domain.projectId().idAsString(),
                domain.environmentId().idAsString(),
                domain.name().value(),
                domain.description().value(),
                domain.value().typeName(),
                domain.value().asText(),
                domain.createdAt().timestamp(),
                domain.updatedAt().timestamp()
        );
    }

    static FeatureToggle toDomain(FeatureToggleRedisDto dto) {
        return new FeatureToggle(
                FeatureToggleId.create(dto.id()),
                ProjectId.create(dto.projectId()),
                EnvironmentId.create(dto.environmentId()),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()),
                FeatureToggleValueBuilder.from(dto.value(), dto.type()),
                CreatedAt.of(dto.createdAt()),
                UpdatedAt.of(dto.updatedAt())
        );
    }

}
