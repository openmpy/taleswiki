package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionarySearchRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionarySearchService {

    private final DictionarySearchRepository dictionarySearchRepository;

    @Transactional(readOnly = true)
    public DictionarySearchDictionariesResponse searchByTitle(final String keyword) {
        final List<Dictionary> dictionaries = dictionarySearchRepository.findByTitle_ValueContaining(keyword);
        return DictionarySearchDictionariesResponse.of(dictionaries);
    }
}
