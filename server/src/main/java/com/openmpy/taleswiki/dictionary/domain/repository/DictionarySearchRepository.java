package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.document.DictionaryDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DictionarySearchRepository extends ElasticsearchRepository<DictionaryDocument, Long> {

    List<DictionaryDocument> findByTitleStartingWith(final String keyword);

    List<DictionaryDocument> findByChosungStartingWith(final String keyword);
}
