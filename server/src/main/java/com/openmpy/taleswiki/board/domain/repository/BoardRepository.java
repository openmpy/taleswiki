package com.openmpy.taleswiki.board.domain.repository;

import com.openmpy.taleswiki.board.domain.entity.Board;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {

    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.comments WHERE b.id = :id")
    @Override
    Optional<Board> findById(@Param("id") final Long id);
}
