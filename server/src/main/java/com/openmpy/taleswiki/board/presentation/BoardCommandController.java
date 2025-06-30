package com.openmpy.taleswiki.board.presentation;

import com.openmpy.taleswiki.auth.annotation.Login;
import com.openmpy.taleswiki.board.application.BoardCommandService;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.request.BoardUpdateRequest;
import com.openmpy.taleswiki.board.dto.request.CommentSaveRequest;
import com.openmpy.taleswiki.board.dto.request.CommentUpdateRequest;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.board.dto.response.BoardUpdateResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@RestController
public class BoardCommandController {

    private final BoardCommandService boardCommandService;

    @PostMapping
    public ResponseEntity<BoardSaveResponse> save(
            @Login final Long memberId,
            final HttpServletRequest servletRequest,
            @RequestBody final BoardSaveRequest request
    ) {
        final BoardSaveResponse response = boardCommandService.save(memberId, servletRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@Login final Long memberId, @PathVariable final Long boardId) {
        boardCommandService.delete(memberId, boardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardUpdateResponse> update(
            @Login final Long memberId,
            @PathVariable final Long boardId,
            @RequestBody final BoardUpdateRequest request
    ) {
        final BoardUpdateResponse response = boardCommandService.update(memberId, boardId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/like/{boardId}")
    public ResponseEntity<Void> like(@Login final Long memberId, @PathVariable final Long boardId) {
        boardCommandService.like(memberId, boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unlike/{boardId}")
    public ResponseEntity<Void> unlike(@Login final Long memberId, @PathVariable final Long boardId) {
        boardCommandService.unlike(memberId, boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{boardId}")
    public ResponseEntity<Void> saveComment(
            @Login final Long memberId,
            @PathVariable final Long boardId,
            final HttpServletRequest servletRequest,
            @RequestBody final CommentSaveRequest request
    ) {
        boardCommandService.saveComment(memberId, boardId, servletRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @Login final Long memberId,
            @PathVariable final Long commentId,
            @RequestBody final CommentUpdateRequest request
    ) {
        boardCommandService.updateComment(memberId, commentId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Login final Long memberId,
            @PathVariable final Long commentId
    ) {
        boardCommandService.deleteComment(memberId, commentId);
        return ResponseEntity.noContent().build();
    }
}
