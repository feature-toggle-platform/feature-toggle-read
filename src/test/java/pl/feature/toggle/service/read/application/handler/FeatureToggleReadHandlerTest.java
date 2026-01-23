package pl.feature.toggle.service.read.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.read.AbstractUnitTest;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleReadUseCase;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleReadHandlerTest extends AbstractUnitTest {

    private FeatureToggleReadUseCase sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.featureToggleReadUseCase(fakeFeatureToggleReadRepository);
    }

    @Test
    @DisplayName("Should return feature toggle view by id")
    void test01() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        insertFeatureToggle(featureToggle);

        // when
        var featureToggleView = sut.getFeatureToggle(featureToggle.id());

        // then
        assertThat(featureToggleView.id()).isEqualTo(featureToggle.id().idAsString());
        assertThat(featureToggleView.value()).isEqualTo(featureToggle.value().asText());
    }

    @Test
    @DisplayName("Should return all feature toggles views")
    void test02() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        var featureToggleSecond = createFeatureToggle("TEST2");
        insertFeatureToggle(featureToggle);
        insertFeatureToggle(featureToggleSecond);

        // when
        var featureToggleViews = sut.getAll();

        // then
        assertThat(featureToggleViews).hasSize(2);
    }

}