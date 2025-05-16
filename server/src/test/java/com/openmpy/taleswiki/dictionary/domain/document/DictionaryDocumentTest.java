package com.openmpy.taleswiki.dictionary.domain.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.helper.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryDocumentTest {

    @DisplayName("[통과] 사전 검색 Document를 생성한다.")
    @Test
    void dictionary_document_test_01() {
        // given
        final long id = 1L;
        final String title = "제목";
        final String chosung = "ㅈㅁ";
        final String category = "런너";
        final long currentHistoryId = 1L;

        // when
        final DictionaryDocument document = new DictionaryDocument(id, title, chosung, category, currentHistoryId);

        // then
        assertThat(document.getId()).isEqualTo(1L);
        assertThat(document.getTitle()).isEqualTo("제목");
        assertThat(document.getChosung()).isEqualTo("ㅈㅁ");
        assertThat(document.getCategory()).isEqualTo("런너");
        assertThat(document.getCurrentHistoryId()).isEqualTo(1L);
    }

    @DisplayName("[통과] 사전 값을 받아서 사전 검색을 반환한다.")
    @Test
    void dictionary_document_test_02() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();

        // when
        final DictionaryDocument document = DictionaryDocument.of(dictionary);

        // then
        assertThat(document.getTitle()).isEqualTo("제목");
        assertThat(document.getChosung()).isEqualTo("ㅈㅁ");
        assertThat(document.getCategory()).isEqualTo("런너");
    }
}