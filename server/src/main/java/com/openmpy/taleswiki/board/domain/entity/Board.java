package com.openmpy.taleswiki.board.domain.entity;

import com.openmpy.taleswiki.board.domain.BoardAuthor;
import com.openmpy.taleswiki.board.domain.BoardContent;
import com.openmpy.taleswiki.board.domain.BoardTitle;
import com.openmpy.taleswiki.board.domain.BoardView;
import com.openmpy.taleswiki.common.domain.ClientIp;
import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.member.domain.entity.Member;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE board SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
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
    @AttributeOverride(name = "value", column = @Column(name = "author", nullable = false))
    private BoardAuthor author;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "view", nullable = false))
    private BoardView view;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ip", nullable = false))
    private ClientIp ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardUnlike> unlikes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardComment> comments = new ArrayList<>();

    @Column(name = "is_deleted")
    private final Boolean isDeleted = false;

    @Builder
    public Board(
            final String title,
            final String content,
            final String author,
            final Long view,
            final String ip,
            final Member member
    ) {
        this.title = new BoardTitle(title);
        this.content = new BoardContent(content);
        this.author = new BoardAuthor(author);
        this.view = new BoardView(view);
        this.ip = new ClientIp(ip);
        this.member = member;
    }

    public static Board save(
            final String title, final String content, final String author, final String ip, final Member member
    ) {
        return Board.builder()
                .title(title)
                .content(content)
                .author(author)
                .view(0L)
                .ip(ip)
                .member(member)
                .build();
    }

    public void incrementViews(final Long count) {
        this.view.increment(count);
    }

    public void update(final String title, final String content) {
        this.title = new BoardTitle(title);
        this.content = new BoardContent(content);
    }

    public void addLike(final BoardLike like) {
        this.likes.add(like);
    }

    public void addUnlike(final BoardUnlike unlike) {
        this.unlikes.add(unlike);
    }

    public void addComment(final BoardComment comment) {
        this.comments.add(comment);
    }

    public String getTitle() {
        return title.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public String getAuthor() {
        return author.getValue();
    }

    public Long getView() {
        return view.getValue();
    }

    public String getIp() {
        return ip.getValue();
    }
}
