package com.openmpy.taleswiki.admin.dto.request;

public record AdminBlacklistSaveRequest(
        String ip,
        String reason
) {
}
