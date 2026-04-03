package com.configly.read.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.domain.EnvironmentView;

import java.util.List;
import java.util.Optional;

public interface EnvironmentQueryRepository {

    Optional<EnvironmentView> find(ProjectId projectId, EnvironmentId environmentId);

    Optional<EnvironmentView> findConsistent(ProjectId projectId, EnvironmentId environmentId);

    List<EnvironmentView> findByProjectId(ProjectId projectId);
}
