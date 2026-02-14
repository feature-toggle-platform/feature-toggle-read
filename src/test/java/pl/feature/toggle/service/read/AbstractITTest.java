package pl.feature.toggle.service.read;

import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

import static pl.feature.ftaas.jooq.tables.EnvironmentView.ENVIRONMENT_VIEW;
import static pl.feature.ftaas.jooq.tables.FeatureToggleView.FEATURE_TOGGLE_VIEW;
import static pl.feature.ftaas.jooq.tables.ProcessedEvents.PROCESSED_EVENTS;
import static pl.feature.ftaas.jooq.tables.ProjectView.PROJECT_VIEW;


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractITTest {

    @DynamicPropertySource
    static void pgProps(DynamicPropertyRegistry r) {
        var pg = PostgresContainer.getInstance();
        var redis = RedisContainer.getInstance();
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
        r.add("spring.data.redis.host", redis::getHost);
        r.add("spring.data.redis.port", redis.getMappedPort(RedisContainer.PORT)::toString);
    }

    @Autowired
    private DSLContext dslContext;

    protected static final EnvironmentId ENVIRONMENT_ID = EnvironmentId.create();
    protected static final ProjectId PROJECT_ID = ProjectId.create();

    @AfterEach
    void tearDown() {
        clearFeatureToggles();
        clearEnvironments();
        clearProjects();
        clearProcessedEvents();
    }

    private void clearFeatureToggles() {
        dslContext.deleteFrom(FEATURE_TOGGLE_VIEW).execute();
    }

    private void clearEnvironments() {
        dslContext.deleteFrom(ENVIRONMENT_VIEW).execute();
    }

    private void clearProjects() {
        dslContext.deleteFrom(PROJECT_VIEW).execute();
    }

    private void clearProcessedEvents() {
        dslContext.deleteFrom(PROCESSED_EVENTS).execute();
    }
}
