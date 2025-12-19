package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleSnapshotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleSnapshotRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleSnapshotRepository sut;

    @Autowired
    protected FeatureToggleReadRepository readRepository;

    @Test
    @DisplayName("Should save a new feature toggle")
    void test01() {
        // given
        var featureToggle = createFeatureToggle("TEST");

        // when
        sut.save(featureToggle);

        // then
        var actual = readRepository.getById(featureToggle.id());
        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Should delete existing feature toggle")
    void test02() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        sut.save(featureToggle);
        assertThat(readRepository.getById(featureToggle.id())).isNotNull();

        // when
        sut.delete(featureToggle.id());

        // then
        var actual = readRepository.getAll();
        assertThat(actual).isEmpty();
    }

}