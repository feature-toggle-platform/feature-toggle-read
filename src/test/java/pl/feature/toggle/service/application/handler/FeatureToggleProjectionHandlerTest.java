package pl.feature.toggle.service.application.handler;

import com.ftaas.domain.CreatedAt;
import com.ftaas.domain.UpdatedAt;
import com.ftaas.domain.featuretoggle.BooleanFeatureToggleValue;
import com.ftaas.domain.featuretoggle.FeatureToggleId;
import com.ftaas.domain.featuretoggle.FeatureToggleType;
import pl.feature.toggle.service.AbstractUnitTest;
import pl.feature.toggle.service.application.port.in.FeatureToggleProjectionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted.featureToggleDeletedEvent;
import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleProjectionHandlerTest extends AbstractUnitTest {

    private FeatureToggleProjectionUseCase sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.featureToggleProjectionUseCase(fakeFeatureToggleSnapshotRepository);
    }

    @Test
    @DisplayName("Should handle feature toggle created event")
    void test01() {
        // given
        var event = featureToggleCreatedEventBuilder()
                .name("TEST")
                .id(UUID.randomUUID())
                .description("description")
                .environmentId(UUID.randomUUID())
                .createdAt(CreatedAt.now().toLocalDateTime())
                .updatedAt(UpdatedAt.now().toLocalDateTime())
                .type(FeatureToggleType.BOOLEAN.name())
                .value(BooleanFeatureToggleValue.enabled().stringValue())
                .projectId(UUID.randomUUID())
                .build();

        // when
        sut.handle(event);

        // then
        var featureToggle = fakeFeatureToggleReadRepository.getById(FeatureToggleId.create(event.id()));
        assertThat(featureToggle).isNotNull();
        assertThat(featureToggle.name().value()).isEqualTo(event.name());
        assertThat(featureToggle.description().value()).isEqualTo(event.description());
        assertThat(featureToggle.environmentId().uuid()).isEqualTo(event.environmentId());
        assertThat(featureToggle.createdAt().toLocalDateTime()).isEqualTo(event.createdAt());
        assertThat(featureToggle.updatedAt().toLocalDateTime()).isEqualTo(event.updatedAt());
        assertThat(featureToggle.type()).isEqualTo(FeatureToggleType.BOOLEAN);
        assertThat(featureToggle.value().stringValue()).isEqualTo(event.value());
        assertThat(featureToggle.projectId().uuid()).isEqualTo(event.projectId());
    }

    @Test
    @DisplayName("Should handle feature toggle deleted event")
    void test02() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        insertFeatureToggle(featureToggle);
        var event = featureToggleDeletedEvent()
                .id(featureToggle.id().uuid())
                .build();

        assertThat(fakeFeatureToggleReadRepository.getById(featureToggle.id())).isNotNull();

        // when
        sut.handle(event);

        // then
        assertThat(fakeFeatureToggleReadRepository.getById(featureToggle.id())).isNull();
    }

    @Test
    @DisplayName("Should handle feature toggle updated event")
    void test03() {
        // given

        // when

        // then
    }

}