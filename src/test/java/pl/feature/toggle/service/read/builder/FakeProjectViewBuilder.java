package pl.feature.toggle.service.read.builder;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.project.ProjectDescription;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectName;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.domain.ProjectView;

import java.time.LocalDateTime;
import java.util.UUID;

public class FakeProjectViewBuilder {

    private ProjectId id;
    private ProjectName name;
    private ProjectDescription description;
    private ProjectStatus status;
    private CreatedAt createdAt;
    private UpdatedAt updatedAt;
    private Revision revision;
    private boolean consistent;

    private FakeProjectViewBuilder() {
        id = ProjectId.create();
        name = ProjectName.create("project" + UUID.randomUUID());
        description = ProjectDescription.create("description-" + UUID.randomUUID());
        status = ProjectStatus.ACTIVE;
        createdAt = CreatedAt.now();
        updatedAt = UpdatedAt.now();
        revision = Revision.initialRevision();
        consistent = true;
    }

    public static FakeProjectViewBuilder fakeProjectViewBuilder() {
        return new FakeProjectViewBuilder();
    }

    public FakeProjectViewBuilder id(ProjectId id) {
        this.id = id;
        return this;
    }

    public FakeProjectViewBuilder name(ProjectName name) {
        this.name = name;
        return this;
    }

    public FakeProjectViewBuilder name(String name) {
        this.name = ProjectName.create(name);
        return this;
    }

    public FakeProjectViewBuilder description(ProjectDescription description) {
        this.description = description;
        return this;
    }

    public FakeProjectViewBuilder description(String description) {
        this.description = ProjectDescription.create(description);
        return this;
    }

    public FakeProjectViewBuilder status(ProjectStatus status) {
        this.status = status;
        return this;
    }

    public FakeProjectViewBuilder createdAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public FakeProjectViewBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = CreatedAt.of(createdAt);
        return this;
    }

    public FakeProjectViewBuilder updatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public FakeProjectViewBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = UpdatedAt.of(updatedAt);
        return this;
    }

    public FakeProjectViewBuilder revision(Revision revision) {
        this.revision = revision;
        return this;
    }

    public FakeProjectViewBuilder consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public ProjectView build() {
        return new ProjectView(
                id,
                name,
                description,
                status,
                createdAt,
                updatedAt,
                revision,
                consistent
        );
    }

}
