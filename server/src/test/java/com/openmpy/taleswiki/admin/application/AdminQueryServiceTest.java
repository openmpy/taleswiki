package com.openmpy.taleswiki.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.openmpy.taleswiki.admin.domain.entity.Blacklist;
import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.admin.dto.response.AdminGetBlacklistResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesHistoriesResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AdminQueryServiceTest extends ServiceTestSupport {

    @Autowired
    private AdminQueryService adminQueryService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private BlacklistRepository blacklistRepository;

    @DisplayName("[통과] 어드민 토큰인지 확인한다.")
    @Test
    void admin_query_service_test_01() {
        // when & then
        assertDoesNotThrow(() -> adminQueryService.me("success"));
    }

    @DisplayName("[통과] 사전 목록을 페이지 형식으로 조회한다.")
    @Test
    void admin_query_service_test_02() {
        // given
        for (int i = 1; i <= 10; i++) {
            final Dictionary dictionary = Dictionary.create("제목" + i, DictionaryCategory.PERSON);
            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자" + i, "내용" + i, 10L, "127.0.0.1", dictionary
            );

            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        // when
        final PaginatedResponse<AdminGetDictionariesResponse> response = adminQueryService.getDictionaries(
                "success", 0, 10
        );

        // then
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.content().getFirst().title()).isEqualTo("제목10");
        assertThat(response.content().getLast().title()).isEqualTo("제목1");
    }

    @DisplayName("[통과] 사전 기록 목록을 페이지 형식으로 조회한다.")
    @Test
    void admin_query_service_test_03() {
        // given
        for (int i = 1; i <= 10; i++) {
            final Dictionary dictionary = Dictionary.create("제목" + i, DictionaryCategory.PERSON);
            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자" + i, "내용" + i, 10L, "127.0.0.1", dictionary
            );

            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        // when
        final PaginatedResponse<AdminGetDictionariesHistoriesResponse> response = adminQueryService.getDictionariesHistories(
                "success", 0, 10
        );

        // then
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.content().getFirst().title()).isEqualTo("제목10");
        assertThat(response.content().getLast().title()).isEqualTo("제목1");
    }

    @DisplayName("[통과] 블랙리스트 목록을 페이지 형식으로 조회한다.")
    @Test
    void admin_query_service_test_04() {
        // given
        for (int i = 1; i <= 10; i++) {
            final Blacklist blacklist = Blacklist.create("127.0.0." + i, "사유" + i);
            blacklistRepository.save(blacklist);
        }

        // when
        final PaginatedResponse<AdminGetBlacklistResponse> response = adminQueryService.getBlacklist("success", 0, 10);

        // then
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.content().getFirst().ip()).isEqualTo("127.0.0.10");
        assertThat(response.content().getLast().ip()).isEqualTo("127.0.0.1");
    }

    @DisplayName("[예외] 잘못된 어드민 토큰이다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(strings = "fail")
    @NullAndEmptySource
    void 예외_admin_query_service_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> adminQueryService.validateToken(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("잘못된 토큰 값입니다.");
    }
}