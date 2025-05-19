package com.openmpy.taleswiki.common.application;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void destroyAllKeys() {
        final Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Set<String> getKeys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void increment(final String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }

    public boolean setIfAbsent(final String key, final String value, final Duration duration) {
        final Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, duration);
        return Boolean.TRUE.equals(success);
    }

    public Set<TypedTuple<Object>> reverseRangeWithScores(final String key, final long start, final long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    public Double incrementScore(final String key, final Object value, final double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    public Set<Object> range(final String key, final long start, final long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Double score(final String key, final Object object) {
        return redisTemplate.opsForZSet().score(key, object);
    }

    public Long remove(final String key, final Object object) {
        return redisTemplate.opsForZSet().remove(key, object);
    }

    public Boolean add(final String key, final Object value, final double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
}
