package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.util.CharacterUtil;
import com.openmpy.taleswiki.dictionary.domain.document.DictionaryDocument;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionarySearchRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DictionarySearchService {

    private final DictionarySearchRepository dictionarySearchRepository;

    @Profile({"dev", "local"})
    @PostConstruct
    public void destroyAllSearchDocuments() {
        dictionarySearchRepository.deleteAll();
    }

    public void save(final Dictionary dictionary) {
        final DictionaryDocument dictionaryDocument = DictionaryDocument.of(dictionary);
        dictionarySearchRepository.save(dictionaryDocument);
    }

    public void delete(final Long id) {
        dictionarySearchRepository.deleteById(id);
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
}
