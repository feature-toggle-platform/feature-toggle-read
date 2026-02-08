package pl.feature.toggle.service.read.application.projection.environment;

import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentTypeChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentUpdated;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;

class EnvironmentProjectionHandler implements EnvironmentProjection {


    @Override
    public void handle(EnvironmentCreated event) {

    }

    @Override
    public void handle(EnvironmentUpdated event) {

    }

    @Override
    public void handle(EnvironmentStatusChanged event) {

    }

    @Override
    public void handle(EnvironmentTypeChanged event) {

    }
}
