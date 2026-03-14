package pl.feature.toggle.service.read.application.port.out.sse;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.UUID;

public record SseScope(
        ScopeType type,
        ProjectId projectId,
        EnvironmentId environmentId
) {

    public static SseScope environmentScope(UUID environmentId) {
        return new SseScope(
                ScopeType.ENVIRONMENT,
                null,
                EnvironmentId.create(environmentId)
        );
    }

    public static SseScope environmentScope(EnvironmentId environmentId) {
        return new SseScope(
                ScopeType.ENVIRONMENT,
                null,
                environmentId
        );
    }

    public static SseScope resolveScope(ProjectId projectId, EnvironmentId environmentId) {
        if (projectId != null && environmentId != null) {
            return environmentScope(environmentId);
        }
        if (projectId == null) {
            return environmentScope(environmentId);
        }
        return projectScope(projectId);
    }

    public static SseScope projectScope(UUID projectId) {
        return new SseScope(
                ScopeType.PROJECT,
                ProjectId.create(projectId),
                null
        );
    }

    public static SseScope projectScope(ProjectId projectId) {
        return new SseScope(
                ScopeType.PROJECT,
                projectId,
                null
        );
    }

    public static SseScope allScope() {
        return new SseScope(
                ScopeType.ALL,
                null,
                null
        );
    }

    public boolean isProjectScope() {
        return type == ScopeType.PROJECT;
    }

    public boolean isEnvironmentScope() {
        return type == ScopeType.ENVIRONMENT;
    }

    public boolean isAllScope() {
        return type == ScopeType.ALL;
    }

    public enum ScopeType {
        ALL,
        PROJECT,
        ENVIRONMENT
    }
}
