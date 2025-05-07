package com.openmpy.taleswiki.common.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean acquireLock(final String key, final String value, long timeoutInMillis) {
        final Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeoutInMillis));
        return Boolean.TRUE.equals(success);
    }
}
