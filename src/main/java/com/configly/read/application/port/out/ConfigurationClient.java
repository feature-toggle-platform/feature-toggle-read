package com.configly.read.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.domain.EnvironmentView;
import com.configly.read.domain.ProjectView;

public interface ConfigurationClient {

    ProjectView fetchProject(ProjectId projectId);

    EnvironmentView fetchEnvironment(ProjectId projectId, EnvironmentId environmentId);

}
