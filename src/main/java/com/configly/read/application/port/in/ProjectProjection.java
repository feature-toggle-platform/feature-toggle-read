package com.configly.read.application.port.in;

import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.event.project.ProjectStatusChanged;
import com.configly.contracts.event.project.ProjectUpdated;

public interface ProjectProjection {

    void handle(ProjectCreated event);

    void handle(ProjectUpdated event);

    void handle(ProjectStatusChanged event);
}
