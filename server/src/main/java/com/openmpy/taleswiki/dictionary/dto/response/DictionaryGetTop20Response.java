package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.time.LocalDateTime;
import java.util.List;

public record DictionaryGetTop20Response(List<DictionaryGetTop20ItemResponse> dictionaries) {

    public record DictionaryGetTop20ItemResponse(
            Long currentHistoryId, String title, String category, LocalDateTime createdAt
    ) {
    }

    public static DictionaryGetTop20Response of(final List<Dictionary> dictionaries) {
        final List<DictionaryGetTop20ItemResponse> responses = dictionaries.stream()
                .map(it -> new DictionaryGetTop20ItemResponse(
                        it.getCurrentHistory().getId(),
                        it.getTitle(),
                        it.getCategory().getValue(),
                        it.getCurrentHistory().getCreatedAt()
                ))
                .toList();

        return new DictionaryGetTop20Response(responses);
    }
}
