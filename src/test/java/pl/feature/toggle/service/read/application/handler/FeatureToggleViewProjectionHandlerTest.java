package pl.feature.toggle.service.read.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.read.AbstractUnitTest;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted.featureToggleDeletedEventBuilder;

class FeatureToggleViewProjectionHandlerTest extends AbstractUnitTest {

    private FeatureToggleProjection sut;

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
                .type(FeatureToggleValueType.BOOLEAN.name())
                .value(FeatureToggleValueBuilder.bool(true).asText())
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
        assertThat(featureToggle.value().type()).isEqualTo(FeatureToggleValueType.BOOLEAN);
        assertThat(featureToggle.value().asText()).isEqualTo(event.value());
        assertThat(featureToggle.projectId().uuid()).isEqualTo(event.projectId());
    }

    @Test
    @DisplayName("Should handle feature toggle deleted event")
    void test02() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        insertFeatureToggle(featureToggle);
        var event = featureToggleDeletedEventBuilder()
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