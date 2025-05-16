package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DictionaryHistoryVersionTest {

    @DisplayName("[통과] 사전 기록 버전 클래스를 생성한다.")
    @Test
    void dictionary_history_version_test_01() {
        // given
        final long value = 1L;

        // when
        final DictionaryHistoryVersion historyVersion = new DictionaryHistoryVersion(value);

        // then
        assertThat(historyVersion.getValue()).isEqualTo(1L);
    }

    @DisplayName("[예외] 사전 기록 버전이 0 또는 음수이다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(longs = {0L, -1L})
    void 예외_dictionary_history_version_test_01(final long value) {
        // when & then
        assertThatThrownBy(() -> new DictionaryHistoryVersion(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("버전 값이 0 또는 음수일 수 없습니다.");
    }
}