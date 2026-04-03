package com.configly.read.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import com.configly.read.application.query.FeatureTogglesInProjectQueryModel;
import com.configly.read.domain.FeatureToggleView;

import java.util.List;
import java.util.Optional;

public interface FeatureToggleQueryRepository {

    Optional<FeatureToggleView> find(FeatureToggleId featureToggleId);

    Optional<FeatureToggleView> findConsistent(FeatureToggleId featureToggleId);

    List<FeatureToggleView> find(ProjectId projectId, EnvironmentId environmentId);

    Optional<FeatureTogglesInEnvironmentQueryModel> findByEnvironment(ProjectId projectId, EnvironmentId environmentId);

    Optional<FeatureTogglesInProjectQueryModel> findByProject(ProjectId projectId);
}
