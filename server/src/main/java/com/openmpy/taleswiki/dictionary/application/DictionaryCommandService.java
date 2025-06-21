package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySaveResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryUpdateResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryCommandService {

    private final DictionaryQueryService dictionaryQueryService;
    private final ImageS3Service imageS3Service;
    private final RedisService redisService;
    private final DictionaryRepository dictionaryRepository;

    @Transactional
    public DictionarySaveResponse save(final HttpServletRequest servletRequest, final DictionarySaveRequest request) {
        final DictionaryCategory category = DictionaryCategory.fromName(request.category());

        if (dictionaryRepository.existsByTitle_ValueAndCategory(request.title(), category)) {
            throw new CustomException("이미 작성된 문서입니다.");
        }

        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String content = imageS3Service.processImageReferences(request.content());
        final long contentLength = content.getBytes().length;

        final Dictionary dictionary = Dictionary.create(request.title(), category);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                request.author(), content, contentLength, clientIp, dictionary
        );

        validateDictionarySubmission(clientIp);

        dictionary.addHistory(dictionaryHistory);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        savedDictionary.updateModifiedAt(LocalDateTime.now());

        return new DictionarySaveResponse(savedDictionary.getCurrentHistory().getId());
    }

    @Transactional
    public DictionaryUpdateResponse update(
            final Long dictionaryId, final HttpServletRequest servletRequest, final DictionaryUpdateRequest request
    ) {
        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);

        if (dictionary.getStatus() != DictionaryStatus.ALL_ACTIVE) {
            throw new CustomException("편집할 수 없는 문서입니다.");
        }

        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String content = imageS3Service.processImageReferences(request.content());
        final long contentLength = content.getBytes().length;
        final long version = dictionary.getCurrentHistory().getVersion() + 1;

        final DictionaryHistory dictionaryHistory = DictionaryHistory.update(
                request.author(), content, version, contentLength, clientIp, dictionary
        );

        validateEditPermission(clientIp);

        dictionary.addHistory(dictionaryHistory);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        savedDictionary.updateModifiedAt(LocalDateTime.now());

        return new DictionaryUpdateResponse(savedDictionary.getCurrentHistory().getId());
    }

    @Transactional
    public void incrementViews(final Long dictionaryId, final Long count) {
        final Dictionary dictionary = dictionaryQueryService.getDictionary(dictionaryId);
        dictionary.incrementViews(count);
    }

    private void validateDictionarySubmission(final String clientIp) {
        final String key = String.format("dictionary-save:%s", clientIp);

        if (!redisService.setIfAbsent(key, "true", Duration.ofMinutes(1L))) {
            throw new CustomException("1분 후에 문서를 작성할 수 있습니다.");
        }
    }

    private void validateEditPermission(final String clientIp) {
        final String key = String.format("dictionary-update:%s", clientIp);

        if (!redisService.setIfAbsent(key, "true", Duration.ofMinutes(1L))) {
            throw new CustomException("1분 후에 문서를 편집할 수 있습니다.");
        }
    }
}
