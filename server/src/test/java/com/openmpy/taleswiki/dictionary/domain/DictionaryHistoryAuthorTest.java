package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class DictionaryHistoryAuthorTest {

    @DisplayName("[통과] 사전 기록 작성자 클래스를 생성한다.")
    @Test
    void dictionary_history_author_test_01() {
        // given
        final String value = "test";

        // when
        final DictionaryHistoryAuthor author = new DictionaryHistoryAuthor(value);

        // then
        assertThat(author.getValue()).isEqualTo("test");
    }

    @DisplayName("[예외] 사진 기록 작성자가 공백이다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_dictionary_history_author_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> new DictionaryHistoryAuthor(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("작성자가 공백일 수 없습니다.");
    }

    @DisplayName("[예외] 사전 기록 작성자 명이 8자를 넘어간다.")
    @Test
    void 예외_dictionary_history_author_test_02() {
        // given
        final String value = "1".repeat(9);

        // when & then
        assertThatThrownBy(() -> new DictionaryHistoryAuthor(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("작성자명이 8자를 넘어갈 수 없습니다.");
    }
}