package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import java.time.LocalDateTime;
import java.util.List;

public record DictionaryGetHistoriesResponse(
        String title,
        List<DictionaryGetHistoriesItemResponse> histories
) {

    public record DictionaryGetHistoriesItemResponse(
            Long dictionaryHistoryId,
            Long version,
            LocalDateTime createdAt,
            Long size,
            String author,
            String dictionaryHistoryStatus
    ) {
    }

    public static DictionaryGetHistoriesResponse of(final String title, final List<DictionaryHistory> histories) {
        final List<DictionaryGetHistoriesItemResponse> responses = histories.stream()
                .map(it -> new DictionaryGetHistoriesItemResponse(
                        it.getId(),
                        it.getVersion(),
                        it.getCreatedAt(),
                        it.getSize(),
                        it.getAuthor(),
                        it.getStatus().name()
                ))
                .sorted((a, b) -> b.version().compareTo(a.version()))
                .toList();

        return new DictionaryGetHistoriesResponse(title, responses);
    }
}
