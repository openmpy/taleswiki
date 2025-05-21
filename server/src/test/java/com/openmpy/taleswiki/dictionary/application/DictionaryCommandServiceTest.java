package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.TestcontainerSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionaryCommandServiceTest extends TestcontainerSupport {

    @Autowired
    private DictionaryCommandService dictionaryCommandService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @MockitoBean
    private ImageS3Service imageS3Service;

    @MockitoBean
    private RedisService redisService;

    @DisplayName("[통과] 사전을 작성한다.")
    @Test
    void dictionary_command_service_test_01() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자",
                "![테스트1](http://localhost:8080/images/tmp/test1.webp)\n테스트"
        );

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);

        // when
        dictionaryCommandService.save(servletRequest, request);

        // then
        final Dictionary dictionary = dictionaryRepository.findAll().getFirst();
        final DictionaryHistory dictionaryHistory = dictionary.getCurrentHistory();

        assertThat(dictionary.getTitle()).isEqualTo("제목");
        assertThat(dictionary.getCategory()).isEqualTo(DictionaryCategory.PERSON);
        assertThat(dictionaryHistory.getAuthor()).isEqualTo("작성자");
        assertThat(dictionaryHistory.getContent()).isEqualTo(
                "![테스트1](http://localhost:8080/images/tmp/test1.webp)\n테스트"
        );

        verify(imageS3Service).moveToBaseDirectory("test1.webp");
    }

    @DisplayName("[통과] 사전을 수정한다.")
    @Test
    void dictionary_command_service_test_02() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("작성자2", "내용2");

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);

        // when
        dictionaryCommandService.update(savedDictionary.getId(), servletRequest, request);

        // then
        final Dictionary foundDictionary = dictionaryRepository.findAll().getFirst();
        final DictionaryHistory dictionaryHistory = dictionary.getCurrentHistory();

        assertThat(foundDictionary.getHistories()).hasSize(2);
        assertThat(dictionaryHistory.getAuthor()).isEqualTo("작성자2");
        assertThat(dictionaryHistory.getContent()).isEqualTo("내용2");
    }

    @DisplayName("[통과] 사전 조회수가 증가한다.")
    @Test
    void dictionary_command_service_test_03() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        // when
        dictionaryCommandService.incrementViews(savedDictionary.getId(), 1L);

        // then
        assertThat(savedDictionary.getView()).isEqualTo(1L);
    }

    @DisplayName("[예외] 이미 작성된 사전이다.")
    @Test
    void 예외_dictionary_command_service_test_01() {
        // given
        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        dictionaryRepository.save(dictionary);

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.save(servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 작성된 문서입니다.");
    }

    @DisplayName("[예외] 사전을 1분 이내 다시 작성한다.")
    @Test
    void 예외_dictionary_command_service_test_02() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.save(servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("1분 후에 문서를 작성할 수 있습니다.");
    }

    @DisplayName("[예외] 수정할 수 없는 사전이다.")
    @Test
    void 예외_dictionary_command_service_test_03() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        dictionary.changeStatus("hidden");
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("작성자2", "내용2");

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.update(savedDictionary.getId(), servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("편집할 수 없는 문서입니다.");
    }

    @DisplayName("[예외] 사전을 1분 이내 다시 수정한다.")
    @Test
    void 예외_dictionary_command_service_test_04() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("작성자2", "내용2");

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.update(savedDictionary.getId(), servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("1분 후에 문서를 편집할 수 있습니다.");
    }
}