package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.List;
import java.util.Optional;

import static pl.feature.toggle.service.read.StubSupport.forMethod;

public class FeatureToggleViewQueryRepositoryStub implements FeatureToggleQueryRepository {

    private final StubSupport<Optional<FeatureToggleView>> find =
            forMethod("find(FeatureToggleId)");
    private final StubSupport<Optional<FeatureToggleView>> findConsistent =
            forMethod("findConsistent(FeatureToggleId)");
    private final StubSupport<List<FeatureToggleView>> findByContext =
            forMethod("findByContext(ProjectId, EnvironmentId)");

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

    public void findByContextReturns(List<FeatureToggleView> value) {
        findByContext.willReturn(value);
    }

    public void findByContextThrows(RuntimeException ex) {
        findByContext.willThrow(ex);
    }

    @Override
    public Optional<FeatureToggleView> find(FeatureToggleId featureToggleId) {
        return find.get();
    }

    @Override
    public Optional<FeatureToggleView> findConsistent(FeatureToggleId featureToggleId) {
        return findConsistent.get();
    }

    @Override
    public List<FeatureToggleView> findByContext(ProjectId projectId, EnvironmentId environmentId) {
        return findByContext.get();
    }

    public void reset() {
        find.reset();
        findConsistent.reset();
        findByContext.reset();
    }
}
