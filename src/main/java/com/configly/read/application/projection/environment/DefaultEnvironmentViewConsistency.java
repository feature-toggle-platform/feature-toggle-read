package com.configly.read.application.projection.environment;

import lombok.AllArgsConstructor;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.in.EnvironmentViewConsistency;
import com.configly.read.application.port.out.ConfigurationClient;
import com.configly.read.application.port.out.EnvironmentProjectionRepository;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.domain.EnvironmentView;

@AllArgsConstructor
class DefaultEnvironmentViewConsistency implements EnvironmentViewConsistency {

    private final ConfigurationClient configurationClient;
    private final EnvironmentProjectionRepository projectionRepository;
    private final EnvironmentQueryRepository queryRepository;

    @Override
    public EnvironmentView getTrusted(ProjectId projectId, EnvironmentId environmentId) {
        return queryRepository.findConsistent(projectId, environmentId)
                .orElseGet(() -> fetchAndSaveEnvironmentView(projectId, environmentId));
    }

    @Override
    public void rebuild(ProjectId projectId, EnvironmentId environmentId) {
        fetchAndSaveEnvironmentView(projectId, environmentId);
    }

    private EnvironmentView fetchAndSaveEnvironmentView(ProjectId projectId, EnvironmentId environmentId) {
        var environmentView = configurationClient.fetchEnvironment(projectId, environmentId);
        projectionRepository.upsert(environmentView);
        return environmentView;
    }
}
