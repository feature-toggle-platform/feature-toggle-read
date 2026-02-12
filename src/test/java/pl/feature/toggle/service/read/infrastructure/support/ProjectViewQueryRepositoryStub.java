package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.util.Optional;

import static pl.feature.toggle.service.read.StubSupport.forMethod;


public class ProjectViewQueryRepositoryStub implements ProjectQueryRepository {

    private final StubSupport<Optional<ProjectView>> find = forMethod("find(ProjectId)");
    private final StubSupport<Optional<ProjectView>> findConsistent = forMethod("findConsistent(ProjectId)");

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

    public void reset() {
        find.reset();
        findConsistent.reset();
    }

    @Override
    public Optional<ProjectView> find(ProjectId projectId) {
        return find.get();
    }

    @Override
    public Optional<ProjectView> findConsistent(ProjectId projectId) {
        return findConsistent.get();
    }


}
