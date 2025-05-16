package com.openmpy.taleswiki.helper;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;

public class Fixture {

    public static final Dictionary DICTIONARY_01 = Dictionary.create("제목", DictionaryCategory.PERSON);
    public static final DictionaryHistory DICTIONARY_HISTORY_01 = DictionaryHistory.create(
            "작성자", "내용", 10L, "127.0.0.1", DICTIONARY_01
    );
}
