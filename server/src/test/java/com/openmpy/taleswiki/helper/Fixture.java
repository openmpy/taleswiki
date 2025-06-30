package com.openmpy.taleswiki.helper;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import com.openmpy.taleswiki.member.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;

public class Fixture {

    public static final Dictionary DICTIONARY_01 = Dictionary.create("제목", DictionaryCategory.PERSON);
    public static final DictionaryHistory DICTIONARY_HISTORY_01 = DictionaryHistory.create(
            "작성자", "내용", 10L, "127.0.0.1", DICTIONARY_01
    );

    public static Dictionary createDictionary() {
        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        return dictionary;
    }

    public static Member createMember() {
        return Member.create("test@test.com", MemberSocial.GOOGLE);
    }

    public static HttpServletRequest createMockHttpServletRequest() {
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("X-Forwarded-For", "127.0.0.1");
        return servletRequest;
    }
}
