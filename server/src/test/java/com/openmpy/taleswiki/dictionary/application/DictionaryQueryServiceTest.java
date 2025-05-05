package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionaryQueryServiceTest {

    @Autowired
    private DictionaryQueryService dictionaryQueryService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @DisplayName("[통과] 사전을 조회한다.")
    @Test
    void dictionary_query_service_test_01() {
        // given
        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                "작성자", "내용", 1L, "192.168.0.1", dictionary
        );

        dictionary.addHistory(dictionaryHistory);
        dictionaryRepository.save(dictionary);

        // when
        final Dictionary foundDictionary = dictionaryQueryService.getDictionary(dictionary.getId());

        // then
        final DictionaryHistory foundDictionaryHistory = foundDictionary.getHistories().getFirst();

        assertThat(foundDictionary.getTitle()).isEqualTo("제목");
        assertThat(foundDictionary.getCategory().name()).isEqualTo("PERSON");
        assertThat(foundDictionary.getStatus().name()).isEqualTo("ALL_ACTIVE");

        assertThat(foundDictionaryHistory.getAuthor()).isEqualTo("작성자");
        assertThat(foundDictionaryHistory.getContent()).isEqualTo("내용");
        assertThat(foundDictionaryHistory.getVersion()).isEqualTo(1);
        assertThat(foundDictionaryHistory.getSize()).isEqualTo(1L);
        assertThat(foundDictionaryHistory.getIp()).isEqualTo("192.168.0.1");
        assertThat(foundDictionaryHistory.getStatus().name()).isEqualTo("ALL_ACTIVE");
        assertThat(foundDictionaryHistory.getDictionary()).isEqualTo(foundDictionary);
    }

    @DisplayName("[예외] 사전을 조회하지 못한다.")
    @Test
    void 예외_dictionary_query_service_test_01() {
        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.getDictionary(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("찾을 수 없는 사전 번호입니다.");
    }
}