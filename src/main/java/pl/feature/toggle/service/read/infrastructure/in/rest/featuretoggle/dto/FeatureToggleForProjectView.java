package pl.feature.toggle.service.read.infrastructure.in.rest.featuretoggle.dto;

import pl.feature.toggle.service.read.application.query.FeatureTogglesInProjectQueryModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeatureToggleForProjectView(
        ProjectData projects
) {

    public static FeatureToggleForProjectView from(FeatureTogglesInProjectQueryModel queryModel) {
        return new FeatureToggleForProjectView(
                new ProjectData(
                        queryModel.projectData().projectId(),
                        queryModel.projectData().projectName(),
                        queryModel.projectData().environments().stream()
                                .map(environmentData -> new EnvironmentData(
                                        environmentData.environmentId(),
                                        environmentData.environmentName(),
                                        environmentData.revision(),
                                        environmentData.updatedAt(),
                                        environmentData.consistent(),
                                        environmentData.featureToggles().stream()
                                                .map(featureToggleData -> new FeatureToggleData(
                                                        featureToggleData.featureToggleId(),
                                                        featureToggleData.name(),
                                                        featureToggleData.description(),
                                                        featureToggleData.type(),
                                                        featureToggleData.value(),
                                                        featureToggleData.status(),
                                                        featureToggleData.updatedAt(),
                                                        featureToggleData.consistent()
                                                ))
                                                .toList()
                                ))
                                .toList()
                )
        );
    }

    record ProjectData(
            UUID projectId,
            String projectName,
            List<EnvironmentData> environments
    ) {
    }

    record EnvironmentData(
            UUID environmentId,
            String environmentName,
            long revision,
            LocalDateTime updatedAt,
            boolean consistent,
            List<FeatureToggleData> featureToggles
    ) {
    }

    record FeatureToggleData(
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
