package pl.feature.toggle.service.read.infrastructure.in.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.contracts.shared.EventProcessor;
import pl.feature.toggle.service.read.AbstractUnitTest;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted.featureToggleDeletedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;

class KafkaEventConsumerTest extends AbstractUnitTest {

    private KafkaEventConsumer sut;
    private EventProcessor eventProcessor;

    @BeforeEach
    void setUp() {
        var projectEnvProjectionUseCase = mock(FeatureToggleProjection.class);
        eventProcessor = mock(IdempotentEventProcessor.class);
        sut = new KafkaEventConsumer(projectEnvProjectionUseCase, eventProcessor);
    }

    @Test
    @DisplayName("Should handle feature toggle created event")
    void test01() {
        // given
        var event = featureToggleCreatedEventBuilder()
                .build();

        // when
        sut.handle(event, acknowledgment);

        // then
        verify(eventProcessor).process(eq(event), any(), any());
    }

    @Test
    @DisplayName("Should handle feature toggle deleted event")
    void test02() {
        // given
        var event = featureToggleDeletedEventBuilder()
                .build();

        // when
        sut.handle(event, acknowledgment);

        // then
        verify(eventProcessor).process(eq(event), any(), any());
    }

    @Test
    @DisplayName("Should handle feature toggle updated event")
    void test03() {
        // given
        var event = featureToggleUpdatedEventBuilder()
                .build();

        // when
        sut.handle(event, acknowledgment);

        // then
        verify(eventProcessor).process(eq(event), any(), any());
    }
}