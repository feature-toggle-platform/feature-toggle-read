package pl.feature.toggle.service.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInEnvironmentQueryModel;
import pl.feature.toggle.service.read.application.query.FeatureTogglesInProjectQueryModel;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.domain.ProjectView;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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
        // given
        var id = FeatureToggleId.create();

        // when
        var result = sut.find(id);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_find_toggle_when_exists() {
        // given
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

        // when
        var actual = sut.find(id).orElseThrow();

        // then
        assertThat(actual.id()).isEqualTo(id);
        assertThat(actual.name().value()).isEqualTo("my-toggle");
        assertThat(actual.revision()).isEqualTo(Revision.from(3));
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_find_only_consistent_toggle() {
        // given
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

        // when
        var consistent = sut.findConsistent(consistentId);
        var inconsistent = sut.findConsistent(inconsistentId);

        // then
        assertThat(consistent).isPresent();
        assertThat(inconsistent).isEmpty();
    }

    @Test
    void should_return_empty_when_project_has_no_toggles_for_findByProject() {
        // given
        var projectView = fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .name("project-a")
                .build();
        insertProjectView(projectView);

        var environmentView = fakeEnvironmentViewBuilder()
                .id(ENVIRONMENT_ID)
                .projectId(PROJECT_ID)
                .name("dev")
                .build();
        insertEnvironmentView(environmentView);

        // when
        var result = sut.findByProject(PROJECT_ID);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_project_with_grouped_environments_and_sorted_toggles_for_findByProject() {
        // given
        var devEnvironmentId = EnvironmentId.create();
        var prodEnvironmentId = EnvironmentId.create();

        var devUpdatedAt = LocalDateTime.of(2026, 1, 2, 10, 0);
        var prodUpdatedAt = LocalDateTime.of(2026, 1, 2, 11, 0);
        var devToggleAUpdatedAt = LocalDateTime.of(2026, 1, 3, 10, 0);
        var devToggleZUpdatedAt = LocalDateTime.of(2026, 1, 3, 11, 0);
        var prodToggleAUpdatedAt = LocalDateTime.of(2026, 1, 3, 12, 0);
        var prodToggleZUpdatedAt = LocalDateTime.of(2026, 1, 3, 13, 0);

        var projectView = fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .name("project-a")
                .build();
        insertProjectView(projectView);

        insertEnvironmentView(fakeEnvironmentViewBuilder()
                .id(prodEnvironmentId)
                .projectId(PROJECT_ID)
                .name("prod")
                .revision(Revision.from(20))
                .updatedAt(prodUpdatedAt)
                .consistent(false)
                .build());

        insertEnvironmentView(fakeEnvironmentViewBuilder()
                .id(devEnvironmentId)
                .projectId(PROJECT_ID)
                .name("dev")
                .revision(Revision.from(10))
                .updatedAt(devUpdatedAt)
                .consistent(true)
                .build());

        var devToggleZId = FeatureToggleId.create();
        var devToggleAId = FeatureToggleId.create();
        var prodToggleZId = FeatureToggleId.create();
        var prodToggleAId = FeatureToggleId.create();

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(devToggleZId)
                .projectId(PROJECT_ID)
                .environmentId(devEnvironmentId)
                .name("z-dev-toggle")
                .description("z-dev-desc")
                .value(FeatureToggleValueBuilder.bool(true))
                .status(FeatureToggleStatus.ARCHIVED)
                .updatedAt(devToggleZUpdatedAt)
                .consistent(false)
                .build());

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(prodToggleZId)
                .projectId(PROJECT_ID)
                .environmentId(prodEnvironmentId)
                .name("z-prod-toggle")
                .description("z-prod-desc")
                .value(FeatureToggleValueBuilder.bool(false))
                .status(FeatureToggleStatus.ACTIVE)
                .updatedAt(prodToggleZUpdatedAt)
                .consistent(true)
                .build());

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(prodToggleAId)
                .projectId(PROJECT_ID)
                .environmentId(prodEnvironmentId)
                .name("a-prod-toggle")
                .description("a-prod-desc")
                .value(FeatureToggleValueBuilder.bool(true))
                .status(FeatureToggleStatus.ARCHIVED)
                .updatedAt(prodToggleAUpdatedAt)
                .consistent(false)
                .build());

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(devToggleAId)
                .projectId(PROJECT_ID)
                .environmentId(devEnvironmentId)
                .name("a-dev-toggle")
                .description("a-dev-desc")
                .value(FeatureToggleValueBuilder.bool(false))
                .status(FeatureToggleStatus.ACTIVE)
                .updatedAt(devToggleAUpdatedAt)
                .consistent(true)
                .build());

        // when
        var result = sut.findByProject(PROJECT_ID).orElseThrow();

        // then
        assertThat(result.projectData().projectId()).isEqualTo(PROJECT_ID.uuid());
        assertThat(result.projectData().projectName()).isEqualTo("project-a");
        assertThat(result.projectData().environments()).hasSize(2);
        assertThat(result.projectData().environments())
                .extracting(FeatureTogglesInProjectQueryModel.EnvironmentData::environmentName)
                .containsExactly("dev", "prod");

        var devEnvironment = result.projectData().environments().getFirst();
        assertThat(devEnvironment.environmentId()).isEqualTo(devEnvironmentId.uuid());
        assertThat(devEnvironment.environmentName()).isEqualTo("dev");
        assertThat(devEnvironment.revision()).isEqualTo(10);
        assertThat(devEnvironment.updatedAt()).isEqualTo(devUpdatedAt);
        assertThat(devEnvironment.consistent()).isTrue();
        assertThat(devEnvironment.featureToggles())
                .extracting(
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::featureToggleId,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::name,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::description,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::type,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::value,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::status,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::updatedAt,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::consistent
                )
                .containsExactly(
                        tuple(devToggleAId.uuid(), "a-dev-toggle", "a-dev-desc", "BOOLEAN", "FALSE", "ACTIVE", devToggleAUpdatedAt, true),
                        tuple(devToggleZId.uuid(), "z-dev-toggle", "z-dev-desc", "BOOLEAN", "TRUE", "ARCHIVED", devToggleZUpdatedAt, false)
                );

        var prodEnvironment = result.projectData().environments().get(1);
        assertThat(prodEnvironment.environmentId()).isEqualTo(prodEnvironmentId.uuid());
        assertThat(prodEnvironment.environmentName()).isEqualTo("prod");
        assertThat(prodEnvironment.revision()).isEqualTo(20);
        assertThat(prodEnvironment.updatedAt()).isEqualTo(prodUpdatedAt);
        assertThat(prodEnvironment.consistent()).isFalse();
        assertThat(prodEnvironment.featureToggles())
                .extracting(
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::featureToggleId,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::name,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::description,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::type,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::value,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::status,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::updatedAt,
                        FeatureTogglesInProjectQueryModel.FeatureToggleData::consistent
                )
                .containsExactly(
                        tuple(prodToggleAId.uuid(), "a-prod-toggle", "a-prod-desc", "BOOLEAN", "TRUE", "ARCHIVED", prodToggleAUpdatedAt, false),
                        tuple(prodToggleZId.uuid(), "z-prod-toggle", "z-prod-desc", "BOOLEAN", "FALSE", "ACTIVE", prodToggleZUpdatedAt, true)
                );
    }

    @Test
    void should_return_empty_when_environment_has_no_toggles_for_findByEnvironment() {
        // given
        var projectView = fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .name("project-a")
                .build();
        insertProjectView(projectView);

        var environmentView = fakeEnvironmentViewBuilder()
                .id(ENVIRONMENT_ID)
                .projectId(PROJECT_ID)
                .name("dev")
                .build();
        insertEnvironmentView(environmentView);

        // when
        var result = sut.findByEnvironment(PROJECT_ID, ENVIRONMENT_ID);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_environment_with_sorted_toggles_for_findByEnvironment() {
        // given
        var targetProjectId = PROJECT_ID;
        var targetEnvironmentId = EnvironmentId.create();
        var otherEnvironmentId = EnvironmentId.create();
        var otherProjectId = ProjectId.create();
        var otherProjectEnvironmentId = EnvironmentId.create();

        var targetEnvironmentUpdatedAt = LocalDateTime.of(2026, 2, 1, 9, 0);
        var targetToggleAUpdatedAt = LocalDateTime.of(2026, 2, 2, 9, 0);
        var targetToggleZUpdatedAt = LocalDateTime.of(2026, 2, 2, 10, 0);

        insertProjectView(fakeProjectViewBuilder()
                .id(targetProjectId)
                .name("project-a")
                .build());

        insertProjectView(fakeProjectViewBuilder()
                .id(otherProjectId)
                .name("project-b")
                .build());

        insertEnvironmentView(fakeEnvironmentViewBuilder()
                .id(targetEnvironmentId)
                .projectId(targetProjectId)
                .name("dev")
                .revision(Revision.from(15))
                .updatedAt(targetEnvironmentUpdatedAt)
                .consistent(false)
                .build());

        insertEnvironmentView(fakeEnvironmentViewBuilder()
                .id(otherEnvironmentId)
                .projectId(targetProjectId)
                .name("prod")
                .build());

        insertEnvironmentView(fakeEnvironmentViewBuilder()
                .id(otherProjectEnvironmentId)
                .projectId(otherProjectId)
                .name("qa")
                .build());

        var targetToggleZId = FeatureToggleId.create();
        var targetToggleAId = FeatureToggleId.create();

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(targetToggleZId)
                .projectId(targetProjectId)
                .environmentId(targetEnvironmentId)
                .name("z-target-toggle")
                .description("z-target-desc")
                .value(FeatureToggleValueBuilder.bool(true))
                .status(FeatureToggleStatus.ARCHIVED)
                .updatedAt(targetToggleZUpdatedAt)
                .consistent(false)
                .build());

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(targetToggleAId)
                .projectId(targetProjectId)
                .environmentId(targetEnvironmentId)
                .name("a-target-toggle")
                .description("a-target-desc")
                .value(FeatureToggleValueBuilder.bool(false))
                .status(FeatureToggleStatus.ACTIVE)
                .updatedAt(targetToggleAUpdatedAt)
                .consistent(true)
                .build());

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(FeatureToggleId.create())
                .projectId(targetProjectId)
                .environmentId(otherEnvironmentId)
                .name("should-be-filtered-by-environment")
                .build());

        insertToggleView(fakeFeatureToggleViewBuilder()
                .id(FeatureToggleId.create())
                .projectId(otherProjectId)
                .environmentId(otherProjectEnvironmentId)
                .name("should-be-filtered-by-project")
                .build());

        // when
        var result = sut.findByEnvironment(targetProjectId, targetEnvironmentId).orElseThrow();

        // then
        assertThat(result.projectData().projectId()).isEqualTo(targetProjectId.uuid());
        assertThat(result.projectData().projectName()).isEqualTo("project-a");

        assertThat(result.environmentData().environmentId()).isEqualTo(targetEnvironmentId.uuid());
        assertThat(result.environmentData().environmentName()).isEqualTo("dev");
        assertThat(result.environmentData().revision()).isEqualTo(15);
        assertThat(result.environmentData().updatedAt()).isEqualTo(targetEnvironmentUpdatedAt);
        assertThat(result.environmentData().consistent()).isFalse();
        assertThat(result.environmentData().featureToggles())
                .extracting(
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::featureToggleId,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::name,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::description,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::type,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::value,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::status,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::updatedAt,
                        FeatureTogglesInEnvironmentQueryModel.FeatureToggleData::consistent
                )
                .containsExactly(
                        tuple(targetToggleAId.uuid(), "a-target-toggle", "a-target-desc", "BOOLEAN", "FALSE", "ACTIVE", targetToggleAUpdatedAt, true),
                        tuple(targetToggleZId.uuid(), "z-target-toggle", "z-target-desc", "BOOLEAN", "TRUE", "ARCHIVED", targetToggleZUpdatedAt, false)
                );
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
