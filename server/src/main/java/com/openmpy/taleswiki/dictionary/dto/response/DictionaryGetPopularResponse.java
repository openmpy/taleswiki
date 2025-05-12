package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.util.List;

public record DictionaryGetPopularResponse(List<DictionaryGetPopularItemResponse> dictionaries) {

    public record DictionaryGetPopularItemResponse(Long currentHistoryId, String title, String category) {
    }

    public static DictionaryGetPopularResponse of(final List<Dictionary> dictionaries) {
        final List<DictionaryGetPopularItemResponse> responses = dictionaries.stream()
                .map(it -> new DictionaryGetPopularItemResponse(
                        it.getCurrentHistory().getId(), it.getTitle(), it.getCategory().getValue()))
                .toList();

        return new DictionaryGetPopularResponse(responses);
    }
}
