package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.time.LocalDateTime;
import java.util.List;

public record DictionaryGetTop10Response(List<DictionaryGetTop10ItemResponse> dictionaries) {

    public record DictionaryGetTop10ItemResponse(
            Long currentHistoryId, String title, String category, LocalDateTime createdAt
    ) {
    }

    public static DictionaryGetTop10Response of(final List<Dictionary> dictionaries) {
        final List<DictionaryGetTop10ItemResponse> responses = dictionaries.stream()
                .map(it -> new DictionaryGetTop10ItemResponse(
                        it.getCurrentHistory().getId(),
                        it.getTitle(),
                        it.getCategory().getValue(),
                        it.getCurrentHistory().getCreatedAt()
                ))
                .toList();

        return new DictionaryGetTop10Response(responses);
    }
}
