package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.util.ArrayList;
import java.util.List;

import static pl.feature.toggle.service.read.StubSupport.forMethod;

public class ProjectViewProjectionRepositorySpy implements ProjectProjectionRepository {

    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(ProjectId)");

    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
    }

    public void markInconsistentIfNotMarkedThrows(RuntimeException ex) {
        markInconsistentIfNotMarked.willThrow(ex);
    }

    private final List<ProjectView> inserted = new ArrayList<>();
    private final List<ProjectView> updated = new ArrayList<>();
    private final List<ProjectView> upserted = new ArrayList<>();
    private final List<ProjectId> markedInconsistent = new ArrayList<>();

    private boolean failOnAnyCall;
    private boolean noUpdates;
    private boolean noInserts;
    private boolean noUpserts;
    private boolean noConsistent;

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        markInconsistentIfNotMarked.reset();

        inserted.clear();
        updated.clear();
        upserted.clear();
        markedInconsistent.clear();

        failOnAnyCall = false;
        noUpdates = false;
        noInserts = false;
        noUpserts = false;
        noConsistent = false;
    }

    @Override
    public void insert(ProjectView ref) {
        if (failOnAnyCall || noInserts) {
            throw new AssertionError("insert should not be called");
        }
        inserted.add(ref);
    }

    @Override
    public void updateStatus(ProjectView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void updateBasicFields(ProjectView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void upsert(ProjectView ref) {
        if (failOnAnyCall || noUpserts) {
            throw new AssertionError("upsert should not be called");
        }
        upserted.add(ref);
    }

    @Override
    public boolean markInconsistentIfNotMarked(ProjectId projectId) {
        if (failOnAnyCall || noConsistent) {
            throw new AssertionError("markInconsistentIfNotMarked should not be called");
        }
        markedInconsistent.add(projectId);
        return markInconsistentIfNotMarked.get();
    }

    public ProjectView lastUpserted() {
        return upserted.isEmpty() ? null : upserted.getLast();
    }

    public ProjectView lastInserted() {
        return inserted.isEmpty() ? null : inserted.getLast();
    }

    public ProjectView lastUpdated() {
        return updated.isEmpty() ? null : updated.getLast();
    }

    public void expectNoUpdates() {
        noUpdates = true;
    }

    public void expectNoUpserts() {
        noUpserts = true;
    }

    public void expectNoInserts() {
        noInserts = true;
    }

    public void expectNoMarkInconsistent() {
        noConsistent = true;
    }


    public ProjectId lastMarkedInconsistent() {
        return markedInconsistent.isEmpty() ? null : markedInconsistent.getLast();
    }
}
