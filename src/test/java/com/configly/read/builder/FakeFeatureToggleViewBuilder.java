package com.configly.read.builder;

import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.read.domain.FeatureToggleView;
import com.configly.value.FeatureToggleValue;
import com.configly.value.FeatureToggleValueBuilder;

import java.time.LocalDateTime;

public class FakeFeatureToggleViewBuilder {

    private FeatureToggleId id;
    private EnvironmentId environmentId;
    private FeatureToggleName name;
    private FeatureToggleDescription description;
    private FeatureToggleValue value;
    private FeatureToggleStatus status;
    private Revision revision;
    private CreatedAt createdAt;
    private UpdatedAt updatedAt;
    private boolean consistent;

    private FakeFeatureToggleViewBuilder() {
        id = FeatureToggleId.create();
        environmentId = EnvironmentId.create();
        name = FeatureToggleName.create("toggle-name");
        description = FeatureToggleDescription.create("description");
        value = FeatureToggleValueBuilder.bool(true);
        status = FeatureToggleStatus.ACTIVE;
        revision = Revision.initialRevision();
        createdAt = CreatedAt.now();
        updatedAt = UpdatedAt.now();
        consistent = true;
    }

    public static FakeFeatureToggleViewBuilder fakeFeatureToggleViewBuilder() {
        return new FakeFeatureToggleViewBuilder();
    }

    public FakeFeatureToggleViewBuilder id(FeatureToggleId id) {
        this.id = id;
        return this;
    }

    public FakeFeatureToggleViewBuilder environmentId(EnvironmentId environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeFeatureToggleViewBuilder name(FeatureToggleName name) {
        this.name = name;
        return this;
    }

    public FakeFeatureToggleViewBuilder name(String name) {
        this.name = FeatureToggleName.create(name);
        return this;
    }

    public FakeFeatureToggleViewBuilder description(FeatureToggleDescription description) {
        this.description = description;
        return this;
    }

    public FakeFeatureToggleViewBuilder description(String description) {
        this.description = FeatureToggleDescription.create(description);
        return this;
    }

    public FakeFeatureToggleViewBuilder value(FeatureToggleValue value) {
        this.value = value;
        return this;
    }

    public FakeFeatureToggleViewBuilder status(FeatureToggleStatus status) {
        this.status = status;
        return this;
    }

    public FakeFeatureToggleViewBuilder revision(Revision revision) {
        this.revision = revision;
        return this;
    }

    public FakeFeatureToggleViewBuilder createdAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public FakeFeatureToggleViewBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = CreatedAt.of(createdAt);
        return this;
    }

    public FakeFeatureToggleViewBuilder updatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public FakeFeatureToggleViewBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = UpdatedAt.of(updatedAt);
        return this;
    }

    public FakeFeatureToggleViewBuilder consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public FeatureToggleView build() {
        return new FeatureToggleView(
                id,
                environmentId,
                name,
                description,
                value,
                status,
                revision,
                createdAt,
                updatedAt,
                consistent
        );
    }

}
