package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DictionarySearchServiceTest extends ServiceTestSupport {

    @Autowired
    private DictionarySearchService dictionarySearchService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @DisplayName("[통과] 문서를 제목으로 검색한다.")
    @Test
    void dictionary_search_service_test_01() {
        // given
        dictionaryRepository.save(Fixture.createDictionary());

        // when
        final DictionarySearchDictionariesResponse response = dictionarySearchService.searchByTitle("제목");

        // then
        assertThat(response.dictionaries()).hasSize(1);
    }
}