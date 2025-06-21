package com.openmpy.taleswiki.board.domain.entity;

import com.openmpy.taleswiki.board.domain.BoardContent;
import com.openmpy.taleswiki.board.domain.BoardTitle;
import com.openmpy.taleswiki.board.domain.BoardView;
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
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false))
    private BoardTitle title;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false))
    private BoardContent content;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "view", nullable = false))
    private BoardView view;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ip", nullable = false))
    private ClientIp ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Board(
            final String title,
            final String content,
            final Long view,
            final String ip,
            final Member member
    ) {
        this.title = new BoardTitle(title);
        this.content = new BoardContent(content);
        this.view = new BoardView(view);
        this.ip = new ClientIp(ip);
        this.member = member;
    }

    public static Board save(final String title, final String content, final String ip, final Member member) {
        return Board.builder()
                .title(title)
                .content(content)
                .view(0L)
                .ip(ip)
                .member(member)
                .build();
    }

    public String getTitle() {
        return title.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public Long getView() {
        return view.getValue();
    }

    public String getIp() {
        return ip.getValue();
    }
}
