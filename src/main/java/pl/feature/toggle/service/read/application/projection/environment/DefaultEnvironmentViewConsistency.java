package pl.feature.toggle.service.read.application.projection.environment;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.EnvironmentViewConsistency;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;

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
