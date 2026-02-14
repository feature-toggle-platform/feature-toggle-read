package pl.feature.toggle.service.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.domain.ProjectView;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeFeatureToggleViewBuilder.fakeFeatureToggleViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class FeatureToggleQueryJooqRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleQueryRepository sut;

    @Autowired
    private FeatureToggleProjectionRepository projectionRepository;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @Autowired
    private EnvironmentProjectionRepository environmentProjectionRepository;


    @Test
    void should_return_empty_when_toggle_missing() {
        var id = FeatureToggleId.create();

        var result = sut.find(id);

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_toggle_when_exists() {
        var id = FeatureToggleId.create();
        var projectView = fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .build();
        insertProjectView(projectView);

        var environmentView = fakeEnvironmentViewBuilder()
                .id(ENVIRONMENT_ID)
                .projectId(PROJECT_ID)
                .build();
        insertEnvironmentView(environmentView);

        var expectedView = fakeFeatureToggleViewBuilder()
                .id(id)
                .environmentId(ENVIRONMENT_ID)
                .name("my-toggle")
                .revision(Revision.from(3))
                .consistent(true)
                .build();
        insertToggleView(expectedView);

        var actual = sut.find(id).orElseThrow();

        assertThat(actual.id()).isEqualTo(id);
        assertThat(actual.name().value()).isEqualTo("my-toggle");
        assertThat(actual.revision()).isEqualTo(Revision.from(3));
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_find_only_consistent_toggle() {
        var consistentId = FeatureToggleId.create();
        var inconsistentId = FeatureToggleId.create();
        var projectView = fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .build();
        insertProjectView(projectView);

        var environmentView = fakeEnvironmentViewBuilder()
                .id(ENVIRONMENT_ID)
                .projectId(PROJECT_ID)
                .build();
        insertEnvironmentView(environmentView);

        var consistentView = fakeFeatureToggleViewBuilder()
                .id(consistentId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("my-toggle")
                .revision(Revision.from(1))
                .consistent(true)
                .build();

        var inconsistentView = fakeFeatureToggleViewBuilder()
                .id(inconsistentId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("my-toggle2")
                .revision(Revision.from(1))
                .consistent(false)
                .build();

        insertToggleView(consistentView);
        insertToggleView(inconsistentView);

        var consistent = sut.findConsistent(consistentId);
        var inconsistent = sut.findConsistent(inconsistentId);

        assertThat(consistent).isPresent();
        assertThat(inconsistent).isEmpty();
    }

    private void insertToggleView(FeatureToggleView view) {
        projectionRepository.insert(view);
    }

    private void insertProjectView(ProjectView projectView) {
        projectProjectionRepository.insert(projectView);
    }

    private void insertEnvironmentView(EnvironmentView environmentView) {
        environmentProjectionRepository.insert(environmentView);
    }
}
