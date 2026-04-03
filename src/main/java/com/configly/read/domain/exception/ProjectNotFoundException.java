package com.configly.read.domain.exception;

import com.configly.model.project.ProjectId;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId);
    }
}
