package pl.feature.toggle.service.read.application.projection.project;

import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;

class ProjectProjectionHandler implements ProjectProjection {

    @Override
    public void handle(ProjectCreated event) {

    }

    @Override
    public void handle(ProjectUpdated event) {

    }

    @Override
    public void handle(ProjectStatusChanged event) {

    }
}
