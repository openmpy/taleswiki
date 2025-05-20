package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.CharacterUtil;
import com.openmpy.taleswiki.dictionary.domain.document.DictionaryDocument;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionarySearchRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DictionarySearchService {

    private final DictionarySearchRepository dictionarySearchRepository;
    private final Environment environment;

    @PostConstruct
    private void destroyAllSearchDocuments() {
        final List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());

        if (activeProfiles.contains("dev") || activeProfiles.contains("local")) {
            dictionarySearchRepository.deleteAll();
        }
    }

    public void save(final Dictionary dictionary) {
        final DictionaryDocument dictionaryDocument = DictionaryDocument.of(dictionary);
        dictionarySearchRepository.save(dictionaryDocument);
    }

    public void delete(final Long id) {
        final DictionaryDocument dictionaryDocument = getDictionaryDocument(id);
        dictionarySearchRepository.delete(dictionaryDocument);
    }

    public DictionarySearchDictionariesResponse searchByTitle(final String keyword) {
        final List<DictionaryDocument> dictionaryDocuments;

        if (CharacterUtil.isChosungOnly(keyword)) {
            dictionaryDocuments = dictionarySearchRepository.findByChosungStartingWith(keyword);
        } else {
            dictionaryDocuments = dictionarySearchRepository.findByTitleStartingWith(keyword);
        }
        return DictionarySearchDictionariesResponse.of(dictionaryDocuments);
    }

    private DictionaryDocument getDictionaryDocument(final Long id) {
        return dictionarySearchRepository.findById(id)
                .orElseThrow(() -> new CustomException("사전 검색 ID를 찾을 수 없습니다."));
    }
}
