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
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.domain.ProjectView;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.raw.FeatureToggleRawValue;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Mapper {

    static ProjectView toView(ProjectViewRecord record) {
        return new ProjectView(
                ProjectId.create(record.getId()),
                ProjectName.create(record.getName()),
                ProjectDescription.create(record.getDescription()),
                ProjectStatus.valueOf(record.getStatus()),
                CreatedAt.of(record.getCreatedAt()),
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
                Revision.from(record.getRevision()),
                record.getConsistent()
        );
    }


    public static FeatureToggleView toView(FeatureToggleViewRecord record) {
        return new FeatureToggleView(
                FeatureToggleId.create(record.getId()),
                ProjectId.create(record.getProjectId()),
                EnvironmentId.create(record.getEnvironmentId()),
                FeatureToggleName.create(record.getName()),
                FeatureToggleDescription.create(record.getDescription()),
                FeatureToggleValueBuilder.from(FeatureToggleRawValue.of(record.getCurrentValue()), record.getType()),
                FeatureToggleStatus.valueOf(record.getStatus()),
                Revision.from(record.getRevision()),
                CreatedAt.of(record.getCreatedAt()),
                UpdatedAt.of(record.getUpdatedAt()),
                record.getConsistent()
        );
    }
}
