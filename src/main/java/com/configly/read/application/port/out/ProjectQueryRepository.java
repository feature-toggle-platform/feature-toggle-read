package com.configly.read.application.port.out;

import com.configly.model.project.ProjectId;
import com.configly.read.domain.ProjectView;

import java.util.List;
import java.util.Optional;

public interface ProjectQueryRepository {

    Optional<ProjectView> find(ProjectId projectId);

    Optional<ProjectView> findConsistent(ProjectId projectId);

    List<ProjectView> findAll();
}
