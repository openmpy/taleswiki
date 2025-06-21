package com.openmpy.taleswiki.board.domain.repository;

import com.openmpy.taleswiki.board.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
