package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;

public interface ProjectProjection {

    void handle(ProjectCreated event);

    void handle(ProjectUpdated event);

    void handle(ProjectStatusChanged event);
}
