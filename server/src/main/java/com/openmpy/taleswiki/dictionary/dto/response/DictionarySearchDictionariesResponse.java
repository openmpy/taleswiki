package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.document.DictionaryDocument;
import java.util.List;

public record DictionarySearchDictionariesResponse(
        List<DictionarySearchDictionariesItemResponse> dictionaries
) {

    public record DictionarySearchDictionariesItemResponse(Long currentHistoryId, String title, String category) {
    }

    public static DictionarySearchDictionariesResponse of(final List<DictionaryDocument> dictionaryDocuments) {
        final List<DictionarySearchDictionariesItemResponse> responses = dictionaryDocuments.stream()
                .map(it -> new DictionarySearchDictionariesItemResponse(
                        it.getCurrentHistoryId(),
                        it.getTitle(),
                        it.getCategory()
                ))
                .toList();

        return new DictionarySearchDictionariesResponse(responses);
    }
}
