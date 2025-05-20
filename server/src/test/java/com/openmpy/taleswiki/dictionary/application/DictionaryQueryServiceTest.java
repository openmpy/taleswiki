package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse.DictionaryGetGroupItemsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse.DictionaryGetHistoriesItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse.DictionaryGetPopularItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetRandomResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response.DictionaryGetTop20ItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import com.openmpy.taleswiki.helper.Fixture;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionaryQueryServiceTest {

    @Autowired
    private DictionaryQueryService dictionaryQueryService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @MockitoBean
    private RedisService redisService;

    @DisplayName("[통과] 최근 작성/편집된 사전 20개를 업데이트 날짜를 기준으로 내림차순하여 조회한다.")
    @Test
    void dictionary_query_service_test_01() {
        // given
        for (int i = 1; i <= 20; i++) {
            final Dictionary dictionary = Dictionary.create("제목" + i, DictionaryCategory.PERSON);
            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자", "내용", 10L, "127.0.0.1", dictionary
            );

            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        // when
        final DictionaryGetTop20Response response = dictionaryQueryService.getTop20Dictionaries();

        // then
        final List<DictionaryGetTop20ItemResponse> dictionaries = response.dictionaries();

        assertThat(dictionaries).hasSize(20);
        assertThat(dictionaries.getFirst().title()).isEqualTo("제목20");
        assertThat(dictionaries.getLast().title()).isEqualTo("제목1");
    }

    @DisplayName("[통과] 카테고리별 사전 목록을 조회한다.")
    @Test
    void dictionary_query_service_test_02() {
        // given
        for (int i = 1; i <= 9; i++) {
            final Dictionary dictionary;

            if (i <= 3) {
                dictionary = Dictionary.create("가나다" + i, DictionaryCategory.PERSON);
            } else if (i <= 6) {
                dictionary = Dictionary.create("abc" + i, DictionaryCategory.PERSON);
            } else {
                dictionary = Dictionary.create("123" + i, DictionaryCategory.PERSON);
            }

            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자", "내용", 10L, "127.0.0.1", dictionary
            );

            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        // when
        final DictionaryGetGroupResponse response = dictionaryQueryService.getGroupDictionaries("person");

        // then
        final List<DictionaryGetGroupItemsResponse> groups = response.groups();

        assertThat(groups).hasSize(3);
        assertThat(groups.get(0).initial()).isEqualTo('1');
        assertThat(groups.get(0).dictionaries()).hasSize(3);
        assertThat(groups.get(1).initial()).isEqualTo('A');
        assertThat(groups.get(1).dictionaries()).hasSize(3);
        assertThat(groups.get(2).initial()).isEqualTo('ㄱ');
        assertThat(groups.get(2).dictionaries()).hasSize(3);
    }

    @DisplayName("[통과] 사전을 조회한다.")
    @Test
    void dictionary_query_service_test_03() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        final DictionaryHistory dictionaryHistory = savedDictionary.getCurrentHistory();

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(false);

        // when
        final DictionaryHistoryResponse response = dictionaryQueryService.get(
                servletRequest, dictionaryHistory.getId()
        );

        // then
        assertThat(response.dictionaryId()).isEqualTo(savedDictionary.getId());
        assertThat(response.dictionaryHistoryId()).isEqualTo(dictionaryHistory.getId());
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isEqualTo("내용");
        assertThat(response.status()).isEqualTo("ALL_ACTIVE");
        assertThat(response.historyStatus()).isEqualTo("ALL_ACTIVE");
        assertThat(response.createdAt()).isEqualTo(dictionaryHistory.getCreatedAt());
    }

    @DisplayName("[통과] 전체 숨김 처리된 사전을 조회한다.")
    @Test
    void dictionary_query_service_test_04() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        final DictionaryHistory dictionaryHistory = savedDictionary.getCurrentHistory();

        savedDictionary.changeStatus("hidden");

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();

        // when
        final DictionaryHistoryResponse response = dictionaryQueryService.get(
                servletRequest, dictionaryHistory.getId()
        );

        // then
        assertThat(response.dictionaryId()).isEqualTo(savedDictionary.getId());
        assertThat(response.dictionaryHistoryId()).isEqualTo(dictionaryHistory.getId());
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isNull();
        assertThat(response.status()).isEqualTo("HIDDEN");
        assertThat(response.historyStatus()).isEqualTo("ALL_ACTIVE");
        assertThat(response.createdAt()).isBefore(dictionaryHistory.getCreatedAt());
    }

    @DisplayName("[통과] 숨김 처리된 사전을 조회한다.")
    @Test
    void dictionary_query_service_test_05() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        final DictionaryHistory dictionaryHistory = savedDictionary.getCurrentHistory();

        dictionaryHistory.changeStatus("hidden");

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();

        // when
        final DictionaryHistoryResponse response = dictionaryQueryService.get(
                servletRequest, dictionaryHistory.getId()
        );

        // then
        assertThat(response.dictionaryId()).isEqualTo(savedDictionary.getId());
        assertThat(response.dictionaryHistoryId()).isEqualTo(dictionaryHistory.getId());
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isNull();
        assertThat(response.status()).isEqualTo("ALL_ACTIVE");
        assertThat(response.historyStatus()).isEqualTo("HIDDEN");
        assertThat(response.createdAt()).isEqualTo(dictionaryHistory.getCreatedAt());
    }

    @DisplayName("[통과] 사전 기록 목록을 조회한다.")
    @Test
    void dictionary_query_service_test_06() {
        // given
        final Dictionary dictionary = Dictionary.create("제목", DictionaryCategory.PERSON);
        Dictionary savedDictionary = null;

        for (long i = 1; i <= 5; i++) {
            final DictionaryHistory dictionaryHistory = DictionaryHistory.update(
                    "작성자" + i, "내용" + i, i, 10L, "127.0.0.1", dictionary
            );

            dictionary.addHistory(dictionaryHistory);
            savedDictionary = dictionaryRepository.save(dictionary);
        }

        // when
        final DictionaryGetHistoriesResponse response = dictionaryQueryService.getHistories(savedDictionary.getId());

        // then
        final List<DictionaryGetHistoriesItemResponse> histories = response.histories();

        assertThat(response.title()).isEqualTo("제목");
        assertThat(histories).hasSize(5);
        assertThat(histories.getFirst().createdAt()).isAfter(histories.getLast().createdAt());

        assertThat(histories.getFirst().version()).isEqualTo(5L);
        assertThat(histories.getFirst().size()).isEqualTo(10L);
        assertThat(histories.getFirst().author()).isEqualTo("작성자5");
        assertThat(histories.getFirst().dictionaryHistoryStatus()).isEqualTo("ALL_ACTIVE");

        assertThat(histories.getLast().version()).isEqualTo(1L);
        assertThat(histories.getLast().size()).isEqualTo(10L);
        assertThat(histories.getLast().author()).isEqualTo("작성자1");
        assertThat(histories.getLast().dictionaryHistoryStatus()).isEqualTo("ALL_ACTIVE");
    }

    @DisplayName("[통과] 사전을 랜덤으로 조회한다.")
    @Test
    void dictionary_query_service_test_07() {
        // given
        for (int i = 1; i <= 10; i++) {
            final Dictionary dictionary = Dictionary.create("제목" + i, DictionaryCategory.PERSON);
            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자" + i, "내용" + i, 10L, "127.0.0.1", dictionary
            );
            final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

            savedDictionary.addHistory(dictionaryHistory);
        }

        // when
        final DictionaryGetRandomResponse response = dictionaryQueryService.getRandomDictionary();

        // then
        final List<Long> savedHistoryIds = dictionaryRepository.findAll().stream()
                .flatMap(d -> d.getHistories().stream())
                .map(DictionaryHistory::getId)
                .toList();

        assertThat(response.currentHistoryId()).isIn(savedHistoryIds);
    }

    @DisplayName("[통과] 사전을 조회한다.")
    @Test
    void dictionary_query_service_test_08() {
        // given
        final Dictionary savedDictionary = dictionaryRepository.save(Fixture.createDictionary());

        // when
        final Dictionary dictionary = dictionaryQueryService.getDictionary(savedDictionary.getId());

        // then
        assertThat(dictionary).isEqualTo(savedDictionary);
    }

    @DisplayName("[통과] 사전 기록을 조회한다.")
    @Test
    void dictionary_query_service_test_09() {
        // given
        final Dictionary savedDictionary = dictionaryRepository.save(Fixture.createDictionary());
        final DictionaryHistory savedDictionaryHistory = savedDictionary.getCurrentHistory();

        // when
        final DictionaryHistory dictionaryHistory = dictionaryQueryService.getDictionaryHistory(
                savedDictionaryHistory.getId()
        );

        // then
        assertThat(dictionaryHistory).isEqualTo(savedDictionaryHistory);
    }

    @DisplayName("[통과] 사전을 처음 조회한다.")
    @Test
    void dictionary_query_service_test_10() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        final DictionaryHistory dictionaryHistory = savedDictionary.getCurrentHistory();

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();

        // stub
        when(redisService.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);
        when(redisService.incrementScore(anyString(), any(), anyDouble())).thenReturn(1.0);

        // when
        final DictionaryHistoryResponse response = dictionaryQueryService.get(
                servletRequest, dictionaryHistory.getId()
        );

        // then
        assertThat(response.dictionaryId()).isEqualTo(savedDictionary.getId());
        assertThat(response.dictionaryHistoryId()).isEqualTo(dictionaryHistory.getId());
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isEqualTo("내용");
        assertThat(response.status()).isEqualTo("ALL_ACTIVE");
        assertThat(response.historyStatus()).isEqualTo("ALL_ACTIVE");
        assertThat(response.createdAt()).isEqualTo(dictionaryHistory.getCreatedAt());

        verify(redisService).setIfAbsent(anyString(), anyString(), any());
    }

    @DisplayName("[통과] 인기 사전 목록을 조회한다.")
    @Test
    void dictionary_query_service_test_11() {
        // given
        for (int i = 1; i <= 10; i++) {
            final Dictionary dictionary = Dictionary.create("제목" + i, DictionaryCategory.PERSON);
            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자" + i, "내용" + i, 10L, "127.0.0.1", dictionary
            );
            final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

            savedDictionary.addHistory(dictionaryHistory);
        }

        final Set<TypedTuple<Object>> tuples = new LinkedHashSet<>();
        double count = 0.0;

        for (final Dictionary foundDictionary : dictionaryRepository.findAll()) {
            final double score = 10.0 - count;
            final TypedTuple<Object> tuple = new DefaultTypedTuple<>(
                    foundDictionary.getCurrentHistory().getId(), score
            );

            tuples.add(tuple);
            count += 0.1;
        }

        // stub
        when(redisService.reverseRangeWithScores("popular_dictionaries", 0, 9)).thenReturn(tuples);

        // when
        final DictionaryGetPopularResponse response = dictionaryQueryService.getPopular();

        // then
        final List<DictionaryGetPopularItemResponse> dictionaries = response.dictionaries();

        assertThat(dictionaries).hasSizeLessThanOrEqualTo(10);
        assertThat(dictionaries.getFirst().currentHistoryId()).isLessThan(dictionaries.getLast().currentHistoryId());
    }

    @DisplayName("[예외] 숨김 처리된 사전 목록을 조회한다.")
    @Test
    void 예외_dictionary_query_service_test_01() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        dictionary.changeStatus("hidden");

        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.getHistories(savedDictionary.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("숨김 처리된 문서입니다.");
    }

    @DisplayName("[예외] 사전이 존재하지 않을 떄 랜덤으로 조회할 수 없다.")
    @Test
    void 예외_dictionary_query_service_test_02() {
        // when & then
        assertThatThrownBy(() -> dictionaryQueryService.getRandomDictionary())
                .isInstanceOf(CustomException.class)
                .hasMessage("문서를 찾지 못했습니다.");
    }
}