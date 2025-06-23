package com.openmpy.taleswiki.board.domain.repository;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.BoardLike;
import com.openmpy.taleswiki.board.domain.entity.BoardUnlike;
import com.openmpy.taleswiki.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardUnlikeRepository extends JpaRepository<BoardUnlike, Long> {

    boolean existsByBoardAndMember(final Board board, final Member member);
}
