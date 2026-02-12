package pl.feature.toggle.service.read.infrastructure.support;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.StubSupport;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleViewConsistency;
import pl.feature.toggle.service.read.domain.FeatureToggleView;

import java.util.ArrayList;
import java.util.List;

public class FeatureToggleViewConsistencySpy implements FeatureToggleViewConsistency {

    private final StubSupport<FeatureToggleView> getTrusted = StubSupport.forMethod(
            "getTrusted(FeatureToggleId)"
    );

    private final List<FeatureToggleId> trustedFeatureToggleIds = new ArrayList<>();
    private final List<FeatureToggleId> rebuildFeatureToggleIds = new ArrayList<>();

    private boolean failOnAnyCall;


    public void reset() {
        getTrusted.reset();
        trustedFeatureToggleIds.clear();
        rebuildFeatureToggleIds.clear();
        failOnAnyCall = false;
    }

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void getTrustedReturns(FeatureToggleView value) {
        getTrusted.willReturn(value);
    }

    public void getTrustedThrows(RuntimeException ex) {
        getTrusted.willThrow(ex);
    }

    @Override
    public FeatureToggleView getTrusted(FeatureToggleId featureToggleId) {
        if (failOnAnyCall) {
            throw new AssertionError("get trusted should not be called");
        }
        trustedFeatureToggleIds.add(featureToggleId);
        return getTrusted.get();
    }

    @Override
    public void rebuild(FeatureToggleId featureToggleId) {
        if (failOnAnyCall) {
            throw new AssertionError("rebuild should not be called");
        }
        rebuildFeatureToggleIds.add(featureToggleId);
    }

    public int getTrustedCalls() {
        return trustedFeatureToggleIds.size();
    }

    public int rebuildCalls() {
        return rebuildFeatureToggleIds.size();
    }

    public FeatureToggleId lastGetTrusted() {
        return trustedFeatureToggleIds.isEmpty() ? null : trustedFeatureToggleIds.getLast();
    }

    public FeatureToggleId lastRebuild() {
        return rebuildFeatureToggleIds.isEmpty() ? null : rebuildFeatureToggleIds.getLast();
    }

}

