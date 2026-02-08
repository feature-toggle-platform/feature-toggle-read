package pl.feature.toggle.service.read.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.read.domain.EnvironmentView;

public interface EnvironmentProjectionRepository {

    void insert(EnvironmentView environmentView);

    void updateStatus(EnvironmentView environmentView);

    void updateName(EnvironmentView environmentView);

    void updateType(EnvironmentView environmentView);

    void upsert(EnvironmentView environmentView);

    boolean markInconsistentIfNotMarked(EnvironmentId environmentId);
}
