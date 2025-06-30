package com.openmpy.taleswiki.board.application;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.common.application.RedisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardScheduler {

    private static final int LIMIT_BOARD_UNLIKES_COUNT = -10;

    private final RedisService redisService;
    private final BoardCommandService boardCommandService;
    private final BoardRepository boardRepository;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void syncViewCountsToDataBase() {
        redisService.getKeys("board-view:*").forEach(key -> {
            final String[] keys = key.split(":");
            final Long boardId = Long.parseLong(keys[1]);

            final Object value = redisService.get(key);
            final Long count = value != null ? Long.parseLong(value.toString()) : null;

            if (count != null) {
                boardCommandService.incrementViews(boardId, count);
            }
            redisService.delete(key);
        });
    }

    @Scheduled(cron = "0 0 0 * * 0")
    @Transactional
    public void deleteUnlikeBoard() {
        final List<Board> boards = boardRepository.findAll();

        for (final Board board : boards) {
            final int size = board.getLikes().size() - board.getUnlikes().size();

            if (size <= LIMIT_BOARD_UNLIKES_COUNT) {
                boardRepository.delete(board);
            }
        }
    }
}
