package com.openmpy.taleswiki.board.presentation;

import com.openmpy.taleswiki.auth.annotation.Login;
import com.openmpy.taleswiki.board.application.BoardService;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
