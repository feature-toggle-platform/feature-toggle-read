package pl.feature.toggle.service.read.infrastructure.out.db;

import pl.feature.toggle.service.read.application.port.out.ProcessedEventRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration("databaseConfig")
class Config {

    @Bean
    ProcessedEventRepository processedEventRepository(DSLContext dslContext) {
        return new ProcessedEventJooqRepository(dslContext);
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
