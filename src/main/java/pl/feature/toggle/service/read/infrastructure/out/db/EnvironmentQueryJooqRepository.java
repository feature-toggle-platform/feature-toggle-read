package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;

import java.util.Optional;

import static pl.feature.ftaas.jooq.tables.EnvironmentView.ENVIRONMENT_VIEW;

@AllArgsConstructor
class EnvironmentQueryJooqRepository implements EnvironmentQueryRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<EnvironmentView> find(ProjectId projectId, EnvironmentId environmentId) {
        return dslContext.selectFrom(ENVIRONMENT_VIEW)
                .where(ENVIRONMENT_VIEW.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_VIEW.PROJECT_ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toView);
    }

    @Override
    public Optional<EnvironmentView> findConsistent(ProjectId projectId, EnvironmentId environmentId) {
        return dslContext.selectFrom(ENVIRONMENT_VIEW)
                .where(ENVIRONMENT_VIEW.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_VIEW.CONSISTENT.eq(true))
                .and(ENVIRONMENT_VIEW.PROJECT_ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toView);
    }
}
