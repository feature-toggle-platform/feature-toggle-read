package com.configly.read.application.port.in;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.domain.EnvironmentView;

public interface EnvironmentViewConsistency {

    EnvironmentView getTrusted(ProjectId projectId, EnvironmentId environmentId);

    void rebuild(ProjectId projectId, EnvironmentId environmentId);
}
