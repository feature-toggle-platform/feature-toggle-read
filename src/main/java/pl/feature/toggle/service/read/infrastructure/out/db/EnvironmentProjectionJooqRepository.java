package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;

import static pl.feature.ftaas.jooq.tables.EnvironmentView.ENVIRONMENT_VIEW;

@AllArgsConstructor
class EnvironmentProjectionJooqRepository implements EnvironmentProjectionRepository {

    private final DSLContext dslContext;

    @Override
    public void insert(EnvironmentView view) {
        dslContext.insertInto(ENVIRONMENT_VIEW)
                .set(ENVIRONMENT_VIEW.ID, view.id().uuid())
                .set(ENVIRONMENT_VIEW.PROJECT_ID, view.projectId().uuid())
                .set(ENVIRONMENT_VIEW.NAME, view.name().value())
                .set(ENVIRONMENT_VIEW.TYPE, view.type())
                .set(ENVIRONMENT_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(ENVIRONMENT_VIEW.STATUS, view.status().name())
                .set(ENVIRONMENT_VIEW.REVISION, view.revision().value())
                .set(ENVIRONMENT_VIEW.CONSISTENT, view.consistent())
                .execute();
    }

    @Override
    public void updateStatus(EnvironmentView view) {
        dslContext.update(ENVIRONMENT_VIEW)
                .set(ENVIRONMENT_VIEW.STATUS, view.status().name())
                .set(ENVIRONMENT_VIEW.REVISION, view.revision().value())
                .set(ENVIRONMENT_VIEW.CONSISTENT, view.consistent())
                .where(ENVIRONMENT_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void updateName(EnvironmentView view) {
        dslContext.update(ENVIRONMENT_VIEW)
                .set(ENVIRONMENT_VIEW.NAME, view.name().value())
                .set(ENVIRONMENT_VIEW.REVISION, view.revision().value())
                .set(ENVIRONMENT_VIEW.CONSISTENT, view.consistent())
                .where(ENVIRONMENT_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void updateType(EnvironmentView view) {
        dslContext.update(ENVIRONMENT_VIEW)
                .set(ENVIRONMENT_VIEW.TYPE, view.type())
                .set(ENVIRONMENT_VIEW.REVISION, view.revision().value())
                .set(ENVIRONMENT_VIEW.CONSISTENT, view.consistent())
                .where(ENVIRONMENT_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void upsert(EnvironmentView view) {
        dslContext.insertInto(ENVIRONMENT_VIEW)
                .set(ENVIRONMENT_VIEW.ID, view.id().uuid())
                .set(ENVIRONMENT_VIEW.PROJECT_ID, view.projectId().uuid())
                .set(ENVIRONMENT_VIEW.NAME, view.name().value())
                .set(ENVIRONMENT_VIEW.TYPE, view.type())
                .set(ENVIRONMENT_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(ENVIRONMENT_VIEW.STATUS, view.status().name())
                .set(ENVIRONMENT_VIEW.REVISION, view.revision().value())
                .set(ENVIRONMENT_VIEW.CONSISTENT, true)
                .onConflict(ENVIRONMENT_VIEW.ID)
                .doUpdate()
                .set(ENVIRONMENT_VIEW.ID, view.id().uuid())
                .set(ENVIRONMENT_VIEW.PROJECT_ID, view.projectId().uuid())
                .set(ENVIRONMENT_VIEW.NAME, view.name().value())
                .set(ENVIRONMENT_VIEW.TYPE, view.type())
                .set(ENVIRONMENT_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(ENVIRONMENT_VIEW.STATUS, view.status().name())
                .set(ENVIRONMENT_VIEW.REVISION, view.revision().value())
                .set(ENVIRONMENT_VIEW.CONSISTENT, true)
                .execute();
    }

    @Override
    public boolean markInconsistentIfNotMarked(EnvironmentId environmentId) {
        var rows = dslContext.update(ENVIRONMENT_VIEW)
                .set(ENVIRONMENT_VIEW.CONSISTENT, false)
                .where(ENVIRONMENT_VIEW.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_VIEW.CONSISTENT.eq(true))
                .execute();
        return rows == 1;
    }
}
