package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;

import java.util.ArrayList;
import java.util.List;

import static pl.feature.toggle.service.read.StubSupport.forMethod;

public class EnvironmentViewProjectionRepositorySpy implements EnvironmentProjectionRepository {

    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(EnvironmentId)");


    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
    }

    public void markInconsistentIfNotMarkedThrows(RuntimeException ex) {
        markInconsistentIfNotMarked.willThrow(ex);
    }

    private final List<EnvironmentView> inserted = new ArrayList<>();
    private final List<EnvironmentView> updated = new ArrayList<>();
    private final List<EnvironmentView> upserted = new ArrayList<>();
    private final List<EnvironmentId> markedInconsistent = new ArrayList<>();

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
    public void insert(EnvironmentView view) {
        if (failOnAnyCall || noInserts) {
            throw new AssertionError("insert should not be called");
        }
        inserted.add(view);
    }

    @Override
    public void updateStatus(EnvironmentView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void updateName(EnvironmentView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void updateType(EnvironmentView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void upsert(EnvironmentView view) {
        if (failOnAnyCall || noUpserts) {
            throw new AssertionError("upsert should not be called");
        }
        upserted.add(view);
    }

    @Override
    public boolean markInconsistentIfNotMarked(EnvironmentId environmentId) {
        if (failOnAnyCall || noConsistent) {
            throw new AssertionError("markInconsistentIfNotMarked should not be called");
        }
        markedInconsistent.add(environmentId);
        return markInconsistentIfNotMarked.get();
    }

    public EnvironmentView lastInserted() {
        return inserted.isEmpty() ? null : inserted.getLast();
    }

    public EnvironmentView lastUpdated() {
        return updated.isEmpty() ? null : updated.getLast();
    }

    public EnvironmentView lastUpserted() {
        return upserted.isEmpty() ? null : upserted.getLast();
    }

    public EnvironmentId lastMarkedInconsistent() {
        return markedInconsistent.isEmpty() ? null : markedInconsistent.getLast();
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

}
