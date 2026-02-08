package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.util.Optional;

import static pl.feature.ftaas.jooq.tables.ProjectView.PROJECT_VIEW;

@AllArgsConstructor
class ProjectQueryJooqRepository implements ProjectQueryRepository {

    private DSLContext dslContext;

    @Override
    public Optional<ProjectView> find(ProjectId projectId) {
        return dslContext.selectFrom(PROJECT_VIEW)
                .where(PROJECT_VIEW.ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toView);
    }

    @Override
    public Optional<ProjectView> findConsistent(ProjectId projectId) {
        return dslContext.selectFrom(PROJECT_VIEW)
                .where(PROJECT_VIEW.ID.eq(projectId.uuid()))
                .and(PROJECT_VIEW.CONSISTENT.eq(true))
                .fetchOptional()
                .map(Mapper::toView);
    }
}
