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

import static pl.feature.ftaas.jooq.tables.ProcessedEvents.PROCESSED_EVENTS;


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

    @AfterEach
    void tearDown() {
        clearProcessedEvents();
    }

    protected void clearProcessedEvents() {
        dslContext.deleteFrom(PROCESSED_EVENTS).execute();
    }
}
