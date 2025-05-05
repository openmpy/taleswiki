package com.openmpy.taleswiki.common.util;

import java.util.Map;

public class CharacterUtil {

    private static final char[] CHO_SUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ',
            'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ',
            'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };
    private static final Map<Character, Character> CHO_SUNG_NORMALIZATION = Map.of(
            'ㄲ', 'ㄱ',
            'ㄸ', 'ㄷ',
            'ㅃ', 'ㅂ',
            'ㅆ', 'ㅅ',
            'ㅉ', 'ㅈ'
    );

    public static Character getInitialGroup(final String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("문자열이 빈 값일 수 없습니다.");
        }

        final char c = text.charAt(0);

        if (c >= '가' && c <= '힣') {
            final int uniVal = c - '가';
            final int choIndex = uniVal / (21 * 28);
            char cho = CHO_SUNG[choIndex];

            cho = CHO_SUNG_NORMALIZATION.getOrDefault(cho, cho);
            return cho;
        }
        if (Character.isAlphabetic(c)) {
            return Character.toUpperCase(c);
        }
        return c;
    }
}
