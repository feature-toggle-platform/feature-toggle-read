package pl.feature.toggle.service.read.infrastructure.out.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.domain.FeatureToggle;
import pl.feature.toggle.service.read.infrastructure.out.redis.dto.FeatureToggleRedisDto;
import pl.feature.toggle.service.read.infrastructure.out.redis.exception.RedisException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
class RedisCacheClient implements CacheClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final Serializer serializer = new Serializer();

    @Override
    public void save(FeatureToggle featureToggle) {
        var dto = FeatureToggleRedisMapper.toDto(featureToggle);
        var jsonValue = serializer.serialize(dto);
        redisTemplate.opsForValue().set(dto.id(), jsonValue);
    }

    @Override
    public Optional<FeatureToggle> read(FeatureToggleId id) {
        var jsonValue = redisTemplate.opsForValue().get(id.idAsString());
        if (jsonValue == null) {
            return Optional.empty();
        }
        var dto = serializer.deserialize(jsonValue, FeatureToggleRedisDto.class);
        return Optional.of(FeatureToggleRedisMapper.toDomain(dto));
    }

    @Override
    public void deleteById(FeatureToggleId featureToggleId) {
        redisTemplate.delete(featureToggleId.idAsString());
    }

    @Override
    public List<FeatureToggle> readAll() {
        Set<String> keys = redisTemplate.keys("*");
        return keys.stream()
                .map(key -> serializer.deserialize(redisTemplate.opsForValue().get(key), FeatureToggleRedisDto.class))
                .map(FeatureToggleRedisMapper::toDomain)
                .toList();
    }

    private static class Serializer {
        private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        String serialize(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (Exception e) {
                throw new RedisException("something went wrong while parsing object to json", e);
            }
        }

        <T> T deserialize(String json, Class<T> clazz) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception e) {
                throw new RedisException("something went wrong while parsing json to object", e);
            }
        }

    }
}
