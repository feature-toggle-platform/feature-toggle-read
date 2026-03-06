package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInProjectQueryModel;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.Optional;

public interface FeatureToggleQueryRepository {

    Optional<FeatureToggleView> find(FeatureToggleId featureToggleId);

    Optional<FeatureToggleView> findConsistent(FeatureToggleId featureToggleId);

    Optional<FeatureTogglesInEnvironmentQueryModel> findByContext(ProjectId projectId, EnvironmentId environmentId);

    Optional<FeatureTogglesInProjectQueryModel> findByProject(ProjectId projectId);
}
