package com.configly.read.application.projection.project;

import lombok.AllArgsConstructor;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.in.ProjectViewConsistency;
import com.configly.read.application.port.out.ConfigurationClient;
import com.configly.read.application.port.out.ProjectProjectionRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;
import com.configly.read.domain.ProjectView;

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
