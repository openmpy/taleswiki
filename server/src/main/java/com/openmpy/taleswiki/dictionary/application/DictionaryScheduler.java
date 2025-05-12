package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.application.RedisService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DictionaryScheduler {

    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DictionaryCommandService dictionaryCommandService;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void syncViewCountsToDataBase() {
        redisService.getKeys("dictionary-view:*").forEach(key -> {
            final String[] keys = key.split(":");
            final Long dictionaryId = Long.parseLong(keys[1]);

            final Object value = redisService.get(keys[0] + ":" + dictionaryId);
            final Long count = value != null ? Long.parseLong(value.toString()) : null;

            if (count != null) {
                dictionaryCommandService.incrementViews(dictionaryId, count);
                redisService.delete(key);
            }
        });
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void decayPopularDictionaryScores() {
        final Set<Object> dictionaries = redisTemplate.opsForZSet().range("popular_dictionaries", 0, -1);

        for (final Object dictionary : dictionaries) {
            final Double score = redisTemplate.opsForZSet().score("popular_dictionaries", dictionary);

            if (score == null) {
                continue;
            }

            final double decayAmount = 0.1;
            final double newScore = score - decayAmount;

            if (newScore <= 0) {
                redisTemplate.opsForZSet().remove("popular_dictionaries", dictionary);
                break;
            }

            redisTemplate.opsForZSet().add("popular_dictionaries", dictionary, newScore);
        }
    }
}
