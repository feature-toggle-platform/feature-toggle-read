package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.EnvironmentView;

public interface EnvironmentViewConsistency {

    EnvironmentView getTrusted(ProjectId projectId, EnvironmentId environmentId);

    void rebuild(ProjectId projectId, EnvironmentId environmentId);
}
