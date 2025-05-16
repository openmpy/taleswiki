package com.openmpy.taleswiki.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CharacterUtilTest {

    @DisplayName("[통과] 문자열의 이니셜을 구한다.")
    @Test
    void character_util_test_01() {
        // given
        final String value1 = "가나다";
        final String value2 = "asd";
        final String value3 = "123";
        final String value4 = "\uD800\uDF48";

        // when
        final Character initial1 = CharacterUtil.getInitial(value1);
        final Character initial2 = CharacterUtil.getInitial(value2);
        final Character initial3 = CharacterUtil.getInitial(value3);
        final Character initial4 = CharacterUtil.getInitial(value4);

        // then
        assertThat(initial1).isEqualTo('ㄱ');
        assertThat(initial2).isEqualTo('A');
        assertThat(initial3).isEqualTo('1');
        assertThat(initial4).isEqualTo('\uD800');
    }

    @DisplayName("[통과] 문자열에서 초성을 추출한다.")
    @Test
    void character_util_test_02() {
        // given
        final String value1 = "가나다";
        final String value2 = "asd";
        final String value3 = "123";
        final String value4 = "\uD800\uDF48";

        // when
        final String chosung1 = CharacterUtil.extractChosung(value1);
        final String chosung2 = CharacterUtil.extractChosung(value2);
        final String chosung3 = CharacterUtil.extractChosung(value3);
        final String chosung4 = CharacterUtil.extractChosung(value4);

        // then
        assertThat(chosung1).isEqualTo("ㄱㄴㄷ");
        assertThat(chosung2).isEqualTo("asd");
        assertThat(chosung3).isEqualTo("123");
        assertThat(chosung4).isEqualTo("\uD800\uDF48");
    }

    @DisplayName("[통과] 문자열이 초성으로만 이루어졌는지 확인한다.")
    @Test
    void character_util_test_03() {
        // given
        final String chosung = "ㄱㄴㄷ";
        final String notChosung = "가나다";

        // when
        final boolean isChosung = CharacterUtil.isChosungOnly(chosung);
        final boolean isNotChosung = CharacterUtil.isChosungOnly(notChosung);

        // then
        assertThat(isChosung).isTrue();
        assertThat(isNotChosung).isFalse();
    }

    @DisplayName("[예외] 입력 받는 문자열이 빈 값이다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_character_util_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> CharacterUtil.getInitial(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("문자열이 빈 값일 수 없습니다.");

        assertThatThrownBy(() -> CharacterUtil.extractChosung(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("문자열이 빈 값일 수 없습니다.");
    }
}