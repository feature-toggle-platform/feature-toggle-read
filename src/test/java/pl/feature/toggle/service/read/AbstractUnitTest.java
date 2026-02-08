package pl.feature.toggle.service.read;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueType;
import pl.feature.toggle.service.read.domain.FeatureToggleView;
import pl.feature.toggle.service.read.infrastructure.FakeAcknowledgment;
import pl.feature.toggle.service.read.infrastructure.out.FakeFeatureToggleReadRepository;
import pl.feature.toggle.service.read.infrastructure.out.FakeFeatureToggleSnapshotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.UUID;

import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;

public abstract class AbstractUnitTest {

    protected FakeFeatureToggleReadRepository fakeFeatureToggleReadRepository;
    protected FakeFeatureToggleSnapshotRepository fakeFeatureToggleSnapshotRepository;
    protected FakeAcknowledgment acknowledgment;

    @BeforeEach
    void setUp() {
        fakeFeatureToggleReadRepository = new FakeFeatureToggleReadRepository();
        fakeFeatureToggleSnapshotRepository = new FakeFeatureToggleSnapshotRepository(fakeFeatureToggleReadRepository);
        acknowledgment = new FakeAcknowledgment();
    }

    @AfterEach
    void tearDown() {
        fakeFeatureToggleReadRepository.clear();
        fakeFeatureToggleSnapshotRepository.clear();
    }


    protected FeatureToggleView insertFeatureToggle(FeatureToggleView featureToggleView) {
        fakeFeatureToggleReadRepository.insert(featureToggleView);
        return featureToggleView;
    }

    protected FeatureToggleView createFeatureToggle(String name) {
        return FeatureToggleView.from(featureToggleCreatedEvent(name));
    }

    private FeatureToggleCreated featureToggleCreatedEvent(String name) {
        return featureToggleCreatedEventBuilder()
                .name(name)
                .id(UUID.randomUUID())
                .description("description")
                .environmentId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(FeatureToggleValueType.BOOLEAN.name())
                .value("true")
                .projectId(UUID.randomUUID())
                .build();
    }

}
