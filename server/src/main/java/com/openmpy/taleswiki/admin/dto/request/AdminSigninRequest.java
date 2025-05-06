package com.openmpy.taleswiki.admin.dto.request;

public record AdminSigninRequest(
        String nickname,
        String password
) {
}
