package com.openmpy.taleswiki.admin.application;

import com.openmpy.taleswiki.admin.domain.entity.Blacklist;
import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.admin.dto.request.AdminBlacklistSaveRequest;
import com.openmpy.taleswiki.admin.dto.request.AdminSigninRequest;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.properties.AdminProperties;
import com.openmpy.taleswiki.dictionary.application.DictionaryQueryService;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCommandService {

    private final AdminQueryService adminQueryService;
    private final DictionaryQueryService dictionaryQueryService;
    private final DictionaryRepository dictionaryRepository;
    private final BlacklistRepository blacklistRepository;
    private final AdminProperties adminProperties;

    @Transactional
    public String signin(final AdminSigninRequest request) {
        if (!adminProperties.nickname().equals(request.nickname()) ||
                !adminProperties.password().equals(request.password())) {
            throw new CustomException("닉네임 또는 패스워드를 다시 한번 확인해주시길 바랍니다.");
        }
        return adminProperties.token();
    }

    @Transactional
    public void changeDictionaryStatus(final String token, final Long dictionaryId, final String status) {
        adminQueryService.validateToken(token);

        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        dictionary.changeStatus(status);
    }

    @Transactional
    public void delete(final String token, final Long dictionaryId) {
        adminQueryService.validateToken(token);

        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        dictionaryRepository.delete(dictionary);
    }

    @Transactional
    public void changeDictionaryHistoryStatus(
            final String token, final Long dictionaryHistoriesId, final String status
    ) {
        adminQueryService.validateToken(token);

        final DictionaryHistory dictionaryHistory = dictionaryQueryService.getDictionaryHistory(dictionaryHistoriesId);

        dictionaryHistory.changeStatus(status);
    }

    @Transactional
    public void saveBlacklist(final String token, final AdminBlacklistSaveRequest request) {
        adminQueryService.validateToken(token);

        if (blacklistRepository.existsByIp_Value(request.ip())) {
            throw new CustomException("이미 등록된 IP입니다.");
        }

        final Blacklist blacklist = Blacklist.create(request.ip(), request.reason());
        blacklistRepository.save(blacklist);
    }

    @Transactional
    public void deleteBlacklist(final String token, final Long blacklistId) {
        adminQueryService.validateToken(token);

        final Blacklist blacklist = blacklistRepository.findById(blacklistId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 블랙리스트 번호입니다."));

        blacklistRepository.delete(blacklist);
    }
}
