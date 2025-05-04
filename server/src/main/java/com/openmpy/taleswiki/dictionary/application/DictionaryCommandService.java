package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryCommandService {

    private final DictionaryRepository dictionaryRepository;

    @Transactional
    public void save(final HttpServletRequest servletRequest, final DictionarySaveRequest request) {
        final DictionaryCategory category = DictionaryCategory.fromName(request.category());

        if (dictionaryRepository.existsByTitle_ValueAndCategory(request.title(), category)) {
            throw new IllegalArgumentException("이미 작성된 사전입니다.");
        }

        final long contentLength = servletRequest.getContentLengthLong();
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);

        final Dictionary dictionary = Dictionary.create(request.title(), category);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                request.author(), request.content(), contentLength, clientIp, dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        dictionaryRepository.save(dictionary);
    }
}
