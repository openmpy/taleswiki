package com.openmpy.taleswiki.dictionary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryViewTest {

    @DisplayName("[통과] 사전 조회수 클래스를 생성한다.")
    @Test
    void dictionary_view_test_01() {
        // given
        final long value = 100L;

        // when
        final DictionaryView view = new DictionaryView(value);

        // then
        assertThat(view.getValue()).isEqualTo(100L);
    }

    @DisplayName("[통과] 사전 조회수가 +1 증가한다.")
    @Test
    void dictionary_view_test_02() {
        // given
        final long value = 100L;
        final DictionaryView view = new DictionaryView(value);

        // when
        view.increment(10L);

        // then
        assertThat(view.getValue()).isEqualTo(110L);
    }

    @DisplayName("[예외] 사전 조회수가 음수이다.")
    @Test
    void 예외_dictionary_view_test_01() {
        // given
        final long value = -1L;

        // when & then
        assertThatThrownBy(() -> new DictionaryView(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("조회수가 음수일 수 없습니다.");
    }
}