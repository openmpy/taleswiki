package com.openmpy.taleswiki.dictionary.domain.constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryCategoryTest {

    @DisplayName("[통과] DictionaryCategory 클래스를 생성한다.")
    @Test
    void dictionary_category_test_01() {
        // given
        final String value = "person";

        // when
        final DictionaryCategory category = DictionaryCategory.fromName(value);

        // then
        assertThat(category.name()).isEqualTo("PERSON");
        assertThat(category.getValue()).isEqualTo("런너");
    }

    @DisplayName("[예외] 사전 카테고리를 찾을 수 없다.")
    @Test
    void 예외_dictionary_category_test_01() {
        // given
        final String value = "test";

        // when & then
        assertThatThrownBy(() -> DictionaryCategory.fromName(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 카테고리입니다.");
    }
}