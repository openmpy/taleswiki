package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import java.util.List;
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
    private final DictionaryRepository dictionaryRepository;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void syncViewCountsToDataBase() {
        redisService.getKeys("dictionary-view:*").forEach(key -> {
            final String[] keys = key.split(":");
            final Long dictionaryId = Long.parseLong(keys[1]);

            final Object value = redisService.get(key);
            final Long count = value != null ? Long.parseLong(value.toString()) : null;

            if (count != null) {
                dictionaryCommandService.incrementViews(dictionaryId, count);
            }
            redisService.delete(key);
        });
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void decayPopularDictionaryScores() {
        final Set<Object> dictionaries = redisService.range("popular_dictionaries", 0, -1);

        for (final Object dictionary : dictionaries) {
            final Double score = redisService.score("popular_dictionaries", dictionary);

            if (score == null) {
                continue;
            }

            final double decayAmount = 0.1;
            final double newScore = score - decayAmount;

            if (newScore <= 0) {
                redisService.remove("popular_dictionaries", dictionary);
                continue;
            }

            redisService.add("popular_dictionaries", dictionary, newScore);
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cacheAllDictionaryIds() {
        final List<Long> ids = dictionaryRepository.findAllByIds();
        redisService.delete("dictionary:ids");
        redisTemplate.opsForSet().add("dictionary:ids", ids.toArray());
    }
}
