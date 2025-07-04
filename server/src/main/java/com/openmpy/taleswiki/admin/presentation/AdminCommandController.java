package com.openmpy.taleswiki.admin.presentation;

import com.openmpy.taleswiki.admin.application.AdminCommandService;
import com.openmpy.taleswiki.admin.dto.request.AdminBlacklistSaveRequest;
import com.openmpy.taleswiki.auth.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("/dictionaries/{dictionaryId}")
    public ResponseEntity<Void> changeDictionaryStatus(
            @Login final Long memberId,
            @PathVariable final Long dictionaryId,
            @RequestParam(value = "status") final String status
    ) {
        adminCommandService.changeDictionaryStatus(memberId, dictionaryId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/dictionaries/{dictionaryId}")
    public ResponseEntity<Void> delete(
            @Login final Long memberId,
            @PathVariable final Long dictionaryId
    ) {
        adminCommandService.delete(memberId, dictionaryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/dictionaries/histories/{dictionaryHistoriesId}")
    public ResponseEntity<Void> changeDictionaryHistoryStatus(
            @Login final Long memberId,
            @PathVariable final Long dictionaryHistoriesId,
            @RequestParam(value = "status") final String status
    ) {
        adminCommandService.changeDictionaryHistoryStatus(memberId, dictionaryHistoriesId, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/blacklist")
    public ResponseEntity<Void> saveBlacklist(
            @Login final Long memberId,
            @RequestBody final AdminBlacklistSaveRequest request
    ) {
        adminCommandService.saveBlacklist(memberId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/blacklist/{blacklistId}")
    public ResponseEntity<Void> deleteBlacklist(
            @Login final Long memberId,
            @PathVariable final Long blacklistId
    ) {
        adminCommandService.deleteBlacklist(memberId, blacklistId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @Login final Long memberId,
            @PathVariable final Long chatId
    ) {
        adminCommandService.deleteChat(memberId, chatId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@Login final Long memberId, @PathVariable final Long boardId) {
        adminCommandService.deleteBoard(memberId, boardId);
        return ResponseEntity.noContent().build();
    }
}
