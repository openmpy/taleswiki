package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.application.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DictionaryScheduler {

    private final RedisService redisService;
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
}
