package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import java.time.LocalDateTime;

public record DictionaryHistoryResponse(
        Long dictionaryId,
        Long dictionaryHistoryId,
        String title,
        String content,
        String status,
        String historyStatus,
        LocalDateTime createdAt
) {

    public static DictionaryHistoryResponse of(final DictionaryHistory dictionaryHistory) {
        final Dictionary dictionary = dictionaryHistory.getDictionary();

        if (dictionary.getStatus() == DictionaryStatus.HIDDEN) {
            return new DictionaryHistoryResponse(
                    dictionary.getId(),
                    dictionaryHistory.getId(),
                    dictionary.getTitle(),
                    null,
                    dictionary.getStatus().name(),
                    dictionaryHistory.getStatus().name(),
                    dictionary.getCreatedAt()
            );
        }
        if (dictionaryHistory.getStatus() == DictionaryStatus.HIDDEN) {
            return new DictionaryHistoryResponse(
                    dictionary.getId(),
                    dictionaryHistory.getId(),
                    dictionary.getTitle(),
                    null,
                    dictionary.getStatus().name(),
                    dictionaryHistory.getStatus().name(),
                    dictionaryHistory.getCreatedAt()
            );
        }
        return new DictionaryHistoryResponse(
                dictionary.getId(),
                dictionaryHistory.getId(),
                dictionary.getTitle(),
                dictionaryHistory.getContent(),
                dictionary.getStatus().name(),
                dictionaryHistory.getStatus().name(),
                dictionaryHistory.getCreatedAt()
        );
    }
}
