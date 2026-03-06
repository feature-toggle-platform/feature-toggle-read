package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInProjectQueryModel;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.List;
import java.util.Optional;

import static pl.feature.toggle.service.read.StubSupport.forMethod;

public class FeatureToggleViewQueryRepositoryStub implements FeatureToggleQueryRepository {

    private final StubSupport<Optional<FeatureToggleView>> find =
            forMethod("find(FeatureToggleId)");
    private final StubSupport<Optional<FeatureToggleView>> findConsistent =
            forMethod("findConsistent(FeatureToggleId)");
    private final StubSupport<List<FeatureTogglesInProjectQueryModel>> findByContext =
            forMethod("findByContext(ProjectId, EnvironmentId)");

    private final StubSupport<List<ProjectFeatureToggleQueryModel>> findByProject =
            forMethod("findByContext(ProjectId)");


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

    public void findByContextReturns(List<FeatureTogglesInProjectQueryModel> value) {
        findByContext.willReturn(value);
    }

    public void findByProjectReturns(List<ProjectFeatureToggleQueryModel> value) {
        findByProject.willReturn(value);
    }

    public void findByProjectThrows(RuntimeException ex) {
        findByProject.willThrow(ex);
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
    public Optional<FeatureTogglesInEnvironmentQueryModel> findByContext(ProjectId projectId, EnvironmentId environmentId) {
        return findByContext.get();
    }

    @Override
    public Optional<FeatureTogglesInProjectQueryModel> findByProject(ProjectId projectId) {
        return findByProject.get();
    }


    public void reset() {
        find.reset();
        findConsistent.reset();
        findByContext.reset();
        findByProject.reset();
    }
}
