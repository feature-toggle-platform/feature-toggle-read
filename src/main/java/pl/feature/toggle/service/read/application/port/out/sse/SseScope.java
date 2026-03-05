package pl.feature.toggle.service.read.application.port.out.sse;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.UUID;

public record SseScope(
        ProjectId projectId,
        EnvironmentId environmentId
) {

    public static SseScope environmentScope(ProjectId projectId, EnvironmentId environmentId) {
        return new SseScope(projectId, environmentId);
    }

    public static SseScope environmentScope(UUID projectId, UUID environmentId) {
        return new SseScope(ProjectId.create(projectId), EnvironmentId.create(environmentId));
    }

    public static SseScope projectScope(UUID projectId) {
        return new SseScope(ProjectId.create(projectId), null);
    }

    public static SseScope allScope() {
        return new SseScope(null, null);
    }

    public boolean isProjectScope() {
        return environmentId == null;
    }

    public boolean isEnvironmentScope() {
        return projectId != null && environmentId != null;
    }

    public boolean isAllScope() {
        return projectId == null && environmentId == null;
    }
}
