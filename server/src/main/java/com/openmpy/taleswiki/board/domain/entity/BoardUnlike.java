package com.openmpy.taleswiki.board.domain.entity;

import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.member.domain.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"board_id", "member_id"})
})
@Entity
public class BoardUnlike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public BoardUnlike(final Board board, final Member member) {
        this.board = board;
        this.member = member;
    }

    public static BoardUnlike save(final Board board, final Member member) {
        return BoardUnlike.builder()
                .board(board)
                .member(member)
                .build();
    }
}
