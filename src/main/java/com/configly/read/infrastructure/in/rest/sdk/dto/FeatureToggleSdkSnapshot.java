package com.configly.read.infrastructure.in.rest.sdk.dto;

import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.read.domain.EnvironmentView;
import com.configly.read.domain.FeatureToggleView;
import com.configly.read.domain.ProjectView;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeatureToggleSdkSnapshot(
        Scope scope,
        Status status,
        long revision,
        Instant generatedAt,
        List<Toggle> toggles
) {

    public static Scope createScope(ProjectView projectView, EnvironmentView environmentView) {
        return new Scope(
                projectView.id().uuid(),
                environmentView.id().uuid()
        );
    }

    public static Status createStatus(ProjectView projectView, EnvironmentView environmentView) {
        return new Status(
                projectView.status().name(),
                environmentView.status().name(),
                projectView.consistent() && environmentView.consistent()
        );
    }

    public static Toggle createToggle(FeatureToggleView view){
        return new Toggle(
                view.id().uuid(),
                view.name().value(),
                view.value().type().name(),
                view.value().typedValue(),
                view.status() == FeatureToggleStatus.ACTIVE,
                view.revision().value()
        );
    }

    public record Scope(
            UUID projectId,
            UUID environmentId
    ) {

    }

    public record Status(
            String project,
            String environment,
            boolean consistent
    ) {
    }

    public record Toggle(
            UUID id,
            String key,
            String type,
            Object value,
            boolean active,
            long revision
    ) {
    }

}
