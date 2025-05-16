package com.openmpy.taleswiki.dictionary.domain.constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryCategoryTest {

    @DisplayName("[통과] 사전 카테고리를 생성한다.")
    @Test
    void dictionary_category_test_01() {
        // given
        final String person = "PERSON";
        final String guild = "GUILD";

        // when
        final DictionaryCategory 런너 = DictionaryCategory.valueOf(person);
        final DictionaryCategory 길드 = DictionaryCategory.valueOf(guild);

        // then
        assertThat(런너.getValue()).isEqualTo("런너");
        assertThat(길드.getValue()).isEqualTo("길드");
    }

    @DisplayName("[통과] 사전 카테고리를 대소문자 상관 없이 찾는다.")
    @Test
    void dictionary_category_test_02() {
        // given
        final String person = "PERSON";
        final String guild = "guild";

        // when
        final DictionaryCategory 런너 = DictionaryCategory.fromName(person);
        final DictionaryCategory 길드 = DictionaryCategory.fromName(guild);

        // then
        assertThat(런너.getValue()).isEqualTo("런너");
        assertThat(길드.getValue()).isEqualTo("길드");
    }

    @DisplayName("[예외] 사전 카테고리를 찾지 못한다.")
    @Test
    void 예외_dictionary_category_test_01() {
        // given
        final String test = "test";

        // when & then
        assertThatThrownBy(() -> DictionaryCategory.fromName(test))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 카테고리입니다.");
    }
}