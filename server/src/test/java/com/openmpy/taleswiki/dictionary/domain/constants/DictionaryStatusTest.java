package com.openmpy.taleswiki.dictionary.domain.constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryStatusTest {

    @DisplayName("[통과] 사전 상태를 생성한다.")
    @Test
    void dictionary_status_test_01() {
        // given
        final String allActive = "ALL_ACTIVE";
        final String readOnly = "READ_ONLY";
        final String hidden = "HIDDEN";

        // when
        final DictionaryStatus 활성 = DictionaryStatus.valueOf(allActive);
        final DictionaryStatus 읽기전용 = DictionaryStatus.valueOf(readOnly);
        final DictionaryStatus 숨김 = DictionaryStatus.valueOf(hidden);

        // then
        assertThat(활성).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(읽기전용).isEqualTo(DictionaryStatus.READ_ONLY);
        assertThat(숨김).isEqualTo(DictionaryStatus.HIDDEN);
    }

    @DisplayName("[통과] 사전 상태를 대소문자 상관 없이 찾는다.")
    @Test
    void dictionary_status_test_02() {
        // given
        final String allActive = "ALL_ACTIVE";
        final String readOnly = "read_only";
        final String hidden = "HIDDEN";

        // when
        final DictionaryStatus 활성 = DictionaryStatus.fromName(allActive);
        final DictionaryStatus 읽기전용 = DictionaryStatus.fromName(readOnly);
        final DictionaryStatus 숨김 = DictionaryStatus.fromName(hidden);

        // then
        assertThat(활성).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(읽기전용).isEqualTo(DictionaryStatus.READ_ONLY);
        assertThat(숨김).isEqualTo(DictionaryStatus.HIDDEN);
    }

    @DisplayName("[예외] 사전 상태를 찾지 못한다.")
    @Test
    void 예외_dictionary_status_test_01() {
        // given
        final String test = "test";

        // when & then
        assertThatThrownBy(() -> DictionaryStatus.fromName(test))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 상태입니다.");
    }
}