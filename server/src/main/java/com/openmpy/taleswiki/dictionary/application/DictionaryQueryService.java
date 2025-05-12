package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryHistoryRepository;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse.DictionaryGetGroupItemsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetRandomResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop10Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryQueryService {

    private final RedisService redisService;
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryHistoryRepository dictionaryHistoryRepository;

    @Transactional(readOnly = true)
    public DictionaryGetTop10Response getTop20Dictionaries() {
        final List<String> statuses = List.of("ALL_ACTIVE", "READ_ONLY");
        final PageRequest pageRequest = PageRequest.of(0, 20);
        final List<Dictionary> dictionaries = dictionaryRepository.findDictionariesByStatusOrderByUpdatedAtDesc(
                statuses, pageRequest
        );
        return DictionaryGetTop10Response.of(dictionaries);
    }

    @Transactional(readOnly = true)
    public DictionaryGetGroupResponse getGroupDictionaries(final String categoryName) {
        final DictionaryCategory category = DictionaryCategory.fromName(categoryName);
        final List<Dictionary> dictionaries = dictionaryRepository.findAllByCategoryOrderByTitle(category);
        final List<DictionaryGetGroupItemsResponse> responses = DictionaryGetGroupItemsResponse.of(dictionaries);
        return new DictionaryGetGroupResponse(responses);
    }

    @Transactional(readOnly = true)
    public DictionaryHistoryResponse get(final HttpServletRequest request, final Long dictionaryHistoryId) {
        final DictionaryHistory dictionaryHistory = getDictionaryHistory(dictionaryHistoryId);
        final Long dictionaryId = dictionaryHistory.getDictionary().getId();

        final String clientIp = IpAddressUtil.getClientIp(request);
        final String key = String.format("dictionary-view:%d:%s", dictionaryId, clientIp);

        if (redisService.setIfAbsent(key, "true", Duration.ofHours(1L))) {
            final String viewKey = String.format("dictionary-view:%d", dictionaryId);
            redisService.increment(viewKey);
        }
        return DictionaryHistoryResponse.of(dictionaryHistory);
    }

    @Transactional(readOnly = true)
    public DictionaryGetHistoriesResponse getHistories(final Long dictionaryId) {
        final Dictionary dictionary = getDictionary(dictionaryId);

        if (dictionary.getStatus() == DictionaryStatus.HIDDEN) {
            throw new CustomException("숨김 처리된 문서입니다.");
        }

        final List<DictionaryHistory> histories = dictionary.getHistories();
        return DictionaryGetHistoriesResponse.of(dictionary.getTitle(), histories);
    }

    @Transactional(readOnly = true)
    public DictionaryGetRandomResponse getRandomDictionary() {
        final long count = dictionaryRepository.count();
        final long randomOffset = ThreadLocalRandom.current().nextLong(1, count + 1);
        final PageRequest pageRequest = PageRequest.of(0, 1);

        final List<Dictionary> dictionaries = dictionaryRepository.findFirstByIdGreaterThanEqualOrderByIdAsc(
                randomOffset, pageRequest
        );

        if (dictionaries.isEmpty()) {
            throw new CustomException("문서를 찾지 못했습니다.");
        }
        return new DictionaryGetRandomResponse(dictionaries.getFirst().getCurrentHistory().getId());
    }

    public Dictionary getDictionary(final Long dictionaryId) {
        return dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 문서 번호입니다."));
    }

    public DictionaryHistory getDictionaryHistory(final Long dictionaryHistoryId) {
        return dictionaryHistoryRepository.findById(dictionaryHistoryId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 문서 기록 번호입니다."));
    }
}
