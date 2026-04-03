package com.configly.read.infrastructure.in.rest.environment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.infrastructure.in.rest.environment.dto.EnvironmentDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/read")
class EnvironmentController {

    private final EnvironmentQueryRepository queryRepository;


    @GetMapping("/projects/{projectId}/environments")
    List<EnvironmentDto> findByProjectId(@PathVariable String projectId) {
        return queryRepository.findByProjectId(ProjectId.create(projectId)).stream()
                .map(EnvironmentDto::from)
                .toList();
    }
}
