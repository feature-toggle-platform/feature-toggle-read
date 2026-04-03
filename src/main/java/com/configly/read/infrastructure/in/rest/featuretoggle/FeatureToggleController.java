package com.configly.read.infrastructure.in.rest.featuretoggle;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.infrastructure.in.rest.featuretoggle.dto.FeatureToggleForEnvironmentDto;
import com.configly.read.infrastructure.in.rest.featuretoggle.dto.FeatureToggleForProjectDto;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/read")
class FeatureToggleController {

    private final FeatureToggleQueryRepository featureToggleQueryRepository;

    @GetMapping("/projects/{projectId}/feature-toggles")
    FeatureToggleForProjectDto findForProject(@PathVariable String projectId) {
        return featureToggleQueryRepository.findByProject(ProjectId.create(projectId))
                .map(FeatureToggleForProjectDto::from)
                .orElse(null);
    }

    @GetMapping("/projects/{projectId}/environments/{environmentId}/feature-toggles")
    FeatureToggleForEnvironmentDto findForEnvironment(
            @PathVariable String projectId,
            @PathVariable String environmentId
    ) {
        return featureToggleQueryRepository.findByEnvironment(ProjectId.create(projectId), EnvironmentId.create(environmentId))
                .map(FeatureToggleForEnvironmentDto::from)
                .orElse(null);
    }
}
