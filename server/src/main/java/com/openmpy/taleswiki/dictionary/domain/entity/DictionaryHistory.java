package com.openmpy.taleswiki.dictionary.domain.entity;

import static com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus.ALL_ACTIVE;

import com.openmpy.taleswiki.common.domain.ClientIp;
import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.dictionary.domain.DictionaryHistoryAuthor;
import com.openmpy.taleswiki.dictionary.domain.DictionaryHistoryContent;
import com.openmpy.taleswiki.dictionary.domain.DictionaryHistorySize;
import com.openmpy.taleswiki.dictionary.domain.DictionaryHistoryVersion;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import jakarta.persistence.AttributeOverride;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DictionaryHistory extends BaseEntity {

    private static final long DEFAULT_VERSION = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "author", nullable = false))
    private DictionaryHistoryAuthor author;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "content", nullable = false))
    private DictionaryHistoryContent content;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "version", nullable = false))
    private DictionaryHistoryVersion version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "size", nullable = false))
    private DictionaryHistorySize size;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ip", nullable = false))
    private ClientIp ip;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DictionaryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary;

    @Builder
    public DictionaryHistory(
            final String author,
            final String content,
            final Long version,
            final Long size,
            final String ip,
            final DictionaryStatus status,
            final Dictionary dictionary
    ) {
        this.author = new DictionaryHistoryAuthor(author);
        this.content = new DictionaryHistoryContent(content);
        this.version = new DictionaryHistoryVersion(version);
        this.size = new DictionaryHistorySize(size);
        this.ip = new ClientIp(ip);
        this.status = status;
        this.dictionary = dictionary;
    }

    public static DictionaryHistory create(
            final String author,
            final String content,
            final Long size,
            final String ip,
            final Dictionary dictionary
    ) {
        return DictionaryHistory.builder()
                .author(author)
                .content(content)
                .version(DEFAULT_VERSION)
                .size(size)
                .ip(ip)
                .status(ALL_ACTIVE)
                .dictionary(dictionary)
                .build();
    }

    public String getAuthor() {
        return author.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public Long getVersion() {
        return version.getValue();
    }

    public Long getSize() {
        return size.getValue();
    }

    public String getIp() {
        return ip.getValue();
    }
}
