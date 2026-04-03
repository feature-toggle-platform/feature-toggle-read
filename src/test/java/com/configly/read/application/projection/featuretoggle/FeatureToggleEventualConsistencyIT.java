package com.configly.read.application.projection.featuretoggle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.read.AbstractITTest;
import com.configly.read.application.port.in.FeatureToggleProjection;
import com.configly.read.application.port.out.*;
import com.configly.value.FeatureToggleValueBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static com.configly.contracts.fake.event.FakeFeatureToggleCreatedBuilder.fakeFeatureToggleCreatedBuilder;
import static com.configly.contracts.fake.event.FakeFeatureToggleStatusChangedBuilder.fakeFeatureToggleStatusChangedBuilder;
import static com.configly.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static com.configly.read.builder.FakeFeatureToggleViewBuilder.fakeFeatureToggleViewBuilder;
import static com.configly.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

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
    private FeatureToggleClient featureToggleClient;

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

        when(featureToggleClient.fetchFeatureToggle(featureToggleId)).thenReturn(fakeFeatureToggleViewBuilder()
                .id(featureToggleId)
                .environmentId(envId)
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .build());

        var outOfOrderStatusChanged = fakeFeatureToggleStatusChangedBuilder()
                .withEnvironmentId(envId.uuid())
                .withId(featureToggleId.uuid())
                .withStatus(FeatureToggleStatus.ARCHIVED.name())
                .withRevision(Revision.from(2).value())
                .build();

        var createdLater = fakeFeatureToggleCreatedBuilder()
                .withId(featureToggleId.uuid())
                .withEnvironmentId(envId.uuid())
                .withCreatedAt(LocalDateTime.now())
                .withUpdatedAt(LocalDateTime.now())
                .withType("BOOLEAN")
                .withValue(FeatureToggleValueBuilder.bool(true).asText())
                .withStatus(EnvironmentStatus.ACTIVE.name())
                .withRevision(Revision.initialRevision().value())
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
                .id(envId)
                .projectId(projectId)
                .status(EnvironmentStatus.ACTIVE)
                .build();
        var featureToggleView = fakeFeatureToggleViewBuilder()
                .id(featureToggleId)
                .environmentId(envId)
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.initialRevision())
                .build();
        projectProjectionRepository.insert(projectView);
        environmentProjectionRepository.insert(environmentView);
        featureToggleProjectionRepository.insert(featureToggleView);

        when(featureToggleClient.fetchFeatureToggle(featureToggleId)).thenReturn(fakeFeatureToggleViewBuilder()
                .id(featureToggleId)
                .environmentId(envId)
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(5))
                .build());

        var gapEvent = fakeFeatureToggleStatusChangedBuilder()
                .withEnvironmentId(envId.uuid())
                .withId(featureToggleId.uuid())
                .withStatus(FeatureToggleStatus.ARCHIVED.name())
                .withRevision(Revision.from(5).value())
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
