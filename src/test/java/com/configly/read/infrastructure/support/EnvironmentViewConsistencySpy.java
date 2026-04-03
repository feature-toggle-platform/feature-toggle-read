package com.configly.read.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.StubSupport;
import com.configly.read.application.port.in.EnvironmentViewConsistency;
import com.configly.read.domain.EnvironmentView;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentViewConsistencySpy implements EnvironmentViewConsistency {

    private final StubSupport<EnvironmentView> getTrusted = StubSupport.forMethod(
            "getTrusted(ProjectId, EnvironmentId)"
    );

    private final List<ProjectId> trustedProjectIds = new ArrayList<>();
    private final List<EnvironmentId> trustedEnvironmentIds = new ArrayList<>();

    private final List<ProjectId> rebuiltProjectIds = new ArrayList<>();
    private final List<EnvironmentId> rebuiltEnvironmentIds = new ArrayList<>();

    private boolean failOnAnyCall;


    public void reset() {
        getTrusted.reset();
        trustedProjectIds.clear();
        trustedEnvironmentIds.clear();
        rebuiltProjectIds.clear();
        rebuiltEnvironmentIds.clear();
        failOnAnyCall = false;
    }

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void getTrustedReturns(EnvironmentView value) {
        getTrusted.willReturn(value);
    }

    public void getTrustedThrows(RuntimeException ex) {
        getTrusted.willThrow(ex);
    }

    @Override
    public EnvironmentView getTrusted(ProjectId projectId, EnvironmentId environmentId) {
        if (failOnAnyCall) {
            throw new AssertionError("get trusted should not be called");
        }
        trustedProjectIds.add(projectId);
        trustedEnvironmentIds.add(environmentId);
        return getTrusted.get();
    }

    @Override
    public void rebuild(ProjectId projectId, EnvironmentId environmentId) {
        if (failOnAnyCall) {
            throw new AssertionError("rebuild should not be called");
        }
        rebuiltProjectIds.add(projectId);
        rebuiltEnvironmentIds.add(environmentId);
    }

    public int getTrustedCalls() {
        return trustedProjectIds.size();
    }

    public int rebuildCalls() {
        return rebuiltProjectIds.size();
    }

    public ProjectId lastGetTrustedProjectId() {
        return trustedProjectIds.isEmpty() ? null : trustedProjectIds.getLast();
    }

    public EnvironmentId lastGetTrustedEnvironmentId() {
        return trustedEnvironmentIds.isEmpty() ? null : trustedEnvironmentIds.getLast();
    }

    public ProjectId lastRebuildProjectId() {
        return rebuiltProjectIds.isEmpty() ? null : rebuiltProjectIds.getLast();
    }

    public EnvironmentId lastRebuildEnvironmentId() {
        return rebuiltEnvironmentIds.isEmpty() ? null : rebuiltEnvironmentIds.getLast();
    }
}

