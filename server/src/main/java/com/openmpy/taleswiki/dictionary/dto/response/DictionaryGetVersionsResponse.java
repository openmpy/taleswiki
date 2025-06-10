package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import java.util.List;

public record DictionaryGetVersionsResponse(
        List<DictionaryGetVersionsItemResponse> versions
) {

    public record DictionaryGetVersionsItemResponse(
            Long version
    ) {
    }

    public static DictionaryGetVersionsResponse of(final Dictionary dictionary) {
        final List<DictionaryHistory> histories = dictionary.getHistories();
        final List<DictionaryGetVersionsItemResponse> responses = histories.stream()
                .map(it -> new DictionaryGetVersionsItemResponse(it.getVersion()))
                .toList();

        return new DictionaryGetVersionsResponse(responses);
    }
}
