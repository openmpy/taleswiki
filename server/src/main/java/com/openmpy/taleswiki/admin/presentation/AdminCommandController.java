package com.openmpy.taleswiki.admin.presentation;

import com.openmpy.taleswiki.admin.application.AdminCommandService;
import com.openmpy.taleswiki.admin.dto.request.AdminBlacklistSaveRequest;
import com.openmpy.taleswiki.admin.dto.request.AdminSigninRequest;
import com.openmpy.taleswiki.common.properties.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@RestController
public class AdminCommandController {

    private final AdminCommandService adminCommandService;
    private final CookieProperties cookieProperties;

    @PostMapping("/signin")
    public ResponseEntity<Void> signin(@RequestBody final AdminSigninRequest request) {
        final String token = adminCommandService.signin(request);
        final ResponseCookie cookie = createCookie(token);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @PatchMapping("/dictionaries/{dictionaryId}")
    public ResponseEntity<Void> changeDictionaryStatus(
            @CookieValue("admin_token") final String token,
            @PathVariable final Long dictionaryId,
            @RequestParam(value = "status") final String status
    ) {
        adminCommandService.changeDictionaryStatus(token, dictionaryId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/dictionaries/{dictionaryId}")
    public ResponseEntity<Void> delete(
            @CookieValue("admin_token") final String token,
            @PathVariable final Long dictionaryId
    ) {
        adminCommandService.delete(token, dictionaryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/dictionaries/histories/{dictionaryHistoriesId}")
    public ResponseEntity<Void> changeDictionaryHistoryStatus(
            @CookieValue("admin_token") final String token,
            @PathVariable final Long dictionaryHistoriesId,
            @RequestParam(value = "status") final String status
    ) {
        adminCommandService.changeDictionaryHistoryStatus(token, dictionaryHistoriesId, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/blacklist")
    public ResponseEntity<Void> saveBlacklist(
            @CookieValue("admin_token") final String token,
            @RequestBody final AdminBlacklistSaveRequest request
    ) {
        adminCommandService.saveBlacklist(token, request);
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie createCookie(final String token) {
        return ResponseCookie.from("admin_token", token)
                .httpOnly(cookieProperties.httpOnly())
                .secure(cookieProperties.secure())
                .domain(cookieProperties.domain())
                .path(cookieProperties.path())
                .sameSite(cookieProperties.sameSite())
                .maxAge(cookieProperties.maxAge())
                .build();
    }
}
