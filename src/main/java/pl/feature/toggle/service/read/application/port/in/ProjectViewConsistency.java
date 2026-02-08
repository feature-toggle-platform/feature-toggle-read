package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.ProjectView;

public interface ProjectViewConsistency {

    ProjectView getTrusted(ProjectId projectId);

    void rebuild(ProjectId projectId);
}
