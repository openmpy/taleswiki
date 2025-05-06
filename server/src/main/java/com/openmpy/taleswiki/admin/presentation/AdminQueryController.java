package com.openmpy.taleswiki.admin.presentation;

import com.openmpy.taleswiki.admin.application.AdminQueryService;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@RestController
public class AdminQueryController {

    private final AdminQueryService adminQueryService;

    @GetMapping("/me")
    public ResponseEntity<Void> me(@CookieValue("admin_token") final String token) {
        adminQueryService.me(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dictionaries")
    public ResponseEntity<PaginatedResponse<AdminGetDictionariesResponse>> getDictionaries(
            @CookieValue("admin_token") final String token,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final PaginatedResponse<AdminGetDictionariesResponse> response = adminQueryService.getDictionaries(
                token, page, size
        );
        return ResponseEntity.ok(response);
    }
}
