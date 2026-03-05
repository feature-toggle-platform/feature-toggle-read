package pl.feature.toggle.service.read.infrastructure.in.rest.sse;

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

    private final FeatureToggleSseUseCase sseUseCase;

    @GetMapping(
            value = "/projects/{projectId}/environments/{environmentId}/feature-toggles/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter establishSSEConnection(@PathVariable String projectId, @PathVariable String environmentId) {
        var sseConnection = SpringSseConnection.create();
        sseUseCase.establish(ProjectId.create(projectId), EnvironmentId.create(environmentId), sseConnection);
        return sseConnection.sseEmitter();
    }

}
