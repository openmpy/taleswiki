package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DictionaryQueryService {

    private final DictionaryRepository dictionaryRepository;

    public Dictionary getDictionary(final Long dictionaryId) {
        return dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사전 번호입니다."));
    }
}
