package com.configly.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import com.configly.read.application.query.FeatureTogglesInProjectQueryModel;
import com.configly.read.domain.FeatureToggleView;

import java.util.List;
import java.util.Optional;

import static com.configly.jooq.tables.EnvironmentView.ENVIRONMENT_VIEW;
import static com.configly.jooq.tables.FeatureToggleView.FEATURE_TOGGLE_VIEW;
import static com.configly.jooq.tables.ProjectView.PROJECT_VIEW;


@AllArgsConstructor
class FeatureToggleQueryJooqRepository implements FeatureToggleQueryRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<FeatureToggleView> find(FeatureToggleId featureToggleId) {
        return dslContext.selectFrom(FEATURE_TOGGLE_VIEW)
                .where(FEATURE_TOGGLE_VIEW.ID.eq(featureToggleId.uuid()))
                .fetchOptional()
                .map(Mapper::toView);
    }

    @Override
    public Optional<FeatureToggleView> findConsistent(FeatureToggleId featureToggleId) {
        return dslContext.selectFrom(FEATURE_TOGGLE_VIEW)
                .where(FEATURE_TOGGLE_VIEW.ID.eq(featureToggleId.uuid()))
                .and(FEATURE_TOGGLE_VIEW.CONSISTENT.eq(true))
                .fetchOptional()
                .map(Mapper::toView);
    }

    @Override
    public List<FeatureToggleView> find(ProjectId projectId, EnvironmentId environmentId) {
        return dslContext.select(FEATURE_TOGGLE_VIEW.fields())
                .from(FEATURE_TOGGLE_VIEW)
                .join(ENVIRONMENT_VIEW)
                .on(ENVIRONMENT_VIEW.ID.eq(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID))
                .where(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_VIEW.PROJECT_ID.eq(projectId.uuid()))
                .fetchInto(FEATURE_TOGGLE_VIEW)
                .map(Mapper::toView);
    }

    @Override
    public Optional<FeatureTogglesInProjectQueryModel> findByProject(ProjectId projectId) {
        var rows = dslContext
                .select(
                        PROJECT_VIEW.ID,
                        PROJECT_VIEW.NAME,
                        FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID,
                        ENVIRONMENT_VIEW.NAME,
                        ENVIRONMENT_VIEW.REVISION,
                        ENVIRONMENT_VIEW.UPDATED_AT,
                        ENVIRONMENT_VIEW.CONSISTENT,
                        FEATURE_TOGGLE_VIEW.ID,
                        FEATURE_TOGGLE_VIEW.NAME,
                        FEATURE_TOGGLE_VIEW.DESCRIPTION,
                        FEATURE_TOGGLE_VIEW.TYPE,
                        FEATURE_TOGGLE_VIEW.CURRENT_VALUE,
                        FEATURE_TOGGLE_VIEW.STATUS,
                        FEATURE_TOGGLE_VIEW.UPDATED_AT,
                        FEATURE_TOGGLE_VIEW.CONSISTENT
                )
                .from(FEATURE_TOGGLE_VIEW)
                .join(ENVIRONMENT_VIEW)
                .on(ENVIRONMENT_VIEW.ID.eq(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID))
                .join(PROJECT_VIEW)
                .on(PROJECT_VIEW.ID.eq(ENVIRONMENT_VIEW.PROJECT_ID))
                .where(ENVIRONMENT_VIEW.PROJECT_ID.eq(projectId.uuid()))
                .orderBy(ENVIRONMENT_VIEW.NAME.asc(), FEATURE_TOGGLE_VIEW.NAME.asc())
                .fetch(Mapper::toFeatureToggleQueryRow);

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(Mapper.toProjectQueryModel(rows));
    }

    @Override
    public Optional<FeatureTogglesInEnvironmentQueryModel> findByEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        var rows = dslContext
                .select(
                        PROJECT_VIEW.ID,
                        PROJECT_VIEW.NAME,
                        FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID,
                        ENVIRONMENT_VIEW.NAME,
                        ENVIRONMENT_VIEW.REVISION,
                        ENVIRONMENT_VIEW.UPDATED_AT,
                        ENVIRONMENT_VIEW.CONSISTENT,
                        FEATURE_TOGGLE_VIEW.ID,
                        FEATURE_TOGGLE_VIEW.NAME,
                        FEATURE_TOGGLE_VIEW.DESCRIPTION,
                        FEATURE_TOGGLE_VIEW.TYPE,
                        FEATURE_TOGGLE_VIEW.CURRENT_VALUE,
                        FEATURE_TOGGLE_VIEW.STATUS,
                        FEATURE_TOGGLE_VIEW.UPDATED_AT,
                        FEATURE_TOGGLE_VIEW.CONSISTENT
                )
                .from(FEATURE_TOGGLE_VIEW)
                .join(ENVIRONMENT_VIEW)
                .on(ENVIRONMENT_VIEW.ID.eq(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID))
                .join(PROJECT_VIEW)
                .on(PROJECT_VIEW.ID.eq(ENVIRONMENT_VIEW.PROJECT_ID))
                .where(ENVIRONMENT_VIEW.ID.eq(environmentId.uuid()))
                .and(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID.eq(environmentId.uuid()))
                .and(PROJECT_VIEW.ID.eq(projectId.uuid()))
                .orderBy(FEATURE_TOGGLE_VIEW.NAME.asc())
                .fetch(Mapper::toFeatureToggleQueryRow);

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(Mapper.toEnvironmentQueryModel(rows));
    }
}
