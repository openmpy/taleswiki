package com.openmpy.taleswiki.admin.dto.response;

public record AdminGetDictionariesHistoriesResponse(
        Long dictionaryHistoryId,
        String title,
        String category,
        String dictionaryHistoryStatus
) {
}
