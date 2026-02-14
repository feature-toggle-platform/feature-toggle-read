package pl.feature.toggle.service.read.builder;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.value.FeatureToggleValue;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;

public class FakeFeatureToggleViewBuilder {

    private FeatureToggleId id;
    private ProjectId projectId;
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
        projectId = ProjectId.create();
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

    public FakeFeatureToggleViewBuilder projectId(ProjectId projectId) {
        this.projectId = projectId;
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

    public FakeFeatureToggleViewBuilder updatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public FakeFeatureToggleViewBuilder consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public FeatureToggleView build() {
        return new FeatureToggleView(
                id,
                projectId,
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
