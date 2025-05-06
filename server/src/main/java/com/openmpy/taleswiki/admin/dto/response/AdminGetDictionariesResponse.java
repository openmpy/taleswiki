package com.openmpy.taleswiki.admin.dto.response;

public record AdminGetDictionariesResponse(
        Long dictionaryId,
        String title,
        String category,
        String status
) {
}
