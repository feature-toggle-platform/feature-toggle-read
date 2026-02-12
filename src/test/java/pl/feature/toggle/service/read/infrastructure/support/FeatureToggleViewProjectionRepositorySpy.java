package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.ArrayList;
import java.util.List;

import static pl.feature.toggle.service.read.StubSupport.forMethod;

public class FeatureToggleViewProjectionRepositorySpy implements FeatureToggleProjectionRepository {

    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(FeatureToggleId)");


    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
    }

    public void markInconsistentIfNotMarkedThrows(RuntimeException ex) {
        markInconsistentIfNotMarked.willThrow(ex);
    }

    private final List<FeatureToggleView> inserted = new ArrayList<>();
    private final List<FeatureToggleView> updated = new ArrayList<>();
    private final List<FeatureToggleView> upserted = new ArrayList<>();
    private final List<FeatureToggleId> markedInconsistent = new ArrayList<>();

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
    public void insert(FeatureToggleView view) {
        if (failOnAnyCall || noInserts) {
            throw new AssertionError("insert should not be called");
        }
        inserted.add(view);
    }

    @Override
    public void updateStatus(FeatureToggleView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void updateBasicFields(FeatureToggleView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void updateValue(FeatureToggleView view) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(view);
    }

    @Override
    public void upsert(FeatureToggleView view) {
        if (failOnAnyCall || noUpserts) {
            throw new AssertionError("upsert should not be called");
        }
        upserted.add(view);
    }

    @Override
    public boolean markInconsistentIfNotMarked(FeatureToggleId featureToggleId) {
        if (failOnAnyCall || noConsistent) {
            throw new AssertionError("markInconsistentIfNotMarked should not be called");
        }
        markedInconsistent.add(featureToggleId);
        return markInconsistentIfNotMarked.get();
    }

    public FeatureToggleView lastInserted() {
        return inserted.isEmpty() ? null : inserted.getLast();
    }

    public FeatureToggleView lastUpdated() {
        return updated.isEmpty() ? null : updated.getLast();
    }

    public FeatureToggleView lastUpserted() {
        return upserted.isEmpty() ? null : upserted.getLast();
    }

    public FeatureToggleId lastMarkedInconsistent() {
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
