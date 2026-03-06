package pl.feature.toggle.service.read.infrastructure.out.db;

import java.time.LocalDateTime;
import java.util.UUID;

record FeatureToggleQueryRow(
        UUID projectId,
        String projectName,
        UUID environmentId,
        String environmentName,
        long environmentRevision,
        LocalDateTime environmentUpdatedAt,
        boolean environmentConsistent,
        UUID featureToggleId,
        String featureToggleName,
        String featureToggleDescription,
        String featureToggleType,
        String featureToggleValue,
        String featureToggleStatus,
        LocalDateTime featureToggleUpdatedAt,
        boolean featureToggleConsistent
) {
}
