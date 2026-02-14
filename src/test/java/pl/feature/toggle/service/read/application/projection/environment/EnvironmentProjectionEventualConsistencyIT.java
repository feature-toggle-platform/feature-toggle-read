package pl.feature.toggle.service.read.application.projection.environment;

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
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged.environmentStatusChangedEventBuilder;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

@Import(EnvironmentProjectionEventualConsistencyIT.SyncAsyncConfig.class)
class EnvironmentProjectionEventualConsistencyIT extends AbstractITTest {

    @Autowired
    private EnvironmentProjection sut;

    @Autowired
    private EnvironmentProjectionRepository environmentProjectionRepository;

    @Autowired
    private EnvironmentQueryRepository environmentQueryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @MockitoBean
    private ConfigurationClient configurationClient;

    @TestConfiguration
    static class SyncAsyncConfig {

        @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
        public Executor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Test
    void should_rebuild_projection_when_env_not_exists_and_status_changed_arrives_before_created() {
        // given
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectView = fakeProjectViewBuilder()
                .name("TEST")
                .id(projectId)
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.initialRevision())
                .build();
        projectProjectionRepository.insert(projectView);

        when(configurationClient.fetchEnvironment(projectId, envId)).thenReturn(fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(envId)
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.from(2))
                .build());

        var outOfOrderStatusChanged = environmentStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(Revision.from(2).value())
                .build();

        var createdLater = environmentCreatedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type("TEST")
                .environmentName("test")
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
                    var actual = environmentQueryRepository.find(projectId, envId).orElseThrow();
                    assertThat(actual.id()).isEqualTo(envId);
                    assertThat(actual.projectId()).isEqualTo(projectId);

                    assertThat(actual.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
                    assertThat(actual.revision()).isEqualTo(Revision.from(2));
                    assertThat(actual.consistent()).isTrue();
                });
    }

    @Test
    void should_rebuild_projection_when_env_exists_and_gap_detected() {
        // given
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectView = fakeProjectViewBuilder()
                .name("TEST")
                .id(projectId)
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.initialRevision())
                .build();
        projectProjectionRepository.insert(projectView);
        environmentProjectionRepository.insert(fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(envId)
                .status(EnvironmentStatus.ACTIVE)
                .build()
        );

        when(configurationClient.fetchEnvironment(projectId, envId)).thenReturn(fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(envId)
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.from(5))
                .build());

        var gapEvent = environmentStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(Revision.from(5).value())
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(gapEvent));

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = environmentQueryRepository.find(projectId, envId).orElseThrow();
                    assertThat(actual.id()).isEqualTo(envId);
                    assertThat(actual.projectId()).isEqualTo(projectId);

                    assertThat(actual.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
                    assertThat(actual.revision()).isEqualTo(Revision.from(5));
                    assertThat(actual.consistent()).isTrue();
                });
    }
}
