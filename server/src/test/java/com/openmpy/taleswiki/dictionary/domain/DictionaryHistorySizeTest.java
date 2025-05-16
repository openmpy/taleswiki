package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryHistorySizeTest {

    @DisplayName("[통과] 사전 기록 사이즈 클래스를 생성한다.")
    @Test
    void dictionary_history_size_test_01() {
        // given
        final long value = 100L;

        // when
        final DictionaryHistorySize size = new DictionaryHistorySize(value);

        // then
        assertThat(size.getValue()).isEqualTo(100L);
    }

    @DisplayName("[예외] 사전 기록 사이즈가 음수이다.")
    @Test
    void 예외_dictionary_history_size_test_01() {
        // given
        final long value = -1L;

        // when & then
        assertThatThrownBy(() -> new DictionaryHistorySize(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("사이즈 값이 음수일 수 없습니다.");
    }
}