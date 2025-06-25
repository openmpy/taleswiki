package com.openmpy.taleswiki.board.domain.repository;

import com.openmpy.taleswiki.board.domain.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
}
