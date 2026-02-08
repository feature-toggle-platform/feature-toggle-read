package pl.feature.toggle.service.read.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.Optional;

import static pl.feature.ftaas.jooq.tables.FeatureToggleView.FEATURE_TOGGLE_VIEW;

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
}
