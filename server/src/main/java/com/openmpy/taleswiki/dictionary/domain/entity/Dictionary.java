package com.openmpy.taleswiki.dictionary.domain.entity;

import static com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus.ALL_ACTIVE;

import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.dictionary.domain.DictionaryTitle;
import com.openmpy.taleswiki.dictionary.domain.DictionaryView;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Dictionary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false))
    private DictionaryTitle title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DictionaryCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DictionaryStatus status;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "view", nullable = false))
    private DictionaryView view;

    @Column
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_hitsory_id")
    private DictionaryHistory currentHistory;

    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DictionaryHistory> histories = new ArrayList<>();

    @Builder
    public Dictionary(
            final String title,
            final DictionaryCategory category,
            final DictionaryStatus status,
            final Long view,
            final LocalDateTime modifiedAt
    ) {
        this.title = new DictionaryTitle(title);
        this.category = category;
        this.status = status;
        this.view = new DictionaryView(view);
        this.modifiedAt = modifiedAt;
    }

    public static Dictionary create(final String title, final DictionaryCategory category) {
        return Dictionary.builder()
                .title(title)
                .category(category)
                .status(ALL_ACTIVE)
                .view(0L)
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public void changeStatus(final DictionaryStatus status) {
        this.status = status;
    }

    public void incrementViews(final Long count) {
        this.view.increment(count);
    }

    public void addHistory(final DictionaryHistory history) {
        this.currentHistory = history;
        this.histories.add(history);
    }

    public void updateModifiedAt(final LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getTitle() {
        return title.getValue();
    }

    public Long getView() {
        return view.getValue();
    }
}
