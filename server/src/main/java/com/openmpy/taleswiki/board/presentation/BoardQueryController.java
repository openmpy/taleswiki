package com.openmpy.taleswiki.board.presentation;

import com.openmpy.taleswiki.auth.annotation.Login;
import com.openmpy.taleswiki.board.application.BoardQueryService;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@RestController
public class BoardQueryController {

    private final BoardQueryService boardQueryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<BoardGetsResponse>> gets(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "30") final int size
    ) {
        final PaginatedResponse<BoardGetsResponse> response = boardQueryService.gets(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardGetResponse> get(
            final HttpServletRequest request,
            @PathVariable final Long boardId
    ) {
        final BoardGetResponse response = boardQueryService.get(request, boardId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member")
    public ResponseEntity<PaginatedResponse<BoardGetsResponse>> getsOfMember(
            @Login final Long memberId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final PaginatedResponse<BoardGetsResponse> response = boardQueryService.getsOfMember(memberId, page, size);
        return ResponseEntity.ok(response);
    }
}
