package com.openmpy.taleswiki.board.dto.request;

public record CommentSaveRequest(Long parentId, String content) {
}
