package pl.feature.toggle.service.read.application.projection.featuretoggle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.out.*;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged.featureToggleStatusChangedBuilder;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeFeatureToggleViewBuilder.fakeFeatureToggleViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

@Import(FeatureToggleEventualConsistencyIT.SyncAsyncConfig.class)
class FeatureToggleEventualConsistencyIT extends AbstractITTest {

    @Autowired
    private FeatureToggleProjection sut;

    @Autowired
    private FeatureToggleQueryRepository featureToggleQueryRepository;

    @Autowired
    private FeatureToggleProjectionRepository featureToggleProjectionRepository;

    @Autowired
    private EnvironmentProjectionRepository environmentProjectionRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @MockitoBean
    private WriteClient writeClient;

    @TestConfiguration
    static class SyncAsyncConfig {

        @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
        public Executor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }


    @Test
    void should_rebuild_projection_when_feature_toggle_not_exists_and_status_changed_arrives_before_created() {
        // given
        var featureToggleId = FeatureToggleId.create();
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectView = fakeProjectViewBuilder()
                .name("TEST")
                .id(projectId)
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.initialRevision())
                .build();
        var environmentView = fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(envId)
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.initialRevision())
                .build();
        projectProjectionRepository.insert(projectView);
        environmentProjectionRepository.insert(environmentView);

        when(writeClient.fetchFeatureToggle(featureToggleId)).thenReturn(fakeFeatureToggleViewBuilder()
                .projectId(projectId)
                .id(featureToggleId)
                .environmentId(envId)
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .build());

        var outOfOrderStatusChanged = featureToggleStatusChangedBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .id(featureToggleId.uuid())
                .status(FeatureToggleStatus.ARCHIVED.name())
                .revision(Revision.from(2).value())
                .build();

        var createdLater = featureToggleCreatedEventBuilder()
                .projectId(projectId.uuid())
                .id(featureToggleId.uuid())
                .environmentId(envId.uuid())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type("BOOLEAN")
                .value(FeatureToggleValueBuilder.bool(true).asText())
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(outOfOrderStatusChanged));
        sut.handle(createdLater);

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = featureToggleQueryRepository.find(featureToggleId).orElseThrow();
                    assertThat(actual.id()).isEqualTo(featureToggleId);

                    assertThat(actual.status()).isEqualTo(FeatureToggleStatus.ARCHIVED);
                    assertThat(actual.revision()).isEqualTo(Revision.from(2));
                    assertThat(actual.consistent()).isTrue();
                });
    }

    @Test
    void should_rebuild_projection_when_feature_toggle_exists_and_gap_detected() {
        // given
        var featureToggleId = FeatureToggleId.create();
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectView = fakeProjectViewBuilder()
                .name("TEST")
                .id(projectId)
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.initialRevision())
                .build();
        var environmentView = fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(envId)
                .status(EnvironmentStatus.ACTIVE)
                .build();
        var featureToggleView = fakeFeatureToggleViewBuilder()
                .projectId(projectId)
                .id(featureToggleId)
                .environmentId(envId)
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.initialRevision())
                .build();
        projectProjectionRepository.insert(projectView);
        environmentProjectionRepository.insert(environmentView);
        featureToggleProjectionRepository.insert(featureToggleView);

        when(writeClient.fetchFeatureToggle(featureToggleId)).thenReturn(fakeFeatureToggleViewBuilder()
                .projectId(projectId)
                .id(featureToggleId)
                .environmentId(envId)
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(5))
                .build());

        var gapEvent = featureToggleStatusChangedBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .id(featureToggleId.uuid())
                .status(FeatureToggleStatus.ARCHIVED.name())
                .revision(Revision.from(5).value())
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(gapEvent));

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = featureToggleQueryRepository.find(featureToggleId).orElseThrow();
                    assertThat(actual.id()).isEqualTo(featureToggleId);

                    assertThat(actual.status()).isEqualTo(FeatureToggleStatus.ARCHIVED);
                    assertThat(actual.revision()).isEqualTo(Revision.from(5));
                    assertThat(actual.consistent()).isTrue();
                });
    }

}
