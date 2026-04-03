package com.configly.read.application.port.in;

import com.configly.model.project.ProjectId;
import com.configly.read.domain.ProjectView;

public interface ProjectViewConsistency {

    ProjectView getTrusted(ProjectId projectId);

    void rebuild(ProjectId projectId);
}
