package pl.feature.toggle.service.read.infrastructure.out.redis;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.*;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import pl.feature.toggle.service.read.infrastructure.out.redis.dto.FeatureToggleRedisDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleRedisMapperTest {

    @Test
    void toDto() {
        // given
        var now = Instant.parse("2025-12-13T10:15:30Z");
        var later = Instant.parse("2025-12-13T10:16:30Z");
        var featureToggleId = FeatureToggleId.create();
        var projectId = ProjectId.create();
        var environmentId = EnvironmentId.create();

        var domain = new FeatureToggle(
                featureToggleId,
                projectId,
                environmentId,
                FeatureToggleName.create("new-ui"),
                FeatureToggleDescription.create("toggle for new UI"),
                FeatureToggleValueBuilder.bool(true),
                CreatedAt.of(now),
                UpdatedAt.of(later)
        );

        // when
        var dto = FeatureToggleRedisMapper.toDto(domain);

        // then
        assertThat(dto.id()).isEqualTo(featureToggleId.idAsString());
        assertThat(dto.projectId()).isEqualTo(projectId.idAsString());
        assertThat(dto.environmentId()).isEqualTo(environmentId.idAsString());
        assertThat(dto.name()).isEqualTo("new-ui");
        assertThat(dto.description()).isEqualTo("toggle for new UI");
        assertThat(dto.type()).isEqualTo("BOOLEAN");
        assertThat(dto.value()).isEqualTo("TRUE");
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.updatedAt()).isEqualTo(later);
    }

    @Test
    void toDomain() {
        // given
        var createdAt = Instant.parse("2025-12-13T10:15:30Z");
        var updatedAt = Instant.parse("2025-12-13T10:16:30Z");
        var featureToggleId = FeatureToggleId.create();
        var projectId = ProjectId.create();
        var environmentId = EnvironmentId.create();

        var dto = new FeatureToggleRedisDto(
                featureToggleId.idAsString(),
                projectId.idAsString(),
                environmentId.idAsString(),
                "new-ui",
                "toggle for new UI",
                "BOOLEAN",
                "TRUE",
                createdAt,
                updatedAt
        );

        // when
        var domain = FeatureToggleRedisMapper.toDomain(dto);

        // then
        assertThat(domain.id().idAsString()).isEqualTo(featureToggleId.idAsString());
        assertThat(domain.projectId().idAsString()).isEqualTo(projectId.idAsString());
        assertThat(domain.environmentId().idAsString()).isEqualTo(environmentId.idAsString());
        assertThat(domain.name().value()).isEqualTo("new-ui");
        assertThat(domain.description().value()).isEqualTo("toggle for new UI");
        assertThat(domain.value().type()).isEqualTo(FeatureToggleType.BOOLEAN);

        assertThat(domain.value().asText()).isEqualTo("TRUE");

        assertThat(domain.createdAt().timestamp()).isEqualTo(createdAt);
        assertThat(domain.updatedAt().timestamp()).isEqualTo(updatedAt);
    }

}