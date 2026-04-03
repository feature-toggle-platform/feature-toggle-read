package com.configly.read.infrastructure.support;

import com.configly.model.project.ProjectId;
import com.configly.read.StubSupport;
import com.configly.read.application.port.out.ProjectQueryRepository;
import com.configly.read.domain.ProjectView;

import java.util.List;
import java.util.Optional;

import static com.configly.read.StubSupport.forMethod;


public class ProjectViewQueryRepositoryStub implements ProjectQueryRepository {

    private final StubSupport<Optional<ProjectView>> find = forMethod("find(ProjectId)");
    private final StubSupport<Optional<ProjectView>> findConsistent = forMethod("findConsistent(ProjectId)");
    private final StubSupport<List<ProjectView>> findAll = forMethod("findAll()");

    public void findReturns(ProjectView value) {
        find.willReturn(Optional.ofNullable(value));
    }

    public void findConsistentReturns(ProjectView value) {
        findConsistent.willReturn(Optional.ofNullable(value));
    }

    public void findThrows(RuntimeException ex) {
        find.willThrow(ex);
    }

    public void findConsistentThrows(RuntimeException ex) {
        findConsistent.willThrow(ex);
    }

    public void findAllReturns(List<ProjectView> views) {
        findAll.willReturn(views);
    }

    public void findAllThrows(RuntimeException ex) {
        findAll.willThrow(ex);
    }

    public void reset() {
        find.reset();
        findConsistent.reset();
        findAll.reset();
    }

    @Override
    public Optional<ProjectView> find(ProjectId projectId) {
        return find.get();
    }

    @Override
    public Optional<ProjectView> findConsistent(ProjectId projectId) {
        return findConsistent.get();
    }

    @Override
    public List<ProjectView> findAll() {
        return findAll.get();
    }


}
