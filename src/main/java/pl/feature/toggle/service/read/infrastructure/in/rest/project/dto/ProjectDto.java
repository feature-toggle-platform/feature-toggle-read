package pl.feature.toggle.service.read.infrastructure.in.rest.project.dto;

import pl.feature.toggle.service.read.domain.ProjectView;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String name,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long revision,
        Boolean consistent
) {

    public static ProjectDto from(ProjectView view) {
        return new ProjectDto(
                view.id().uuid(),
                view.name().value(),
                view.description().value(),
                view.status().name(),
                view.createdAt().toLocalDateTime(),
                view.updatedAt().toLocalDateTime(),
                view.revision().value(),
                view.consistent()
        );
    }

}
