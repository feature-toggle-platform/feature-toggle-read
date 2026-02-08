package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import static pl.feature.ftaas.jooq.tables.FeatureToggleView.FEATURE_TOGGLE_VIEW;

@AllArgsConstructor
class FeatureToggleProjectionJooqRepository implements FeatureToggleProjectionRepository {

    private final DSLContext dslContext;

    @Override
    public void insert(FeatureToggleView view) {
        dslContext.insertInto(FEATURE_TOGGLE_VIEW)
                .set(FEATURE_TOGGLE_VIEW.ID, view.id().uuid())
                .set(FEATURE_TOGGLE_VIEW.STATUS, view.status().name())
                .set(FEATURE_TOGGLE_VIEW.REVISION, view.revision().value())
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, view.consistent())
                .set(FEATURE_TOGGLE_VIEW.PROJECT_ID, view.projectId().uuid())
                .set(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID, view.environmentId().uuid())
                .set(FEATURE_TOGGLE_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(FEATURE_TOGGLE_VIEW.UPDATED_AT, view.updatedAt().toLocalDateTime())
                .set(FEATURE_TOGGLE_VIEW.NAME, view.name().value())
                .set(FEATURE_TOGGLE_VIEW.DESCRIPTION, view.description().value())
                .set(FEATURE_TOGGLE_VIEW.TYPE, view.value().typeName())
                .set(FEATURE_TOGGLE_VIEW.CURRENT_VALUE, view.value().asText())
                .execute();
    }

    @Override
    public void updateStatus(FeatureToggleView view) {
        dslContext.update(FEATURE_TOGGLE_VIEW)
                .set(FEATURE_TOGGLE_VIEW.STATUS, view.status().name())
                .set(FEATURE_TOGGLE_VIEW.REVISION, view.revision().value())
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, view.consistent())
                .where(FEATURE_TOGGLE_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void updateBasicFields(FeatureToggleView view) {
        dslContext.update(FEATURE_TOGGLE_VIEW)
                .set(FEATURE_TOGGLE_VIEW.NAME, view.name().value())
                .set(FEATURE_TOGGLE_VIEW.DESCRIPTION, view.description().value())
                .set(FEATURE_TOGGLE_VIEW.REVISION, view.revision().value())
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, view.consistent())
                .where(FEATURE_TOGGLE_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void updateValue(FeatureToggleView view) {
        dslContext.update(FEATURE_TOGGLE_VIEW)
                .set(FEATURE_TOGGLE_VIEW.CURRENT_VALUE, view.value().asText())
                .set(FEATURE_TOGGLE_VIEW.REVISION, view.revision().value())
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, view.consistent())
                .where(FEATURE_TOGGLE_VIEW.ID.eq(view.id().uuid()))
                .execute();
    }

    @Override
    public void upsert(FeatureToggleView view) {
        dslContext.insertInto(FEATURE_TOGGLE_VIEW)
                .set(FEATURE_TOGGLE_VIEW.ID, view.id().uuid())
                .set(FEATURE_TOGGLE_VIEW.PROJECT_ID, view.projectId().uuid())
                .set(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID, view.environmentId().uuid())
                .set(FEATURE_TOGGLE_VIEW.NAME, view.name().value())
                .set(FEATURE_TOGGLE_VIEW.DESCRIPTION, view.description().value())
                .set(FEATURE_TOGGLE_VIEW.TYPE, view.value().typeName())
                .set(FEATURE_TOGGLE_VIEW.CURRENT_VALUE, view.value().asText())
                .set(FEATURE_TOGGLE_VIEW.STATUS, view.status().name())
                .set(FEATURE_TOGGLE_VIEW.CREATED_AT, view.createdAt().toLocalDateTime())
                .set(FEATURE_TOGGLE_VIEW.UPDATED_AT, view.updatedAt().toLocalDateTime())
                .set(FEATURE_TOGGLE_VIEW.REVISION, view.revision().value())
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, true)
                .onConflict(FEATURE_TOGGLE_VIEW.ID)
                .doUpdate()
                .set(FEATURE_TOGGLE_VIEW.PROJECT_ID, view.projectId().uuid())
                .set(FEATURE_TOGGLE_VIEW.ENVIRONMENT_ID, view.environmentId().uuid())
                .set(FEATURE_TOGGLE_VIEW.NAME, view.name().value())
                .set(FEATURE_TOGGLE_VIEW.DESCRIPTION, view.description().value())
                .set(FEATURE_TOGGLE_VIEW.TYPE, view.value().typeName())
                .set(FEATURE_TOGGLE_VIEW.CURRENT_VALUE, view.value().asText())
                .set(FEATURE_TOGGLE_VIEW.STATUS, view.status().name())
                .set(FEATURE_TOGGLE_VIEW.UPDATED_AT, view.updatedAt().toLocalDateTime())
                .set(FEATURE_TOGGLE_VIEW.REVISION, view.revision().value())
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, true)
                .execute();
    }

    @Override
    public boolean markInconsistentIfNotMarked(FeatureToggleId featureToggleId) {
        var rows = dslContext.update(FEATURE_TOGGLE_VIEW)
                .set(FEATURE_TOGGLE_VIEW.CONSISTENT, false)
                .where(FEATURE_TOGGLE_VIEW.ID.eq(featureToggleId.uuid()))
                .and(FEATURE_TOGGLE_VIEW.CONSISTENT.eq(true))
                .execute();
        return rows == 1;
    }
}
