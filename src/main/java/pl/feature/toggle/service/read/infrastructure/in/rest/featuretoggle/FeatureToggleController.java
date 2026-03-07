package pl.feature.toggle.service.read.infrastructure.in.rest.featuretoggle;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.infrastructure.in.rest.featuretoggle.dto.FeatureToggleForEnvironmentView;
import pl.feature.toggle.service.read.infrastructure.in.rest.featuretoggle.dto.FeatureToggleForProjectView;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/read")
class FeatureToggleController {

    private final FeatureToggleQueryRepository featureToggleQueryRepository;

    @GetMapping("/projects/{projectId}/feature-toggles")
    FeatureToggleForProjectView findForProject(@PathVariable String projectId) {
        return featureToggleQueryRepository.findByProject(ProjectId.create(projectId))
                .map(FeatureToggleForProjectView::from)
                .orElse(null);
    }

    @GetMapping("/projects/{projectId}/environments/{environmentId}/feature-toggles")
    FeatureToggleForEnvironmentView findForEnvironment(
            @PathVariable String projectId,
            @PathVariable String environmentId
    ) {
        return featureToggleQueryRepository.findByEnvironment(ProjectId.create(projectId), EnvironmentId.create(environmentId))
                .map(FeatureToggleForEnvironmentView::from)
                .orElse(null);
    }
}
