package pl.feature.toggle.service.read.infrastructure.in.rest.project;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.infrastructure.in.rest.project.dto.ProjectDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/read")
class ProjectController {

    private final ProjectQueryRepository queryRepository;

    @GetMapping("/projects")
    List<ProjectDto> findProjects() {
        return queryRepository.findAll().stream()
                .map(ProjectDto::from)
                .toList();
    }


}
