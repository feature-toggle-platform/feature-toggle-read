package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.ProjectView;

public interface ProjectProjectionRepository {

    void insert(ProjectView view);

    void updateStatus(ProjectView view);

    void updateName(ProjectView view);

    void upsert(ProjectView view);

    boolean markInconsistentIfNotMarked(ProjectId projectId);
}
