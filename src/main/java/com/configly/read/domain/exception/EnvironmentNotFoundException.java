package com.configly.read.domain.exception;

import com.configly.model.environment.EnvironmentId;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(EnvironmentId environmentId) {
        super("Environment not found: " + environmentId);
    }
}
