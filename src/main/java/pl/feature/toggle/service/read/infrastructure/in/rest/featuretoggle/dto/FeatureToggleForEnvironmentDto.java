package pl.feature.toggle.service.read.infrastructure.in.rest.featuretoggle.dto;

import pl.feature.toggle.service.read.application.query.FeatureTogglesInEnvironmentQueryModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeatureToggleForEnvironmentDto(
        ProjectData project,
        EnvironmentData environment
) {

    public static FeatureToggleForEnvironmentDto from(FeatureTogglesInEnvironmentQueryModel queryModel) {
        return new FeatureToggleForEnvironmentDto(
                new ProjectData(
                        queryModel.projectData().projectId(),
                        queryModel.projectData().projectName()
                ),
                new EnvironmentData(
                        queryModel.environmentData().environmentId(),
                        queryModel.environmentData().environmentName(),
                        queryModel.environmentData().revision(),
                        queryModel.environmentData().updatedAt(),
                        queryModel.environmentData().consistent(),
                        queryModel.environmentData().featureToggles().stream()
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
                )
        );
    }

    record ProjectData(
            UUID projectId,
            String projectName
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
