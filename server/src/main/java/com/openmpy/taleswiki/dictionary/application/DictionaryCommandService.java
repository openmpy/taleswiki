package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryUpdateResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryCommandService {

    private final DictionaryQueryService dictionaryQueryService;
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

    @Transactional
    public DictionaryUpdateResponse update(
            final Long dictionaryId, final HttpServletRequest servletRequest, final DictionaryUpdateRequest request
    ) {
        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        if (dictionary.getStatus() != DictionaryStatus.ALL_ACTIVE) {
            throw new IllegalArgumentException("수정할 수 없는 사전입니다.");
        }

        final long contentLength = servletRequest.getContentLengthLong();
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final long version = dictionary.getCurrentHistory().getVersion() + 1;

        final DictionaryHistory dictionaryHistory = DictionaryHistory.update(
                request.author(), request.content(), version, contentLength, clientIp, dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        return new DictionaryUpdateResponse(savedDictionary.getCurrentHistory().getId());
    }

    @Transactional
    public void changeStatus(final Long dictionaryId, final String status) {
        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        dictionary.changeStatus(status);
    }

    @Transactional
    public void delete(final Long dictionaryId) {
        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        dictionaryRepository.delete(dictionary);
    }
}
