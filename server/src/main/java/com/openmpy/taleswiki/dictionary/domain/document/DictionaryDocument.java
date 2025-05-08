package com.openmpy.taleswiki.dictionary.domain.document;

import com.openmpy.taleswiki.common.util.CharacterUtil;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "dictionary")
public class DictionaryDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ngram_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ngram_analyzer")
    private String chosung;

    private String category;
    private Long currentHistoryId;

    @Builder
    public DictionaryDocument(
            final Long id, final String title, final String chosung, final String category, final Long currentHistoryId
    ) {
        this.id = id;
        this.title = title;
        this.chosung = chosung;
        this.category = category;
        this.currentHistoryId = currentHistoryId;
    }

    public static DictionaryDocument of(final Dictionary dictionary) {
        final String dictionaryTitle = dictionary.getTitle();

        return DictionaryDocument.builder()
                .id(dictionary.getId())
                .title(dictionaryTitle)
                .chosung(CharacterUtil.extractChosung(dictionaryTitle))
                .category(dictionary.getCategory().getValue())
                .currentHistoryId(dictionary.getCurrentHistory().getId())
                .build();
    }
}
