package pl.feature.toggle.service.read.infrastructure.out.redis.dto;

import java.time.Instant;

public record FeatureToggleRedisDto(
        String id,
        String projectId,
        String environmentId,
        String name,
        String description,
        String type,
        String value,
        Instant createdAt,
        Instant updatedAt
) {
}