package pl.feature.toggle.service.read.infrastructure.out.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.event.processing.api.ProcessedEventRepository;
import pl.feature.toggle.service.read.application.port.out.*;

import javax.sql.DataSource;

@Configuration("databaseConfig")
class Config {

    @Bean
    ProcessedEventRepository processedEventRepository(DSLContext dslContext) {
        return new ProcessedEventJooqRepository(dslContext);
    }

    @Bean
    EnvironmentProjectionRepository environmentProjectionRepository(DSLContext dslContext) {
        return new EnvironmentProjectionJooqRepository(dslContext);
    }

    @Bean
    EnvironmentQueryRepository environmentQueryRepository(DSLContext dslContext) {
        return new EnvironmentQueryJooqRepository(dslContext);
    }

    @Bean
    ProjectProjectionRepository projectProjectionRepository(DSLContext dslContext) {
        return new ProjectProjectionJooqRepository(dslContext);
    }

    @Bean
    ProjectQueryRepository projectQueryRepository(DSLContext dslContext) {
        return new ProjectQueryJooqRepository(dslContext);
    }

    @Bean
    FeatureToggleProjectionRepository featureToggleProjectionRepository(DSLContext dslContext) {
        return new FeatureToggleProjectionJooqRepository(dslContext);
    }

    @Bean
    FeatureToggleQueryRepository featureToggleQueryRepository(DSLContext dslContext) {
        return new FeatureToggleQueryJooqRepository(dslContext);
    }

    @Bean
    DefaultConfiguration jooqConfiguration(DataSource ds) {
        var settings = new Settings()
                .withRenderQuotedNames(RenderQuotedNames.NEVER)
                .withRenderNameCase(RenderNameCase.LOWER)
                .withRenderSchema(false);

        var cfg = new DefaultConfiguration();
        cfg.set(ds);
        cfg.set(SQLDialect.POSTGRES);
        cfg.set(settings);
        return cfg;
    }
}
