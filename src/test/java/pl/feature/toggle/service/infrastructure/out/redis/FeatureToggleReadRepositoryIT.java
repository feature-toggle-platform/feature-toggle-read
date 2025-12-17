package pl.feature.toggle.service.infrastructure.out.redis;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.AbstractITTest;
import pl.feature.toggle.service.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleSnapshotRepository;
import pl.feature.toggle.service.domain.exception.FeatureToggleNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class FeatureToggleReadRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleSnapshotRepository snapshotRepository;

    @Autowired
    protected FeatureToggleReadRepository sut;

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