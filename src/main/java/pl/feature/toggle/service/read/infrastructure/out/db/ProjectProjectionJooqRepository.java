package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.domain.ProjectView;

import static pl.feature.ftaas.jooq.tables.ProjectView.PROJECT_VIEW;

@AllArgsConstructor
class ProjectProjectionJooqRepository implements ProjectProjectionRepository {

    private final DSLContext dslContext;

    @Override
    public void insert(ProjectView view) {
        dslContext.insertInto(PROJECT_VIEW)
                .set(PROJECT_VIEW.ID, view.id().uuid())
                .set(PROJECT_VIEW.NAME, view.name().value())
                .set(PROJECT_VIEW.DESCRIPTION, view.description().value())
                .set(PROJECT_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(PROJECT_VIEW.STATUS, view.status().name())
                .set(PROJECT_VIEW.REVISION, view.revision().value())
                .set(PROJECT_VIEW.CONSISTENT, view.consistent())
                .execute();
    }

    @Override
    public void updateStatus(ProjectView view) {
        dslContext.update(PROJECT_VIEW)
                .set(PROJECT_VIEW.STATUS, view.status().name())
                .set(PROJECT_VIEW.REVISION, view.revision().value())
                .set(PROJECT_VIEW.CONSISTENT, view.consistent())
                .where(PROJECT_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void updateName(ProjectView view) {
        dslContext.update(PROJECT_VIEW)
                .set(PROJECT_VIEW.NAME, view.name().value())
                .set(PROJECT_VIEW.REVISION, view.revision().value())
                .set(PROJECT_VIEW.CONSISTENT, view.consistent())
                .where(PROJECT_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void upsert(ProjectView view) {
        dslContext.insertInto(PROJECT_VIEW)
                .set(PROJECT_VIEW.ID, view.id().uuid())
                .set(PROJECT_VIEW.NAME, view.name().value())
                .set(PROJECT_VIEW.DESCRIPTION, view.description().value())
                .set(PROJECT_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(PROJECT_VIEW.STATUS, view.status().name())
                .set(PROJECT_VIEW.REVISION, view.revision().value())
                .set(PROJECT_VIEW.CONSISTENT, view.consistent())
                .onConflict(PROJECT_VIEW.ID)
                .doUpdate()
                .set(PROJECT_VIEW.NAME, view.name().value())
                .set(PROJECT_VIEW.DESCRIPTION, view.description().value())
                .set(PROJECT_VIEW.STATUS, view.status().name())
                .set(PROJECT_VIEW.REVISION, view.revision().value())
                .set(PROJECT_VIEW.CONSISTENT, view.consistent())
                .execute();
    }

    @Override
    public boolean markInconsistentIfNotMarked(ProjectId projectId) {
        var rows = dslContext.update(PROJECT_VIEW)
                .set(PROJECT_VIEW.CONSISTENT, false)
                .where(PROJECT_VIEW.ID.eq(projectId.uuid()))
                .and(PROJECT_VIEW.CONSISTENT.eq(true))
                .execute();
        return rows == 1;
    }
}
