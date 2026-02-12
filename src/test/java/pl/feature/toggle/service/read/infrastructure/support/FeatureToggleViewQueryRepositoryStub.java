package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.Optional;

import static pl.feature.toggle.service.read.StubSupport.forMethod;

public class FeatureToggleViewQueryRepositoryStub implements FeatureToggleQueryRepository {

    private final StubSupport<Optional<FeatureToggleView>> find =
            forMethod("find(FeatureToggleId)");
    private final StubSupport<Optional<FeatureToggleView>> findConsistent =
            forMethod("findConsistent(FeatureToggleId)");

    public void findReturns(FeatureToggleView value) {
        find.willReturn(Optional.ofNullable(value));
    }

    public void findThrows(RuntimeException ex) {
        find.willThrow(ex);
    }

    public void findConsistentReturns(FeatureToggleView value) {
        findConsistent.willReturn(Optional.ofNullable(value));
    }

    public void findConsistentThrows(RuntimeException ex) {
        findConsistent.willThrow(ex);
    }

    @Override
    public Optional<FeatureToggleView> find(FeatureToggleId featureToggleId) {
        return find.get();
    }

    @Override
    public Optional<FeatureToggleView> findConsistent(FeatureToggleId featureToggleId) {
        return findConsistent.get();
    }

    public void reset() {
        find.reset();
        findConsistent.reset();
    }
}
