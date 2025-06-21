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
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetRandomResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetVersionResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetVersionsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryQueryService {

    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryHistoryRepository dictionaryHistoryRepository;

    @Transactional(readOnly = true)
    public DictionaryGetTop20Response getTop20Dictionaries() {
        final List<String> statuses = List.of("ALL_ACTIVE", "READ_ONLY");
        final PageRequest pageRequest = PageRequest.of(0, 20);
        final List<Dictionary> dictionaries = dictionaryRepository.findDictionariesByStatusOrderByModifiedAtDesc(
                statuses, pageRequest
        );
        return DictionaryGetTop20Response.of(dictionaries);
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
        final Dictionary dictionary = dictionaryHistory.getDictionary();
        final Long dictionaryId = dictionary.getId();

        final String clientIp = IpAddressUtil.getClientIp(request);
        final String key = String.format("dictionary-view_%d:%s", dictionaryId, clientIp);

        if (redisService.setIfAbsent(key, "true", Duration.ofHours(1L))) {
            final String viewKey = String.format("dictionary-view:%d", dictionaryId);

            redisService.incrementScore("popular_dictionaries", dictionaryId, 1.0);
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
    public DictionaryGetVersionsResponse getVersions(final Long dictionaryId) {
        final Dictionary dictionary = getDictionary(dictionaryId);
        return DictionaryGetVersionsResponse.of(dictionary);
    }

    @Transactional(readOnly = true)
    public DictionaryGetRandomResponse getRandomDictionary() {
        final Object randomIds = redisTemplate.opsForSet().randomMember("dictionary:ids");
        final Dictionary dictionary = getDictionary(((Number) randomIds).longValue());

        return new DictionaryGetRandomResponse(dictionary.getCurrentHistory().getId());
    }

    @Transactional(readOnly = true)
    public DictionaryGetVersionResponse getVersion(final Long dictionaryId, final Long version) {
        final DictionaryHistory dictionaryHistory = dictionaryHistoryRepository.findByDictionary_IdAndVersion_Value(
                        dictionaryId, version
                )
                .orElseThrow(() -> new CustomException("찾을 수 없는 문서 버전입니다."));

        return DictionaryGetVersionResponse.of(dictionaryHistory);
    }

    @Transactional(readOnly = true)
    public DictionaryGetPopularResponse getPopular() {
        final Set<TypedTuple<Object>> popularDocs = redisService.reverseRangeWithScores("popular_dictionaries", 0, 9);

        final List<Long> popularDictionaryIds = Objects.requireNonNull(popularDocs).stream()
                .map(doc -> Long.parseLong(Objects.requireNonNull(doc.getValue()).toString()))
                .toList();

        final List<Dictionary> dictionaries = dictionaryRepository.findAllById(popularDictionaryIds).stream()
                .sorted(Comparator.comparingInt(o -> popularDictionaryIds.indexOf(o.getId())))
                .toList();

        return DictionaryGetPopularResponse.of(dictionaries);
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
