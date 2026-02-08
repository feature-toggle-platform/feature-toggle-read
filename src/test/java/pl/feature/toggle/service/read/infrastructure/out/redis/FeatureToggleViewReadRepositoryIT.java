package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.domain.exception.FeatureToggleNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class FeatureToggleViewReadRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleProjectionRepository snapshotRepository;

    @Autowired
    protected FeatureToggleQueryRepository sut;

    @Test
    @DisplayName("Should return all toggles")
    void test01() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        snapshotRepository.save(featureToggle);
        var featureToggleSecond = createFeatureToggle("TEST2");
        snapshotRepository.save(featureToggleSecond);

        // when
        var all = sut.getAll();

        // then
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return toggle by id if exists")
    void test02() {
        // given
        var featureToggle = createFeatureToggle("TEST");
        snapshotRepository.save(featureToggle);

        // when
        var actual = sut.getById(featureToggle.id());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(featureToggle);
    }

    @Test
    @DisplayName("Should throw exception if toggle does not exist by id")
    void test03() {
        // given && when
        var exception = catchException(() -> sut.getById(FeatureToggleId.create()));

        // then
        assertThat(exception).isInstanceOf(FeatureToggleNotFoundException.class);
    }
}