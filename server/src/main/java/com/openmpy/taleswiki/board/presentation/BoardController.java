package com.openmpy.taleswiki.board.presentation;

import com.openmpy.taleswiki.auth.annotation.Login;
import com.openmpy.taleswiki.board.application.BoardService;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardSaveResponse> save(
            @Login final Long memberId,
            final HttpServletRequest servletRequest,
            @RequestBody final BoardSaveRequest request
    ) {
        final BoardSaveResponse response = boardService.save(memberId, servletRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<BoardGetsResponse>> gets(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "30") final int size
    ) {
        final PaginatedResponse<BoardGetsResponse> response = boardService.gets(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardGetResponse> get(
            final HttpServletRequest request,
            @PathVariable final Long boardId
    ) {
        final BoardGetResponse response = boardService.get(request, boardId);
        return ResponseEntity.ok(response);
    }
}
