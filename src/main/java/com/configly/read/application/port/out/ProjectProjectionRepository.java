package com.configly.read.application.port.out;

import com.configly.model.project.ProjectId;
import com.configly.read.domain.ProjectView;

public interface ProjectProjectionRepository {

    void insert(ProjectView view);

    void updateStatus(ProjectView view);

    void updateBasicFields(ProjectView view);

    void upsert(ProjectView view);

    boolean markInconsistentIfNotMarked(ProjectId projectId);
}
