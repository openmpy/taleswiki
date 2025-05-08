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
            final int unicode = c - '가';
            final int choIndex = unicode / (21 * 28);
            char cho = CHO_SUNG[choIndex];

            cho = CHO_SUNG_NORMALIZATION.getOrDefault(cho, cho);
            return cho;
        }
        if (Character.isAlphabetic(c)) {
            return Character.toUpperCase(c);
        }
        return c;
    }

    public static String extractChosung(final String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("문자열이 빈 값일 수 없습니다.");
        }

        final StringBuilder sb = new StringBuilder();

        for (final char ch : text.toCharArray()) {
            if (ch >= '가' && ch <= '힣') {
                final int unicode = ch - '가';
                final int choIndex = unicode / (21 * 28);

                sb.append(CHO_SUNG[choIndex]);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static boolean isChosungOnly(final String text) {
        final String regex = "^[ㄱ-ㅎ]+$";
        return text.matches(regex);
    }
}
