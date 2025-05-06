package com.openmpy.taleswiki.admin.dto.response;

import java.time.LocalDateTime;

public record AdminGetBlacklistResponse(
        Long blacklistId,
        String ip,
        String reason,
        LocalDateTime createdAt
) {
}
