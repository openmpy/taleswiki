package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.common.util.CharacterUtil;
import com.openmpy.taleswiki.dictionary.domain.document.DictionaryDocument;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionarySearchRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse.DictionarySearchDictionariesItemResponse;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionarySearchServiceTest extends ServiceTestSupport {

    @Autowired
    private DictionarySearchService dictionarySearchService;

    @MockitoBean
    private DictionarySearchRepository dictionarySearchRepository;

    @DisplayName("[통과] 사전 검색 기록을 저장한다.")
    @Test
    void dictionary_search_service_test_01() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();

        // when
        dictionarySearchService.save(dictionary);

        // then
        final ArgumentCaptor<DictionaryDocument> captor = ArgumentCaptor.forClass(DictionaryDocument.class);
        verify(dictionarySearchRepository, times(1)).save(captor.capture());

        final DictionaryDocument savedDocument = captor.getValue();
        assertThat(savedDocument.getId()).isEqualTo(dictionary.getId());
        assertThat(savedDocument.getTitle()).isEqualTo(dictionary.getTitle());
        assertThat(savedDocument.getChosung()).isEqualTo(CharacterUtil.extractChosung(dictionary.getTitle()));
        assertThat(savedDocument.getCategory()).isEqualTo(dictionary.getCategory().getValue());
        assertThat(savedDocument.getCurrentHistoryId()).isEqualTo(dictionary.getCurrentHistory().getId());
    }

    @DisplayName("[통과] 사전 검색 기록을 삭제한다.")
    @Test
    void dictionary_search_service_test_02() {
        // given
        final DictionaryDocument dictionaryDocument = new DictionaryDocument(1L, "제목", "ㅈㅁ", "런너", 1L);

        // stub
        when(dictionarySearchRepository.findById(dictionaryDocument.getId()))
                .thenReturn(Optional.of(dictionaryDocument));

        // when
        dictionarySearchService.delete(dictionaryDocument.getId());

        // then
        verify(dictionarySearchRepository, times(1)).delete(dictionaryDocument);
    }

    @DisplayName("[통과] 사전 제목으로 초성 검색을 한다.")
    @Test
    void dictionary_search_service_test_03() {
        // given
        final DictionaryDocument dictionaryDocument1 = new DictionaryDocument(1L, "수환", "ㅅㅎ", "런너", 1L);
        final DictionaryDocument dictionaryDocument2 = new DictionaryDocument(2L, "상혁", "ㅅㅎ", "길드", 2L);
        final List<DictionaryDocument> dictionaryDocuments = List.of(dictionaryDocument1, dictionaryDocument2);

        // stub
        when(dictionarySearchRepository.findByChosungStartingWith(anyString())).thenReturn(dictionaryDocuments);

        // when
        final DictionarySearchDictionariesResponse response = dictionarySearchService.searchByTitle("ㅅㅎ");

        // then
        final List<DictionarySearchDictionariesItemResponse> dictionaries = response.dictionaries();

        assertThat(dictionaries).hasSize(2);
        assertThat(dictionaries.getFirst().currentHistoryId()).isEqualTo(1L);
        assertThat(dictionaries.getFirst().title()).isEqualTo("수환");
        assertThat(dictionaries.getFirst().category()).isEqualTo("런너");
        assertThat(dictionaries.getLast().currentHistoryId()).isEqualTo(2L);
        assertThat(dictionaries.getLast().title()).isEqualTo("상혁");
        assertThat(dictionaries.getLast().category()).isEqualTo("길드");
    }

    @DisplayName("[통과] 사전 제목으로 검색을 한다.")
    @Test
    void dictionary_search_service_test_04() {
        // given
        final DictionaryDocument dictionaryDocument1 = new DictionaryDocument(1L, "수환", "ㅅㅎ", "런너", 1L);
        final DictionaryDocument dictionaryDocument2 = new DictionaryDocument(2L, "수호", "ㅅㅎ", "길드", 2L);
        final List<DictionaryDocument> dictionaryDocuments = List.of(dictionaryDocument1, dictionaryDocument2);

        // stub
        when(dictionarySearchRepository.findByTitleStartingWith(anyString())).thenReturn(dictionaryDocuments);

        // when
        final DictionarySearchDictionariesResponse response = dictionarySearchService.searchByTitle("수");

        // then
        final List<DictionarySearchDictionariesItemResponse> dictionaries = response.dictionaries();

        assertThat(dictionaries).hasSize(2);
        assertThat(dictionaries.getFirst().currentHistoryId()).isEqualTo(1L);
        assertThat(dictionaries.getFirst().title()).isEqualTo("수환");
        assertThat(dictionaries.getFirst().category()).isEqualTo("런너");
        assertThat(dictionaries.getLast().currentHistoryId()).isEqualTo(2L);
        assertThat(dictionaries.getLast().title()).isEqualTo("수호");
        assertThat(dictionaries.getLast().category()).isEqualTo("길드");
    }
}