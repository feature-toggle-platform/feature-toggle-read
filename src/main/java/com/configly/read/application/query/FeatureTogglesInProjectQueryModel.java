package com.configly.read.application.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeatureTogglesInProjectQueryModel(
        ProjectData projectData
) {

    public record ProjectData(
            UUID projectId,
            String projectName,
            List<EnvironmentData> environments
    ) {
    }

    public record EnvironmentData(
            UUID environmentId,
            String environmentName,
            long revision,
            LocalDateTime updatedAt,
            boolean consistent,
            List<FeatureToggleData> featureToggles
    ) {
    }

    public record FeatureToggleData(
            UUID featureToggleId,
            String name,
            String description,
            String type,
            String value,
            String status,
            LocalDateTime updatedAt,
            boolean consistent
    ) {

    }
}
