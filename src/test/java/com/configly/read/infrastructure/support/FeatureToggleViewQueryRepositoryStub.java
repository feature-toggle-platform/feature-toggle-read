package com.configly.read.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.read.StubSupport;
import com.configly.read.application.port.out.FeatureToggleQueryRepository;
import com.configly.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import com.configly.read.application.query.FeatureTogglesInProjectQueryModel;
import com.configly.read.domain.FeatureToggleView;

import java.util.List;
import java.util.Optional;

import static com.configly.read.StubSupport.forMethod;

public class FeatureToggleViewQueryRepositoryStub implements FeatureToggleQueryRepository {

    private final StubSupport<Optional<FeatureToggleView>> find =
            forMethod("find(FeatureToggleId)");
    private final StubSupport<Optional<FeatureToggleView>> findConsistent =
            forMethod("findConsistent(FeatureToggleId)");

    private final StubSupport<FeatureTogglesInProjectQueryModel> findByProject =
            forMethod("findByProject(ProjectId)");
    private final StubSupport<FeatureTogglesInEnvironmentQueryModel> findByEnvironment =
            forMethod("findByEnvironment(ProjectId, EnvironmentId)");


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

    public void findByEnvironmentReturns(FeatureTogglesInEnvironmentQueryModel value) {
        findByEnvironment.willReturn(value);
    }

    public void findByProjectReturns(FeatureTogglesInProjectQueryModel value) {
        findByProject.willReturn(value);
    }

    public void findByProjectThrows(RuntimeException ex) {
        findByProject.willThrow(ex);
    }

    public void findByEnvironmentThrows(RuntimeException ex) {
        findByEnvironment.willThrow(ex);
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
    public List<FeatureToggleView> find(ProjectId projectId, EnvironmentId environmentId) {
        return List.of();
    }

    @Override
    public Optional<FeatureTogglesInEnvironmentQueryModel> findByEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        return Optional.ofNullable(findByEnvironment.get());
    }

    @Override
    public Optional<FeatureTogglesInProjectQueryModel> findByProject(ProjectId projectId) {
        return Optional.ofNullable(findByProject.get());
    }


    public void reset() {
        find.reset();
        findConsistent.reset();
        findByProject.reset();
        findByProject.reset();
    }
}
