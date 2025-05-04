package com.openmpy.taleswiki.dictionary.domain.constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryStatusTest {

    @DisplayName("[통과] DictionaryStatus 클래스를 생성한다.")
    @Test
    void dictionary_status_test_01() {
        // given
        final String value = "all_active";

        // when
        final DictionaryStatus status = DictionaryStatus.fromName(value);

        // then
        assertThat(status.name()).isEqualTo("ALL_ACTIVE");
    }

    @DisplayName("[예외] 사전 상태를 찾을 수 없다.")
    @Test
    void 예외_dictionary_status_test_01() {
        // given
        final String value = "test";

        // when & then
        assertThatThrownBy(() -> DictionaryStatus.fromName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("찾을 수 없는 상태입니다.");
    }
}