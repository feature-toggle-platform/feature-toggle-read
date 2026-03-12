package pl.feature.toggle.service.read.infrastructure.in.rest.environment.dto;

import pl.feature.toggle.service.read.domain.EnvironmentView;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnvironmentDto(
        UUID id,
        UUID projectId,
        String name,
        String type,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long revision,
        Boolean consistent
) {

    public static EnvironmentDto from(EnvironmentView view) {
        return new EnvironmentDto(
                view.id().uuid(),
                view.projectId().uuid(),
                view.name().value(),
                view.type(),
                view.status().name(),
                view.createdAt().toLocalDateTime(),
                view.updatedAt().toLocalDateTime(),
                view.revision().value(),
                view.consistent()
        );
    }
}
