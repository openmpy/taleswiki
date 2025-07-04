package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DictionaryTitleTest {

    @DisplayName("[통과] 사전 제목 클래스를 생성한다.")
    @Test
    void dictionary_title_test_01() {
        // given
        final String value = "test";

        // when
        final DictionaryTitle title = new DictionaryTitle(value);

        // then
        assertThat(title.getValue()).isEqualTo("test");
    }

    @DisplayName("[예외] 사전 제목에 공백이 들어간다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_dictionary_title_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> new DictionaryTitle(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("제목이 공백일 수 없습니다.");
    }

    @DisplayName("[예외] 사전 제목 길이가 12자를 넘어간다.")
    @Test
    void 예외_dictionary_title_test_02() {
        // given
        final String value = "1".repeat(13);

        // when & then
        assertThatThrownBy(() -> new DictionaryTitle(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("제목 길이가 12자를 넘길 수 없습니다.");
    }

    @DisplayName("[예외] 사전 제목에 올바르지 않은 문자가 들어간다. (특수문자, 모음, 자음)")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(strings = {"!@#", "ㄱㄴㄷ", "ㅏㅑㅗ"})
    void 예외_dictionary_title_test_03(final String value) {
        // when & then
        assertThatThrownBy(() -> new DictionaryTitle(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("제목이 올바르지 않습니다.");
    }
}