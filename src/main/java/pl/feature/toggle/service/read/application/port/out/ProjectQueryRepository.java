package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.util.Optional;

public interface ProjectQueryRepository {

    Optional<ProjectView> find(ProjectId projectId);

    Optional<ProjectView> findConsistent(ProjectId projectId);
}
