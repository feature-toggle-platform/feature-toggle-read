package pl.feature.toggle.service;

import com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated;
import com.ftaas.domain.featuretoggle.FeatureToggleType;
import pl.feature.toggle.service.domain.FeatureToggle;
import pl.feature.toggle.service.infrastructure.out.FakeFeatureToggleReadRepository;
import pl.feature.toggle.service.infrastructure.out.FakeFeatureToggleSnapshotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;

public abstract class AbstractUnitTest {

    protected FakeFeatureToggleReadRepository fakeFeatureToggleReadRepository;
    protected FakeFeatureToggleSnapshotRepository fakeFeatureToggleSnapshotRepository;

    @BeforeEach
    void setUp() {
        fakeFeatureToggleReadRepository = new FakeFeatureToggleReadRepository();
        fakeFeatureToggleSnapshotRepository = new FakeFeatureToggleSnapshotRepository(fakeFeatureToggleReadRepository);
    }

    @AfterEach
    void tearDown() {
        fakeFeatureToggleReadRepository.clear();
        fakeFeatureToggleSnapshotRepository.clear();
    }


    protected FeatureToggle insertFeatureToggle(FeatureToggle featureToggle) {
        fakeFeatureToggleReadRepository.insert(featureToggle);
        return featureToggle;
    }

    protected FeatureToggle createFeatureToggle(String name) {
        return FeatureToggle.from(featureToggleCreatedEvent(name));
    }

    private FeatureToggleCreated featureToggleCreatedEvent(String name) {
        return featureToggleCreatedEventBuilder()
                .name(name)
                .id(UUID.randomUUID())
                .description("description")
                .environmentId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(FeatureToggleType.BOOLEAN.name())
                .value("true")
                .projectId(UUID.randomUUID())
                .build();
    }

}
