package com.openmpy.taleswiki.dictionary.domain.entity;

import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.dictionary.domain.DictionaryTitle;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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

    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DictionaryHistory> histories = new ArrayList<>();

    public Dictionary(final String title, final DictionaryCategory category, final DictionaryStatus status) {
        this.title = new DictionaryTitle(title);
        this.category = category;
        this.status = status;
    }

    public String getTitle() {
        return title.getValue();
    }
}
