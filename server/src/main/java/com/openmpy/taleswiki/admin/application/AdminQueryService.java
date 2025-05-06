package com.openmpy.taleswiki.admin.application;

import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesHistoriesResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.properties.AdminProperties;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryHistoryRepository;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminQueryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryHistoryRepository dictionaryHistoryRepository;
    private final AdminProperties adminProperties;

    @Transactional(readOnly = true)
    public void me(final String token) {
        validateToken(token);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<AdminGetDictionariesResponse> getDictionaries(
            final String token, final int page, final int size
    ) {
        validateToken(token);

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
            final String token, final int page, final int size
    ) {
        validateToken(token);

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

    public void validateToken(final String token) {
        final String adminToken = adminProperties.token();

        if (token == null || token.isBlank() || !token.equals(adminToken)) {
            throw new CustomException("잘못된 토큰 값입니다.");
        }
    }
}
