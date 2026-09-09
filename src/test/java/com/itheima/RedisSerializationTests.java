package com.itheima;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.config.CacheConfig;
import com.itheima.config.CacheNames;
import com.itheima.pojo.Dept;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class RedisSerializationTests {
    @Test
    void departmentListCanRoundTripThroughRedisJsonSerializer() {
        CacheConfig cacheConfig = new CacheConfig();
        RedisSerializer<Object> serializer =
                cacheConfig.redisValueSerializer(new ObjectMapper().findAndRegisterModules());
        List<Dept> source = new ArrayList<>();
        source.add(new Dept(1, "研发部", LocalDateTime.of(2026, 9, 10, 1, 0),
                LocalDateTime.of(2026, 9, 10, 1, 30)));

        byte[] bytes = serializer.serialize(source);
        Object restored = serializer.deserialize(bytes);

        assertEquals(source, restored);
    }

    @Test
    void cacheManagerUsesExpectedTimeToLiveValues() {
        CacheConfig cacheConfig = new CacheConfig();
        RedisSerializer<Object> serializer =
                cacheConfig.redisValueSerializer(new ObjectMapper().findAndRegisterModules());
        RedisCacheManager cacheManager = cacheConfig.redisCacheManager(
                mock(RedisConnectionFactory.class), serializer);
        assertNotNull(cacheManager);
        var configurations = cacheConfig.cacheConfigurations(RedisCacheConfiguration.defaultCacheConfig());

        assertEquals(Duration.ofMinutes(10),
                configurations.get(CacheNames.DEPT_LIST).getTtlFunction().getTimeToLive(null, null));
        assertEquals(Duration.ofMinutes(5),
                configurations.get(CacheNames.CLAZZ_LIST).getTtlFunction().getTimeToLive(null, null));
        assertEquals(Duration.ofMinutes(5),
                configurations.get(CacheNames.EMP_DETAIL).getTtlFunction().getTimeToLive(null, null));
        assertEquals(Duration.ofMinutes(2),
                configurations.get(CacheNames.REPORT).getTtlFunction().getTimeToLive(null, null));
    }
}
