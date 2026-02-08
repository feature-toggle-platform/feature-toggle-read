package pl.feature.toggle.service.read.application.projection.project;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.ProjectViewConsistency;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.domain.ProjectView;

@AllArgsConstructor
class DefaultProjectViewConsistency implements ProjectViewConsistency {

    private final ConfigurationClient configurationClient;
    private final ProjectProjectionRepository projectionRepository;
    private final ProjectQueryRepository queryRepository;

    @Override
    public ProjectView getTrusted(ProjectId projectId) {
        return queryRepository.findConsistent(projectId)
                .orElseGet(() -> fetchAndSaveProjectView(projectId));
    }

    @Override
    public void rebuild(ProjectId projectId) {
        fetchAndSaveProjectView(projectId);
    }

    private ProjectView fetchAndSaveProjectView(ProjectId projectId) {
        var projectView = configurationClient.fetchProject(projectId);
        projectionRepository.upsert(projectView);
        return projectView;
    }
}
