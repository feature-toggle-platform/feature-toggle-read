package pl.feature.toggle.service.infrastructure.out.redis;

import pl.feature.toggle.service.application.port.out.FeatureToggleReadRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleSnapshotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration("redisConfig")
class Config {

    @Bean
    FeatureToggleSnapshotRepository featureToggleSnapshotRepository(CacheClient cacheClient) {
        return new FeatureToggleRedisSnapshotRepository(cacheClient);
    }

    @Bean
    FeatureToggleReadRepository featureToggleReadRepository(CacheClient cacheClient) {
        return new FeatureToggleRedisReadRepository(cacheClient);
    }

    @Bean
    CacheClient cacheClient(RedisTemplate<String, String> redisTemplate) {
        return new RedisCacheClient(redisTemplate);
    }

}
