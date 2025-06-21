package com.openmpy.taleswiki.board.application;

import com.openmpy.taleswiki.common.application.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardScheduler {

    private final RedisService redisService;
    private final BoardService boardService;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void syncViewCountsToDataBase() {
        redisService.getKeys("board-view:*").forEach(key -> {
            final String[] keys = key.split(":");
            final Long boardId = Long.parseLong(keys[1]);

            final Object value = redisService.get(key);
            final Long count = value != null ? Long.parseLong(value.toString()) : null;

            if (count != null) {
                boardService.incrementViews(boardId, count);
            }
            redisService.delete(key);
        });
    }
}
