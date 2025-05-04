package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionaryCommandServiceTest {

    @Autowired
    private DictionaryCommandService dictionaryCommandService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @DisplayName("[통과] 사전을 작성한다.")
    @Test
    void dictionary_save_test_01() {
        // given
        final HttpServletRequest mockHttpServletRequest = createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        // when
        dictionaryCommandService.save(mockHttpServletRequest, request);

        // then
        final Dictionary dictionary = dictionaryRepository.findAll().getFirst();
        final DictionaryHistory dictionaryHistory = dictionary.getHistories().getFirst();

        assertThat(dictionary.getTitle()).isEqualTo("제목");
        assertThat(dictionary.getCategory().name()).isEqualTo("PERSON");
        assertThat(dictionary.getStatus().name()).isEqualTo("ALL_ACTIVE");

        assertThat(dictionaryHistory.getAuthor()).isEqualTo("작성자");
        assertThat(dictionaryHistory.getContent()).isEqualTo("내용");
        assertThat(dictionaryHistory.getVersion()).isEqualTo(1);
        assertThat(dictionaryHistory.getSize()).isEqualTo(4L);
        assertThat(dictionaryHistory.getIp()).isEqualTo("192.168.0.1");
        assertThat(dictionaryHistory.getStatus().name()).isEqualTo("ALL_ACTIVE");
        assertThat(dictionaryHistory.getDictionary()).isEqualTo(dictionary);
    }

    @DisplayName("[통과] 사전을 수정한다.")
    @Test
    void dictionary_save_test_02() {
        // given
        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                "작성자", "내용", 1L, "192.168.0.1", dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        final HttpServletRequest mockHttpServletRequest = createMockHttpServletRequest();
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("작성자2", "내용2");

        // when
        dictionaryCommandService.update(savedDictionary.getId(), mockHttpServletRequest, request);

        // then
        final Dictionary foundDictionary = dictionaryRepository.findAll().getFirst();

        assertThat(foundDictionary.getTitle()).isEqualTo("제목");
        assertThat(foundDictionary.getCategory().name()).isEqualTo("PERSON");
        assertThat(foundDictionary.getStatus().name()).isEqualTo("ALL_ACTIVE");
        assertThat(foundDictionary.getHistories()).hasSize(2);

        final DictionaryHistory updatedDictionaryHistory = foundDictionary.getHistories().getLast();
        assertThat(updatedDictionaryHistory.getAuthor()).isEqualTo("작성자2");
        assertThat(updatedDictionaryHistory.getContent()).isEqualTo("내용2");
        assertThat(updatedDictionaryHistory.getVersion()).isEqualTo(2);
        assertThat(updatedDictionaryHistory.getSize()).isEqualTo(4L);
        assertThat(updatedDictionaryHistory.getIp()).isEqualTo("192.168.0.1");
        assertThat(updatedDictionaryHistory.getStatus().name()).isEqualTo("ALL_ACTIVE");
        assertThat(updatedDictionaryHistory.getDictionary()).isEqualTo(dictionary);
    }

    @DisplayName("[통과] 사전 상태를 변경한다.")
    @Test
    void dictionary_save_test_03() {
        // given
        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                "작성자", "내용", 1L, "192.168.0.1", dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        // when
        dictionaryCommandService.changeStatus(savedDictionary.getId(), "hidden");

        // then
        final Dictionary foundDictionary = dictionaryRepository.findAll().getFirst();

        assertThat(foundDictionary.getTitle()).isEqualTo("제목");
        assertThat(foundDictionary.getCategory().name()).isEqualTo("PERSON");
        assertThat(foundDictionary.getStatus().name()).isEqualTo("HIDDEN");
    }

    @DisplayName("[예외] 해당 카테고리에 이미 작성된 사전이다.")
    @Test
    void 예외_dictionary_save_test_01() {
        // given
        final HttpServletRequest mockHttpServletRequest = createMockHttpServletRequest();
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "person", "작성자", "내용");

        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                "작성자", "내용", 1L, "192.168.0.1", dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        dictionaryRepository.save(dictionary);

        // when & then
        assertThatThrownBy(() -> dictionaryCommandService.save(mockHttpServletRequest, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 작성된 사전입니다.");
    }

    private HttpServletRequest createMockHttpServletRequest() {
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        servletRequest.addHeader("X-Forwarded-For", "192.168.0.1");
        servletRequest.setContent("test".getBytes());
        return servletRequest;
    }
}