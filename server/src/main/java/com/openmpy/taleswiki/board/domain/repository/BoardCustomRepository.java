package com.openmpy.taleswiki.board.domain.repository;

import com.openmpy.taleswiki.admin.dto.response.AdminGetBoardsResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.member.domain.entity.Member;

public interface BoardCustomRepository {

    PaginatedResponse<BoardGetsResponse> gets(final int page, final int size);

    BoardGetResponse get(final Long id);

    PaginatedResponse<AdminGetBoardsResponse> getsAdmin(final int page, final int size);

    PaginatedResponse<BoardGetsResponse> getsOfMember(final Member member, final int page, final int size);
}
