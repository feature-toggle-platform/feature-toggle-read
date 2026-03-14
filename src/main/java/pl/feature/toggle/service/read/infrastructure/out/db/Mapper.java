package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.ftaas.jooq.tables.records.EnvironmentViewRecord;
import pl.feature.ftaas.jooq.tables.records.FeatureToggleViewRecord;
import pl.feature.ftaas.jooq.tables.records.ProjectViewRecord;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentName;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectDescription;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectName;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInProjectQueryModel;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.domain.ProjectView;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.feature.ftaas.jooq.Tables.ENVIRONMENT_VIEW;
import static pl.feature.ftaas.jooq.Tables.PROJECT_VIEW;
import static pl.feature.ftaas.jooq.tables.FeatureToggleView.FEATURE_TOGGLE_VIEW;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Mapper {

    static ProjectView toView(ProjectViewRecord record) {
        return new ProjectView(
                ProjectId.create(record.getId()),
                ProjectName.create(record.getName()),
                ProjectDescription.create(record.getDescription()),
                ProjectStatus.valueOf(record.getStatus()),
                CreatedAt.of(record.getCreatedAt()),
                UpdatedAt.of(record.getUpdatedAt()),
                Revision.from(record.getRevision()),
                record.getConsistent()
        );
    }

    static EnvironmentView toView(EnvironmentViewRecord record) {
        return new EnvironmentView(
                EnvironmentId.create(record.getId()),
                ProjectId.create(record.getProjectId()),
                EnvironmentName.create(record.getName()),
                record.getType(),
                EnvironmentStatus.valueOf(record.getStatus()),
                CreatedAt.of(record.getCreatedAt()),
                UpdatedAt.of(record.getUpdatedAt()),
                Revision.from(record.getRevision()),
                record.getConsistent()
        );
    }


    static FeatureToggleView toView(FeatureToggleViewRecord record) {
        return new FeatureToggleView(
                FeatureToggleId.create(record.getId()),
                EnvironmentId.create(record.getEnvironmentId()),
                FeatureToggleName.create(record.getName()),
                FeatureToggleDescription.create(record.getDescription()),
                FeatureToggleValueBuilder.from(FeatureToggleValueSnapshot.of(record.getCurrentValue()), record.getType()),
                FeatureToggleStatus.valueOf(record.getStatus()),
                Revision.from(record.getRevision()),
                CreatedAt.of(record.getCreatedAt()),
                UpdatedAt.of(record.getUpdatedAt()),
                record.getConsistent()
        );
    }

    static FeatureToggleQueryRow toFeatureToggleQueryRow(Record record) {
        return new FeatureToggleQueryRow(
                record.get(PROJECT_VIEW.ID),
                record.get(PROJECT_VIEW.NAME),
                record.get(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID),
                record.get(ENVIRONMENT_VIEW.NAME),
                record.get(ENVIRONMENT_VIEW.REVISION),
                record.get(ENVIRONMENT_VIEW.UPDATED_AT),
                record.get(ENVIRONMENT_VIEW.CONSISTENT),
                record.get(FEATURE_TOGGLE_VIEW.ID),
                record.get(FEATURE_TOGGLE_VIEW.NAME),
                record.get(FEATURE_TOGGLE_VIEW.DESCRIPTION),
                record.get(FEATURE_TOGGLE_VIEW.TYPE),
                record.get(FEATURE_TOGGLE_VIEW.CURRENT_VALUE),
                record.get(FEATURE_TOGGLE_VIEW.STATUS),
                record.get(FEATURE_TOGGLE_VIEW.UPDATED_AT),
                record.get(FEATURE_TOGGLE_VIEW.CONSISTENT)
        );
    }

    static FeatureTogglesInProjectQueryModel toProjectQueryModel(List<FeatureToggleQueryRow> rows) {
        var first = rows.getFirst();

        var environments = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> new EnvironmentKey(
                                row.environmentId(),
                                row.environmentName(),
                                row.environmentRevision(),
                                row.environmentUpdatedAt(),
                                row.environmentConsistent()
                        ),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> new FeatureTogglesInProjectQueryModel.EnvironmentData(
                        entry.getKey().environmentId(),
                        entry.getKey().environmentName(),
                        entry.getKey().revision(),
                        entry.getKey().updatedAt(),
                        entry.getKey().consistent(),
                        entry.getValue().stream()
                                .map(Mapper::toProjectFeatureToggleData)
                                .toList()
                ))
                .toList();

        return new FeatureTogglesInProjectQueryModel(
                new FeatureTogglesInProjectQueryModel.ProjectData(
                        first.projectId(),
                        first.projectName(),
                        environments
                )
        );
    }

    static FeatureTogglesInEnvironmentQueryModel toEnvironmentQueryModel(List<FeatureToggleQueryRow> rows) {
        var first = rows.getFirst();

        var featureToggles = rows.stream()
                .map(Mapper::toEnvironmentFeatureToggleData)
                .toList();

        return new FeatureTogglesInEnvironmentQueryModel(
                new FeatureTogglesInEnvironmentQueryModel.ProjectData(
                        first.projectId(),
                        first.projectName()
                ),
                new FeatureTogglesInEnvironmentQueryModel.EnvironmentData(
                        first.environmentId(),
                        first.environmentName(),
                        first.environmentRevision(),
                        first.environmentUpdatedAt(),
                        first.environmentConsistent(),
                        featureToggles
                )
        );
    }

    private static FeatureTogglesInProjectQueryModel.FeatureToggleData toProjectFeatureToggleData(FeatureToggleQueryRow row) {
        return new FeatureTogglesInProjectQueryModel.FeatureToggleData(
                row.featureToggleId(),
                row.featureToggleName(),
                row.featureToggleDescription(),
                row.featureToggleType(),
                row.featureToggleValue(),
                row.featureToggleStatus(),
                row.featureToggleUpdatedAt(),
                row.featureToggleConsistent()
        );
    }

    private static FeatureTogglesInEnvironmentQueryModel.FeatureToggleData toEnvironmentFeatureToggleData(FeatureToggleQueryRow row) {
        return new FeatureTogglesInEnvironmentQueryModel.FeatureToggleData(
                row.featureToggleId(),
                row.featureToggleName(),
                row.featureToggleDescription(),
                row.featureToggleType(),
                row.featureToggleValue(),
                row.featureToggleStatus(),
                row.featureToggleUpdatedAt(),
                row.featureToggleConsistent()
        );
    }

    private record EnvironmentKey(
            UUID environmentId,
            String environmentName,
            long revision,
            LocalDateTime updatedAt,
            boolean consistent
    ) {
    }
}
