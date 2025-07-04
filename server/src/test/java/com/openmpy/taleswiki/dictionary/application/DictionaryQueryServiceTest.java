package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetRandomResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetVersionResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetVersionsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DictionaryQueryServiceTest extends ServiceTestSupport {

    @Autowired
    private DictionaryQueryService dictionaryQueryService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @DisplayName("[통과] 최근 작성/편집된 문서 20개를 조회한다.")
    @Test
    void dictionary_query_service_test_01() {
        // given
        for (int i = 1; i <= 20; i++) {
            final DictionaryStatus dictionaryStatus = (i % 2 == 0) ?
                    DictionaryStatus.HIDDEN : DictionaryStatus.ALL_ACTIVE;

            final Dictionary dictionary = Dictionary.builder()
                    .title("제목" + i)
                    .category(DictionaryCategory.PERSON)
                    .status(dictionaryStatus)
                    .view(0L)
                    .build();

            final DictionaryHistory dictionaryHistory = DictionaryHistory.builder()
                    .author("작성자")
                    .version(1L)
                    .size(1L)
                    .ip("127.0.0.1")
                    .status(DictionaryStatus.ALL_ACTIVE)
                    .content("내용")
                    .dictionary(dictionary)
                    .build();

            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        // when
        final DictionaryGetTop20Response response = dictionaryQueryService.getTop20Dictionaries();

        // then
        assertThat(response.dictionaries()).hasSize(10);
    }

    @DisplayName("[통과] 문서를 카테고리별로 조회하고 이니셜로 그룹화한다.")
    @Test
    void dictionary_query_service_test_02() {
        // given
        final Dictionary dictionary01 = Dictionary.create("가나다", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory01 = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary01
        );

        final Dictionary dictionary02 = Dictionary.create("나다가", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory02 = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary01
        );

        final Dictionary dictionary03 = Dictionary.create("다가나", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory03 = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary01
        );

        dictionary01.addHistory(dictionaryHistory01);
        dictionary02.addHistory(dictionaryHistory02);
        dictionary03.addHistory(dictionaryHistory03);

        dictionaryRepository.saveAll(List.of(dictionary01, dictionary02, dictionary03));

        // when
        final DictionaryGetGroupResponse response = dictionaryQueryService.getGroupDictionaries("person");

        // then
        assertThat(response.groups()).hasSize(3);
        assertThat(response.groups().getFirst().initial()).isEqualTo('ㄱ');
        assertThat(response.groups().getFirst().dictionaries().getFirst().title()).isEqualTo("가나다");
        assertThat(response.groups().getLast().initial()).isEqualTo('ㄷ');
        assertThat(response.groups().getLast().dictionaries().getFirst().title()).isEqualTo("다가나");
    }

    @DisplayName("[통과] 문서 기록을 처음으로 조회한다.")
    @Test
    void dictionary_query_service_test_03() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final Dictionary dictionary01 = dictionaryRepository.save(Fixture.createDictionary());
        final Dictionary dictionary02 = dictionaryRepository.save(Fixture.createDictionary());

        dictionary02.changeStatus(DictionaryStatus.HIDDEN);
        dictionary02.getCurrentHistory().changeStatus(DictionaryStatus.HIDDEN);

        // when
        final DictionaryHistoryResponse response01 = dictionaryQueryService.get(servletRequest,
                dictionary01.getCurrentHistory().getId());
        final DictionaryHistoryResponse response02 = dictionaryQueryService.get(servletRequest,
                dictionary02.getCurrentHistory().getId());

        // then
        assertThat(response01.title()).isEqualTo("제목");
        assertThat(response01.content()).isEqualTo("내용");
        assertThat(response01.status()).isEqualTo(DictionaryStatus.ALL_ACTIVE.name());
        assertThat(response01.historyStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE.name());

        assertThat(response02.title()).isEqualTo("제목");
        assertThat(response02.content()).isNull();
        assertThat(response02.status()).isEqualTo(DictionaryStatus.HIDDEN.name());
        assertThat(response02.historyStatus()).isEqualTo(DictionaryStatus.HIDDEN.name());

        final String key = String.format("dictionary-view_%d:%s", dictionary01.getId(), "127.0.0.1");
        assertThat(redisTemplate.hasKey(key)).isTrue();

        final String viewKey = String.format("dictionary-view:%d", dictionary01.getId());
        assertThat(redisTemplate.opsForValue().get(viewKey)).isEqualTo(1);
    }

    @DisplayName("[통과] 문서 기록 목록을 조회한다.")
    @Test
    void dictionary_query_service_test_04() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        // when
        final DictionaryGetHistoriesResponse response = dictionaryQueryService.getHistories(dictionary.getId());

        // then
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.histories()).hasSize(1);
    }

    @DisplayName("[통과] 문서 버전 목록을 조회한다.")
    @Test
    void dictionary_query_service_test_05() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        // when
        final DictionaryGetVersionsResponse response = dictionaryQueryService.getVersions(dictionary.getId());

        // then
        assertThat(response.versions()).hasSize(1);
    }

    @DisplayName("[통과] 문서를 랜덤으로 조회한다.")
    @Test
    void dictionary_query_service_test_06() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());
        redisTemplate.opsForSet().add("dictionary:ids", List.of(dictionary.getId()).toArray());

        // when
        final DictionaryGetRandomResponse response = dictionaryQueryService.getRandomDictionary();

        // then
        assertThat(response.currentHistoryId()).isEqualTo(dictionary.getCurrentHistory().getId());
    }

    @DisplayName("[통과] 문서 버전을 조회한다.")
    @Test
    void dictionary_query_service_test_07() {
        // given
        final Dictionary dictionary01 = dictionaryRepository.save(Fixture.createDictionary());
        final Dictionary dictionary02 = dictionaryRepository.save(Fixture.createDictionary());
        dictionary02.getCurrentHistory().changeStatus(DictionaryStatus.HIDDEN);

        // when
        final DictionaryGetVersionResponse response01 = dictionaryQueryService.getVersion(dictionary01.getId(), 1L);
        final DictionaryGetVersionResponse response02 = dictionaryQueryService.getVersion(dictionary02.getId(), 1L);

        // then
        assertThat(response01.content()).isEqualTo("내용");
        assertThat(response02.content()).isNull();
    }

    @DisplayName("[통과] 실시간 인기 문서 목록을 조회한다.")
    @Test
    void dictionary_query_service_test_08() {
        // given
        final Dictionary dictionary01 = Dictionary.create("가나다", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory01 = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary01
        );

        final Dictionary dictionary02 = Dictionary.create("나다가", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory02 = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary01
        );

        final Dictionary dictionary03 = Dictionary.create("다가나", DictionaryCategory.PERSON);
        final DictionaryHistory dictionaryHistory03 = DictionaryHistory.create(
                "작성자", "내용", 10L, "127.0.0.1", dictionary01
        );

        dictionary01.addHistory(dictionaryHistory01);
        dictionary02.addHistory(dictionaryHistory02);
        dictionary03.addHistory(dictionaryHistory03);

        final Dictionary savedDictionary01 = dictionaryRepository.save(dictionary01);
        final Dictionary savedDictionary02 = dictionaryRepository.save(dictionary02);
        final Dictionary savedDictionary03 = dictionaryRepository.save(dictionary03);

        redisTemplate.opsForZSet().incrementScore("popular_dictionaries", savedDictionary01.getId(), 1.0);
        redisTemplate.opsForZSet().incrementScore("popular_dictionaries", savedDictionary02.getId(), 0.9);
        redisTemplate.opsForZSet().incrementScore("popular_dictionaries", savedDictionary03.getId(), 0.8);

        // when
        final DictionaryGetPopularResponse response = dictionaryQueryService.getPopular();

        // then
        assertThat(response.dictionaries()).hasSize(3);
        assertThat(response.dictionaries().getFirst().currentHistoryId())
                .isEqualTo(savedDictionary01.getCurrentHistory().getId());
        assertThat(response.dictionaries().getLast().currentHistoryId())
                .isEqualTo(savedDictionary03.getCurrentHistory().getId());
    }

    @DisplayName("[통과] 문서 기록을 중복으로 조회한다.")
    @Test
    void dictionary_query_service_test_09() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        final String key = String.format("dictionary-view_%d:%s", dictionary.getId(), "127.0.0.1");
        redisTemplate.opsForSet().add(key, "true");

        // when
        final DictionaryHistoryResponse response = dictionaryQueryService.get(servletRequest,
                dictionary.getCurrentHistory().getId());

        // then
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isEqualTo("내용");
        assertThat(response.status()).isEqualTo(DictionaryStatus.ALL_ACTIVE.name());
        assertThat(response.historyStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE.name());
    }

    @DisplayName("[예외] 문서 기록 번호를 찾을 수 없다.")
    @Test
    void 예외_dictionary_query_service_test_01() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();

        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.get(servletRequest, 999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 문서 기록 번호입니다.");
    }

    @DisplayName("[예외] 숨김 처리된 문서의 기록을 조회할 수 없다.")
    @Test
    void 예외_dictionary_query_service_test_02() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());
        dictionary.changeStatus(DictionaryStatus.HIDDEN);

        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.getHistories(dictionary.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("숨김 처리된 문서입니다.");
    }

    @DisplayName("[예외] 문서 버전을 조회할 수 없다.")
    @Test
    void 예외_dictionary_query_service_test_03() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.getVersion(dictionary.getId(), 999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 문서 버전입니다.");
    }

    @DisplayName("[예외] 문서를 조회할 수 없다.")
    @Test
    void 예외_dictionary_query_service_test_04() {
        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.getDictionary(999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 문서 번호입니다. [999]");
    }
}