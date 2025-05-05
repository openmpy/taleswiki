package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryHistoryRepository;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse.DictionaryGetGroupItemsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop10Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryQueryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryHistoryRepository dictionaryHistoryRepository;

    @Transactional(readOnly = true)
    public DictionaryGetTop10Response getTop20Dictionaries() {
        final List<String> statuses = List.of("ALL_ACTIVE", "READ_ONLY");
        final List<Dictionary> dictionaries = dictionaryRepository.findTop20ByStatusInOrderByUpdatedAtDesc(statuses);
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
    public DictionaryHistoryResponse get(final Long dictionaryHistoryId) {
        final DictionaryHistory dictionaryHistory = getDictionaryHistory(dictionaryHistoryId);
        return DictionaryHistoryResponse.of(dictionaryHistory);
    }

    public Dictionary getDictionary(final Long dictionaryId) {
        return dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사전 번호입니다."));
    }

    public DictionaryHistory getDictionaryHistory(final Long dictionaryHistoryId) {
        return dictionaryHistoryRepository.findById(dictionaryHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사전 기록 번호입니다."));
    }
}
