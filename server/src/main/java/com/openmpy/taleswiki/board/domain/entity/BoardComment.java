package com.openmpy.taleswiki.board.domain.entity;

import com.openmpy.taleswiki.board.domain.BoardAuthor;
import com.openmpy.taleswiki.board.domain.CommentContent;
import com.openmpy.taleswiki.common.domain.ClientIp;
import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.member.domain.entity.Member;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "author", nullable = false))
    private BoardAuthor author;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "content", nullable = false))
    private CommentContent content;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ip", nullable = false))
    private ClientIp ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Builder
    public BoardComment(
            final String author, final String content, final String ip, final Member member, final Board board
    ) {
        this.author = new BoardAuthor(author);
        this.content = new CommentContent(content);
        this.ip = new ClientIp(ip);
        this.member = member;
        this.board = board;
    }

    public static BoardComment save(
            final String author, final String content, final String ip, final Member member, final Board board
    ) {
        return BoardComment.builder()
                .author(author)
                .content(content)
                .ip(ip)
                .member(member)
                .board(board)
                .build();
    }

    public void update(final String content) {
        this.content = new CommentContent(content);
    }

    public String getAuthor() {
        return author.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public String getIp() {
        return ip.getValue();
    }
}
