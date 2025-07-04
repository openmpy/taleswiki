package com.openmpy.taleswiki.admin.application;

import com.openmpy.taleswiki.admin.domain.entity.Blacklist;
import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.admin.dto.response.AdminGetBlacklistResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetBoardsResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetChatsResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesHistoriesResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesResponse;
import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.chat.domain.entity.ChatMessage;
import com.openmpy.taleswiki.chat.domain.repository.ChatMessageRepository;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryHistoryRepository;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminQueryService {

    private final MemberService memberService;
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryHistoryRepository dictionaryHistoryRepository;
    private final BlacklistRepository blacklistRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public PaginatedResponse<AdminGetDictionariesResponse> getDictionaries(
            final Long memberId, final int page, final int size
    ) {
        memberService.validateAdmin(memberId);

        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<Dictionary> dictionaries = dictionaryRepository.findAll(pageRequest);
        final Page<AdminGetDictionariesResponse> responses = dictionaries.map(it -> new AdminGetDictionariesResponse(
                it.getId(),
                it.getTitle(),
                it.getCategory().getValue(),
                it.getStatus().name()
        ));

        return PaginatedResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<AdminGetDictionariesHistoriesResponse> getDictionariesHistories(
            final Long memberId, final int page, final int size
    ) {
        memberService.validateAdmin(memberId);

        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<DictionaryHistory> dictionaryHistories = dictionaryHistoryRepository.findAll(pageRequest);
        final Page<AdminGetDictionariesHistoriesResponse> responses = dictionaryHistories.map(
                it -> new AdminGetDictionariesHistoriesResponse(
                        it.getId(),
                        it.getDictionary().getTitle(),
                        it.getIp(),
                        it.getDictionary().getCategory().getValue(),
                        it.getStatus().name()
                ));

        return PaginatedResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<AdminGetBlacklistResponse> getBlacklist(
            final Long memberId, final int page, final int size
    ) {
        memberService.validateAdmin(memberId);

        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<Blacklist> blacklist = blacklistRepository.findAll(pageRequest);
        final Page<AdminGetBlacklistResponse> responses = blacklist.map(it ->
                new AdminGetBlacklistResponse(it.getId(), it.getIp(), it.getReason(), it.getCreatedAt())
        );

        return PaginatedResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<AdminGetChatsResponse> getChats(final Long memberId, final int page, final int size) {
        memberService.validateAdmin(memberId);

        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<ChatMessage> chatMessages = chatMessageRepository.findAll(pageRequest);
        final Page<AdminGetChatsResponse> responses = chatMessages.map(it -> new AdminGetChatsResponse(
                it.getId(), it.getSender(), it.getContent(), it.getNickname(), it.getCreatedAt()
        ));

        return PaginatedResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<AdminGetBoardsResponse> getBoards(final Long memberId, final int page, final int size) {
        memberService.validateAdmin(memberId);

        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<Board> boards = boardRepository.findAll(pageRequest);
        final Page<AdminGetBoardsResponse> responses = boards.map(it -> new AdminGetBoardsResponse(
                it.getId(), it.getAuthor(), it.getIp(), it.getTitle(), it.getCreatedAt()
        ));
        return PaginatedResponse.of(responses);
    }
}
