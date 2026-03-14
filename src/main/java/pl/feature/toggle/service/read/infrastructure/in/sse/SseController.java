package pl.feature.toggle.service.read.infrastructure.in.sse;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleSseUseCase;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/sdk")
class SseController {

    private static final Long HEARTBEAT_INTERVAL_IN_S = 15L;

    private final FeatureToggleSseUseCase sseUseCase;

    @GetMapping(
            value = "/projects/{projectId}/environments/{environmentId}/feature-toggles/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter establishSSEConnection(@PathVariable String projectId, @PathVariable(required = false) String environmentId) {
        var sseConnection = SpringSseConnection.create(HEARTBEAT_INTERVAL_IN_S);
        var environmentIdVO = environmentId == null ? null : EnvironmentId.create(environmentId);
        sseUseCase.establish(ProjectId.create(projectId), environmentIdVO, sseConnection);
        return sseConnection.sseEmitter();
    }

}
