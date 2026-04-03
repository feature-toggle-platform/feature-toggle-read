package com.configly.read.application.projection.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.read.AbstractITTest;
import com.configly.read.application.port.in.ProjectProjection;
import com.configly.read.application.port.out.ConfigurationClient;
import com.configly.read.application.port.out.ProjectProjectionRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static com.configly.contracts.fake.event.FakeProjectCreatedBuilder.fakeProjectCreatedBuilder;
import static com.configly.contracts.fake.event.FakeProjectStatusChangedBuilder.fakeProjectStatusChangedBuilder;
import static com.configly.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

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

        var outOfOrderStatusChanged = fakeProjectStatusChangedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(Revision.from(2).value())
                .build();

        var createdLater = fakeProjectCreatedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ACTIVE.name())
                .withProjectName("TEST")
                .withCreatedAt(LocalDateTime.now())
                .withUpdatedAt(LocalDateTime.now())
                .withProjectDescription("TEST_DESCRIPTION")
                .withRevision(Revision.initialRevision().value())
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

        var gapEvent = fakeProjectStatusChangedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(Revision.from(5).value())
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
