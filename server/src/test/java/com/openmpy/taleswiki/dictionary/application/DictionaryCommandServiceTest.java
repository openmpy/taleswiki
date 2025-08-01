package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySaveResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryUpdateResponse;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DictionaryCommandServiceTest extends ServiceTestSupport {

    @Autowired
    private DictionaryCommandService dictionaryCommandService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @DisplayName("[통과] 문서를 작성한다.")
    @Test
    void dictionary_command_service_test_01() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        // stub
        when(imageS3Service.processImageReferences(anyString())).thenReturn("내용");

        // when
        final DictionarySaveResponse response = dictionaryCommandService.save(servletRequest, request);

        // then
        assertThat(response.dictionaryHistoryId()).isNotNull();
    }

    @DisplayName("[통과] 문서를 편집한다.")
    @Test
    void dictionary_command_service_test_02() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("수정_작성자", "수정_내용");

        // stub
        when(imageS3Service.processImageReferences(anyString())).thenReturn("수정_내용");

        // when
        final DictionaryUpdateResponse response = dictionaryCommandService.update(
                dictionary.getId(), servletRequest, request
        );

        // then
        assertThat(response.dictionaryHistoryId()).isNotNull();
    }

    @DisplayName("[통과] 문서 조회수를 증가시킨다.")
    @Test
    void dictionary_command_service_test_03() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        // when
        dictionaryCommandService.incrementViews(dictionary.getId(), 10L);

        // then
        final Dictionary foundDictionary = dictionaryRepository.findById(dictionary.getId()).get();
        assertThat(foundDictionary.getView()).isEqualTo(10L);
    }

    @DisplayName("[예외] 문서 카테고리가 올바르지 않다.")
    @Test
    void 예외_dictionary_command_service_test_01() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "wrong", "작성자", "내용");

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.save(servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 카테고리입니다.");
    }

    @DisplayName("[예외] 이미 작성된 문서이다.")
    @Test
    void 예외_dictionary_command_service_test_02() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        dictionaryRepository.save(dictionary);

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.save(servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 작성된 문서입니다.");
    }

    @DisplayName("[예외] 편집할 수 없는 문서이다.")
    @Test
    void 예외_dictionary_command_service_test_03() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        dictionary.changeStatus(DictionaryStatus.HIDDEN);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("수정_작성자", "수정_내용");

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.update(savedDictionary.getId(), servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("편집할 수 없는 문서입니다.");
    }

    @DisplayName("[예외] 문서를 연속으로 작성할 수 없다.")
    @Test
    void 예외_dictionary_command_service_test_04() {
        // given
        final String key = String.format("dictionary-save:%s", "127.0.0.1");
        redisTemplate.opsForValue().set(key, "127.0.0.1");

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        // stub
        when(imageS3Service.processImageReferences(anyString())).thenReturn("내용");

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.save(servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("1분 후에 문서를 작성할 수 있습니다.");
    }

    @DisplayName("[예외] 문서를 연속으로 수정할 수 없다.")
    @Test
    void 예외_dictionary_command_service_test_05() {
        // given
        final String key = String.format("dictionary-update:%s", "127.0.0.1");
        redisTemplate.opsForValue().set(key, "127.0.0.1");

        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("수정_작성자", "수정_내용");

        // stub
        when(imageS3Service.processImageReferences(anyString())).thenReturn("수정_내용");

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.update(dictionary.getId(), servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("1분 후에 문서를 편집할 수 있습니다.");
    }
}