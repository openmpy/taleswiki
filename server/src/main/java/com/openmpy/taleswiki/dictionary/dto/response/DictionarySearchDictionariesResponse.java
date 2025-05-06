package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.util.List;

public record DictionarySearchDictionariesResponse(
        List<DictionarySearchDictionariesItemResponse> dictionaries
) {

    public record DictionarySearchDictionariesItemResponse(Long currentHistoryId, String title, String category) {
    }

    public static DictionarySearchDictionariesResponse of(final List<Dictionary> dictionaries) {
        final List<DictionarySearchDictionariesItemResponse> responses = dictionaries.stream()
                .map(it -> new DictionarySearchDictionariesItemResponse(
                        it.getCurrentHistory().getId(),
                        it.getTitle(),
                        it.getCategory().getValue()
                ))
                .toList();

        return new DictionarySearchDictionariesResponse(responses);
    }
}
