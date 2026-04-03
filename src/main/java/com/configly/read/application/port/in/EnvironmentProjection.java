package com.configly.read.application.port.in;

import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;
import com.configly.contracts.event.environment.EnvironmentTypeChanged;
import com.configly.contracts.event.environment.EnvironmentUpdated;

public interface EnvironmentProjection {

    void handle(EnvironmentCreated event);

    void handle(EnvironmentUpdated event);

    void handle(EnvironmentStatusChanged event);

    void handle(EnvironmentTypeChanged event);
}
