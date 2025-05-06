package com.openmpy.taleswiki.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginatedResponse<T>(
        List<T> content,        // 현재 페이지의 데이터 목록
        long totalElements,     // 전체 데이터의 총 개수
        int totalPages,         // 전체 페이지 수
        int size,               // 페이지당 데이터 개수
        int number,             // 현재 페이지 번호 (0부터 시작)
        boolean isFirst,        // 첫 페이지 여부
        boolean isLast          // 마지막 페이지 여부
) {

    public static <T> PaginatedResponse<T> of(final Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
    }
}
