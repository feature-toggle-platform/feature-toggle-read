package pl.feature.toggle.service.read.builder;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentName;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.EnvironmentView;

public class FakeEnvironmentViewBuilder {

    private EnvironmentId id;
    private ProjectId projectId;
    private EnvironmentName name;
    private String type;
    private EnvironmentStatus status;
    private CreatedAt createdAt;
    private UpdatedAt updatedAt;
    private Revision revision;
    private boolean consistent;

    private FakeEnvironmentViewBuilder() {
        id = EnvironmentId.create();
        projectId = ProjectId.create();
        name = EnvironmentName.create("env-name");
        type = "DEFAULT";
        status = EnvironmentStatus.ACTIVE;
        createdAt = CreatedAt.now();
        updatedAt = UpdatedAt.now();
        revision = Revision.initialRevision();
        consistent = true;
    }

    public static FakeEnvironmentViewBuilder fakeEnvironmentViewBuilder() {
        return new FakeEnvironmentViewBuilder();
    }

    public FakeEnvironmentViewBuilder id(EnvironmentId id) {
        this.id = id;
        return this;
    }

    public FakeEnvironmentViewBuilder projectId(ProjectId projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeEnvironmentViewBuilder name(EnvironmentName name) {
        this.name = name;
        return this;
    }

    public FakeEnvironmentViewBuilder type(String type) {
        this.type = type;
        return this;
    }

    public FakeEnvironmentViewBuilder status(EnvironmentStatus status) {
        this.status = status;
        return this;
    }

    public FakeEnvironmentViewBuilder createdAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public FakeEnvironmentViewBuilder updatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public FakeEnvironmentViewBuilder revision(Revision revision) {
        this.revision = revision;
        return this;
    }

    public FakeEnvironmentViewBuilder consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public EnvironmentView build() {
        return new EnvironmentView(
                id,
                projectId,
                name,
                type,
                status,
                createdAt,
                updatedAt,
                revision,
                consistent
        );
    }
}
