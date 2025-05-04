package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DictionaryHistoryVersionTest {

    @DisplayName("[통과] DictionaryHistoryVersion 클래스를 생성한다.")
    @Test
    void dictionary_history_version_test_01() {
        // given
        final long value = 1;

        // when
        final DictionaryHistoryVersion version = new DictionaryHistoryVersion(value);

        // then
        assertThat(version.getValue()).isEqualTo(value);
    }

    @DisplayName("[예외] 버전 값이 0 또는 음수이다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(longs = {0, -1})
    void 예외_dictionary_history_version_test_01(final long value) {
        // when & then
        assertThatThrownBy(() -> new DictionaryHistoryVersion(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("버전 값이 0 또는 음수일 수 없습니다.");
    }
}