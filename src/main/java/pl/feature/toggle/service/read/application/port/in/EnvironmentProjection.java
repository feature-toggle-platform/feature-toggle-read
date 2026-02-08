package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentTypeChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentUpdated;

public interface EnvironmentProjection {

    void handle(EnvironmentCreated event);

    void handle(EnvironmentUpdated event);

    void handle(EnvironmentStatusChanged event);

    void handle(EnvironmentTypeChanged event);
}
