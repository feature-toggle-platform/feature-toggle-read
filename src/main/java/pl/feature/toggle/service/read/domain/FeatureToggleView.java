package pl.feature.toggle.service.read.domain;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.value.FeatureToggleValue;

public record FeatureToggleView(
        FeatureToggleId id,
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        FeatureToggleStatus status,
        Revision revision,
        CreatedAt createdAt,
        UpdatedAt updatedAt,
        boolean consistent
) {


}
