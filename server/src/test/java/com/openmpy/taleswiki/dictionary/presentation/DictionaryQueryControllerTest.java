package com.openmpy.taleswiki.dictionary.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse.DictionaryGetGroupItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse.DictionaryGetGroupItemsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse.DictionaryGetHistoriesItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse.DictionaryGetPopularItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetRandomResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response.DictionaryGetTop20ItemResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse.DictionarySearchDictionariesItemResponse;
import com.openmpy.taleswiki.helper.ControllerTestSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DictionaryQueryControllerTest extends ControllerTestSupport {

    @DisplayName("[통과] 최근 편집된 사전 TOP 20개를 조회한다.")
    @Test
    void dictionary_query_controller_test_01() throws Exception {
        // given
        final List<DictionaryGetTop20ItemResponse> dictionaries = new ArrayList<>();

        for (long i = 20; i >= 1; i--) {
            final DictionaryGetTop20ItemResponse item = new DictionaryGetTop20ItemResponse(
                    i, "제목" + i, "런너",
                    LocalDateTime.of(2025, 5, 1, 0, 0, 0).minusDays(20 - i)
            );

            dictionaries.add(item);
        }

        final DictionaryGetTop20Response response = new DictionaryGetTop20Response(dictionaries);

        // when
        when(dictionaryQueryService.getTop20Dictionaries()).thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/latest-modified")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.dictionaries").isArray())
                .andExpect(jsonPath("$.dictionaries[0].currentHistoryId").value(20))
                .andExpect(jsonPath("$.dictionaries[0].title").value("제목20"))
                .andExpect(jsonPath("$.dictionaries[0].category").value("런너"))
                .andExpect(jsonPath("$.dictionaries[0].createdAt").value("2025-05-01T00:00:00"))
                .andDo(
                        document(
                                "dictionary-getTop20Dictionaries",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("dictionaries[].currentHistoryId").description("최신 사전 기록 ID"),
                                        fieldWithPath("dictionaries[].title").description("제목"),
                                        fieldWithPath("dictionaries[].category").description("카테고리"),
                                        fieldWithPath("dictionaries[].createdAt").description("작성일자")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 카테고리별 사전 목록을 조회한다.")
    @Test
    void dictionary_query_controller_test_02() throws Exception {
        // given
        final DictionaryGetGroupItemResponse response1 = new DictionaryGetGroupItemResponse(1L, "가가", "all_active");
        final DictionaryGetGroupItemResponse response2 = new DictionaryGetGroupItemResponse(2L, "가나", "all_active");
        final DictionaryGetGroupItemResponse response3 = new DictionaryGetGroupItemResponse(3L, "가다", "all_active");
        final DictionaryGetGroupItemsResponse itemsResponse1 = new DictionaryGetGroupItemsResponse(
                'ㄱ', List.of(response1, response2, response3)
        );

        final DictionaryGetGroupItemResponse response4 = new DictionaryGetGroupItemResponse(4L, "나가", "all_active");
        final DictionaryGetGroupItemResponse response5 = new DictionaryGetGroupItemResponse(5L, "나나", "all_active");
        final DictionaryGetGroupItemResponse response6 = new DictionaryGetGroupItemResponse(6L, "나다", "all_active");
        final DictionaryGetGroupItemsResponse itemsResponse2 = new DictionaryGetGroupItemsResponse(
                'ㄴ', List.of(response4, response5, response6)
        );

        final DictionaryGetGroupResponse groupResponse = new DictionaryGetGroupResponse(
                List.of(itemsResponse1, itemsResponse2)
        );

        // when
        when(dictionaryQueryService.getGroupDictionaries(anyString())).thenReturn(groupResponse);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/categories/{categoryName}", "person")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.groups").isArray())
                .andExpect(jsonPath("$.groups[0].initial").value("ㄱ"))
                .andExpect(jsonPath("$.groups[0].dictionaries").isArray())
                .andExpect(jsonPath("$.groups[0].dictionaries[0].currentHistoryId").value(1L))
                .andExpect(jsonPath("$.groups[0].dictionaries[0].title").value("가가"))
                .andExpect(jsonPath("$.groups[0].dictionaries[0].status").value("all_active"))
                .andDo(
                        document(
                                "dictionary-getGroupDictionaries",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("categoryName").description("카테고리")
                                ),
                                responseFields(
                                        fieldWithPath("groups[].initial").description("제목 첫글자 이니셜"),
                                        fieldWithPath("groups[].dictionaries").description("사전 목록"),
                                        fieldWithPath("groups[].dictionaries[].currentHistoryId")
                                                .description("최근 사전 기록 ID"),
                                        fieldWithPath("groups[].dictionaries[].title").description("제목"),
                                        fieldWithPath("groups[].dictionaries[].status").description("상태")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 사전 기록을 조회한다.")
    @Test
    void dictionary_query_controller_test_03() throws Exception {
        // given
        final DictionaryHistoryResponse response = new DictionaryHistoryResponse(
                1L, 1L, "제목", "내용", "all_active", "read_only",
                LocalDateTime.of(2025, 5, 1, 0, 0, 0)
        );

        // when
        when(dictionaryQueryService.get(any(), anyLong())).thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/histories/{dictionaryHistoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.dictionaryId").value(1))
                .andExpect(jsonPath("$.dictionaryHistoryId").value(1))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.status").value("all_active"))
                .andExpect(jsonPath("$.historyStatus").value("read_only"))
                .andExpect(jsonPath("$.createdAt").value("2025-05-01T00:00:00"))
                .andDo(
                        document(
                                "dictionary-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("dictionaryHistoryId").description("사전 기록 ID")
                                ),
                                responseFields(
                                        fieldWithPath("dictionaryId").description("사전 ID"),
                                        fieldWithPath("dictionaryHistoryId").description("사전 기록 ID"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("status").description("사전 상태"),
                                        fieldWithPath("historyStatus").description("사전 기록 상태"),
                                        fieldWithPath("createdAt").description("작성일자")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 사전 기록 목록을 조회한다.")
    @Test
    void dictionary_query_controller_test_04() throws Exception {
        // given
        final List<DictionaryGetHistoriesItemResponse> responses = new ArrayList<>();

        for (long i = 3; i >= 1; i--) {
            final DictionaryGetHistoriesItemResponse item = new DictionaryGetHistoriesItemResponse(
                    i, i,
                    LocalDateTime.of(2025, 5, 1, 0, 0, 0).minusDays(i - 3),
                    10L, "작성자", "ALL_ACTIVE"
            );

            responses.add(item);
        }

        final DictionaryGetHistoriesResponse response = new DictionaryGetHistoriesResponse("제목", responses);

        // when
        when(dictionaryQueryService.getHistories(anyLong())).thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/{dictionaryId}/history", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.histories").isArray())
                .andExpect(jsonPath("$.histories[0].dictionaryHistoryId").value(3))
                .andExpect(jsonPath("$.histories[0].version").value(3))
                .andExpect(jsonPath("$.histories[0].createdAt").value("2025-05-01T00:00:00"))
                .andExpect(jsonPath("$.histories[0].size").value(10))
                .andExpect(jsonPath("$.histories[0].author").value("작성자"))
                .andExpect(jsonPath("$.histories[0].dictionaryHistoryStatus").value("ALL_ACTIVE"))
                .andDo(
                        document(
                                "dictionary-getHistories",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("dictionaryId").description("사전 ID")
                                ),
                                responseFields(
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("histories[].dictionaryHistoryId").description("사전 기록 ID"),
                                        fieldWithPath("histories[].version").description("버전"),
                                        fieldWithPath("histories[].createdAt").description("작성일자"),
                                        fieldWithPath("histories[].size").description("사이즈"),
                                        fieldWithPath("histories[].author").description("작성자"),
                                        fieldWithPath("histories[].dictionaryHistoryStatus").description("사전 기록 상태")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 제목으로 사전을 검색한다.")
    @Test
    void dictionary_query_controller_test_05() throws Exception {
        // given
        final List<DictionarySearchDictionariesItemResponse> responses = new ArrayList<>();

        for (long i = 1; i <= 3; i++) {
            final DictionarySearchDictionariesItemResponse item = new DictionarySearchDictionariesItemResponse(
                    i, "apple" + i, "런너"
            );

            responses.add(item);
        }

        final DictionarySearchDictionariesResponse response = new DictionarySearchDictionariesResponse(responses);

        // when
        when(dictionarySearchService.searchByTitle(anyString())).thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("title", "apple")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.hits").isArray())
                .andExpect(jsonPath("$.hits[0].currentHistoryId").value(1))
                .andExpect(jsonPath("$.hits[0].title").value("apple1"))
                .andExpect(jsonPath("$.hits[0].category").value("런너"))
                .andDo(
                        document(
                                "dictionary-searchByTitle",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("title").description("검색할 게시글 제목")
                                ),
                                responseFields(
                                        fieldWithPath("hits[].currentHistoryId").description("최근 사전 기록 ID"),
                                        fieldWithPath("hits[].title").description("제목"),
                                        fieldWithPath("hits[].category").description("카테고리")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 사전을 랜덤으로 조회한다.")
    @Test
    void dictionary_query_controller_test_06() throws Exception {
        // given
        final DictionaryGetRandomResponse response = new DictionaryGetRandomResponse(1L);

        // when
        when(dictionaryQueryService.getRandomDictionary()).thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/random")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.currentHistoryId").value(1))
                .andDo(
                        document(
                                "dictionary-getRandomDictionary",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("currentHistoryId").description("최근 사전 기록 ID")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 실시간 인기 사전을 조회한다.")
    @Test
    void dictionary_query_controller_test_07() throws Exception {
        // given
        final List<DictionaryGetPopularItemResponse> responses = new ArrayList<>();

        for (long i = 1; i <= 3; i++) {
            final DictionaryGetPopularItemResponse item = new DictionaryGetPopularItemResponse(i, "제목" + i, "런너");

            responses.add(item);
        }

        final DictionaryGetPopularResponse response = new DictionaryGetPopularResponse(responses);

        // when
        when(dictionaryQueryService.getPopular()).thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/dictionaries/popular")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.dictionaries").isArray())
                .andExpect(jsonPath("$.dictionaries[0].currentHistoryId").value(1))
                .andExpect(jsonPath("$.dictionaries[0].title").value("제목1"))
                .andExpect(jsonPath("$.dictionaries[0].category").value("런너"))
                .andDo(
                        document(
                                "dictionary-getPopular",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("dictionaries[].currentHistoryId").description("최근 사전 기록 ID"),
                                        fieldWithPath("dictionaries[].title").description("제목"),
                                        fieldWithPath("dictionaries[].category").description("카테고리")
                                )
                        )
                )
        ;
    }
}