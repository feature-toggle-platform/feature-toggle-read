package com.configly.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.domain.EnvironmentView;

import java.util.List;
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

    @Override
    public List<EnvironmentView> findByProjectId(ProjectId projectId) {
        return dslContext.selectFrom(ENVIRONMENT_VIEW)
                .where(ENVIRONMENT_VIEW.CONSISTENT.eq(true))
                .and(ENVIRONMENT_VIEW.PROJECT_ID.eq(projectId.uuid()))
                .fetch()
                .map(Mapper::toView);
    }
}
