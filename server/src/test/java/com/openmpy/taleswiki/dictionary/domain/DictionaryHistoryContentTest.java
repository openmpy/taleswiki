package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryHistoryContentTest {

    @DisplayName("[통과] DictionaryHistoryContent 클래스를 생성한다.")
    @Test
    void dictionary_history_content_test_01() {
        // given
        final String value = "test";

        // when
        final DictionaryHistoryContent content = new DictionaryHistoryContent(value);

        // then
        assertThat(content.getValue()).isEqualTo(value);
    }
}