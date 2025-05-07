package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.common.application.ImageService;
import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.FileLoaderUtil;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DictionaryCommandService {

    private static final String IMAGE_URL_PATTERN = "(!\\[[^]]*]\\(http://localhost:8080/images)/tmp/([a-f0-9\\-]+\\.webp\\))";
    private static final int REDIS_LOCK_EXPIRATION_DURATION = 60 * 1000;

    private final DictionaryQueryService dictionaryQueryService;
    private final ImageService imageService;
    private final RedisService redisService;
    private final DictionaryRepository dictionaryRepository;

    @Transactional
    public void save(final HttpServletRequest servletRequest, final DictionarySaveRequest request) {
        final DictionaryCategory category = DictionaryCategory.fromName(request.category());

        if (dictionaryRepository.existsByTitle_ValueAndCategory(request.title(), category)) {
            throw new CustomException("이미 작성된 사전입니다.");
        }

        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String key = String.format("save-dictionary:%s", clientIp);

        if (!redisService.acquireLock(key, clientIp, REDIS_LOCK_EXPIRATION_DURATION)) {
            throw new CustomException("1분 후에 사전을 작성할 수 있습니다.");
        }

        final String content = processImageReferences(request.content());
        final long contentLength = content.getBytes().length;

        final Dictionary dictionary = Dictionary.create(request.title(), category);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                request.author(), content, contentLength, clientIp, dictionary
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
            throw new CustomException("편집할 수 없는 사전입니다.");
        }

        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String key = String.format("update-dictionary:%s", clientIp);

        if (!redisService.acquireLock(key, clientIp, REDIS_LOCK_EXPIRATION_DURATION)) {
            throw new CustomException("1분 후에 사전을 편집할 수 있습니다.");
        }

        final String content = processImageReferences(request.content());
        final long contentLength = content.getBytes().length;
        final long version = dictionary.getCurrentHistory().getVersion() + 1;

        final DictionaryHistory dictionaryHistory = DictionaryHistory.update(
                request.author(), content, version, contentLength, clientIp, dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        return new DictionaryUpdateResponse(savedDictionary.getCurrentHistory().getId());
    }

    private String processImageReferences(final String content) {
        final List<String> fileNames = FileLoaderUtil.extractImageFileNames(content);
        for (final String fileName : fileNames) {
            imageService.moveToBaseDirectory(fileName);
        }

        final Pattern pattern = Pattern.compile(IMAGE_URL_PATTERN);
        final Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("$1/$2");
    }
}
