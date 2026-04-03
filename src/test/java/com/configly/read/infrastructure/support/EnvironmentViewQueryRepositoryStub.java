package com.configly.read.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.StubSupport;
import com.configly.read.application.port.out.EnvironmentQueryRepository;
import com.configly.read.domain.EnvironmentView;

import java.util.List;
import java.util.Optional;

import static com.configly.read.StubSupport.forMethod;

public class EnvironmentViewQueryRepositoryStub implements EnvironmentQueryRepository {

    private final StubSupport<Optional<EnvironmentView>> find =
            forMethod("find(ProjectId, EnvironmentId)");
    private final StubSupport<Optional<EnvironmentView>> findConsistent =
            forMethod("findConsistent(ProjectId, EnvironmentId)");
    private final StubSupport<List<EnvironmentView>> findByProjectId =
            forMethod("findByProjectId(ProjectId)");

    public void findReturns(EnvironmentView value) {
        find.willReturn(Optional.ofNullable(value));
    }

    public void findThrows(RuntimeException ex) {
        find.willThrow(ex);
    }

    public void findConsistentReturns(EnvironmentView value) {
        findConsistent.willReturn(Optional.ofNullable(value));
    }

    public void findConsistentThrows(RuntimeException ex) {
        findConsistent.willThrow(ex);
    }

    public void findByProjectId(List<EnvironmentView> views) {
        findByProjectId.willReturn(views);
    }

    public void findByProjectIdThrows(RuntimeException ex) {
        findByProjectId.willThrow(ex);
    }

    @Override
    public Optional<EnvironmentView> find(ProjectId projectId, EnvironmentId environmentId) {
        return find.get();
    }

    @Override
    public Optional<EnvironmentView> findConsistent(ProjectId projectId, EnvironmentId environmentId) {
        return findConsistent.get();
    }

    @Override
    public List<EnvironmentView> findByProjectId(ProjectId projectId) {
        return findByProjectId.get();
    }

    public void reset() {
        find.reset();
        findByProjectId.reset();
        findConsistent.reset();
    }
}
