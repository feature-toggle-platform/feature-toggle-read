package pl.feature.toggle.service.read.application.projection.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static pl.feature.toggle.service.contracts.event.project.ProjectCreated.projectCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged.projectStatusChangedEventBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

@Import(ProjectProjectionEventualConsistencyIT.SyncAsyncConfig.class)
class ProjectProjectionEventualConsistencyIT extends AbstractITTest {

    @Autowired
    private ProjectProjection sut;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @Autowired
    private ProjectQueryRepository queryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoBean
    private ConfigurationClient configurationClient;
    @Autowired
    private ProjectQueryRepository projectQueryRepository;

    @TestConfiguration
    static class SyncAsyncConfig {

        @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
        public Executor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }


    @Test
    void should_rebuild_projection_when_project_not_exists_and_status_changed_arrives_before_created() {
        // given
        var projectId = ProjectId.create();

        when(configurationClient.fetchProject(projectId)).thenReturn(fakeProjectViewBuilder()
                .status(ProjectStatus.ARCHIVED)
                .id(projectId)
                .revision(Revision.from(2))
                .build());

        var outOfOrderStatusChanged = projectStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(Revision.from(2).value())
                .build();

        var createdLater = projectCreatedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ACTIVE.name())
                .projectName("TEST")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectDescription("TEST_DESCRIPTION")
                .revision(Revision.initialRevision().value())
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(outOfOrderStatusChanged));
        sut.handle(createdLater);

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = projectQueryRepository.find(projectId).orElseThrow();
                    assertThat(actual.id()).isEqualTo(projectId);

                    assertThat(actual.status()).isEqualTo(ProjectStatus.ARCHIVED);
                    assertThat(actual.revision()).isEqualTo(Revision.from(2));
                    assertThat(actual.consistent()).isTrue();
                });
    }


    @Test
    void should_rebuild_projection_when_project_exists_and_gap_detected() {
        // given
        var projectId = ProjectId.create();
        var existingProject = fakeProjectViewBuilder()
                .status(ProjectStatus.ACTIVE)
                .id(projectId)
                .revision(Revision.initialRevision())
                .build();

        projectProjectionRepository.insert(existingProject);

        when(configurationClient.fetchProject(projectId)).thenReturn(fakeProjectViewBuilder()
                .status(ProjectStatus.ARCHIVED)
                .id(projectId)
                .revision(Revision.from(5))
                .build());

        var gapEvent = projectStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(Revision.from(5).value())
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(gapEvent));

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = projectQueryRepository.find(projectId).orElseThrow();
                    assertThat(actual.id()).isEqualTo(projectId);

                    assertThat(actual.status()).isEqualTo(ProjectStatus.ARCHIVED);
                    assertThat(actual.revision()).isEqualTo(Revision.from(5));
                    assertThat(actual.consistent()).isTrue();
                });
    }
}
