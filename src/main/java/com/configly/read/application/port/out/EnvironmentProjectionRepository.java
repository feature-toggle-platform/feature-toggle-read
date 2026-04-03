package com.configly.read.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.read.domain.EnvironmentView;

public interface EnvironmentProjectionRepository {

    void insert(EnvironmentView environmentView);

    void updateStatus(EnvironmentView environmentView);

    void updateName(EnvironmentView environmentView);

    void updateType(EnvironmentView environmentView);

    void upsert(EnvironmentView environmentView);

    boolean markInconsistentIfNotMarked(EnvironmentId environmentId);
}
