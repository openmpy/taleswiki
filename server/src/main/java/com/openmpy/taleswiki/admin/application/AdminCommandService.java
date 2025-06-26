package com.openmpy.taleswiki.admin.application;

import com.openmpy.taleswiki.admin.domain.entity.Blacklist;
import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.admin.dto.request.AdminBlacklistSaveRequest;
import com.openmpy.taleswiki.chat.domain.entity.ChatMessage;
import com.openmpy.taleswiki.chat.domain.repository.ChatMessageRepository;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.application.DictionaryQueryService;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCommandService {

    private final DictionaryQueryService dictionaryQueryService;
    private final MemberService memberService;
    private final DictionaryRepository dictionaryRepository;
    private final BlacklistRepository blacklistRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void changeDictionaryStatus(final Long memberId, final Long dictionaryId, final String status) {
        memberService.validateAdmin(memberId);

        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);
        final DictionaryStatus dictionaryStatus = DictionaryStatus.fromName(status);

        dictionary.changeStatus(dictionaryStatus);
    }

    @Transactional
    public void delete(final Long memberId, final Long dictionaryId) {
        memberService.validateAdmin(memberId);

        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        dictionaryRepository.delete(dictionary);
    }

    @Transactional
    public void changeDictionaryHistoryStatus(
            final Long memberId, final Long dictionaryHistoriesId, final String status
    ) {
        memberService.validateAdmin(memberId);

        final DictionaryHistory dictionaryHistory = dictionaryQueryService.getDictionaryHistory(dictionaryHistoriesId);

        dictionaryHistory.changeStatus(DictionaryStatus.fromName(status));
    }

    @Transactional
    public void saveBlacklist(final Long memberId, final AdminBlacklistSaveRequest request) {
        memberService.validateAdmin(memberId);

        if (blacklistRepository.existsByIp_Value(request.ip())) {
            throw new CustomException("이미 등록된 IP입니다.");
        }

        final Blacklist blacklist = Blacklist.create(request.ip(), request.reason());
        blacklistRepository.save(blacklist);
    }

    @Transactional
    public void deleteBlacklist(final Long memberId, final Long blacklistId) {
        memberService.validateAdmin(memberId);

        final Blacklist blacklist = blacklistRepository.findById(blacklistId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 블랙리스트 번호입니다."));

        blacklistRepository.delete(blacklist);
    }

    @Transactional
    public void deleteChat(final Long memberId, final Long chatId) {
        memberService.validateAdmin(memberId);

        final ChatMessage chatMessage = chatMessageRepository.findById(chatId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 채팅 메세지 번호입니다."));

        chatMessageRepository.delete(chatMessage);
    }
}
