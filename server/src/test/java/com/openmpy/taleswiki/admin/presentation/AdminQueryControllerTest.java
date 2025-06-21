package com.openmpy.taleswiki.admin.presentation;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openmpy.taleswiki.admin.dto.response.AdminGetBlacklistResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesHistoriesResponse;
import com.openmpy.taleswiki.admin.dto.response.AdminGetDictionariesResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.helper.ControllerTestSupport;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

class AdminQueryControllerTest extends ControllerTestSupport {

    @DisplayName("[통과] 모든 사전 목록을 페이지 형식으로 조회한다.")
    @Test
    void admin_query_controller_test_02() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        final List<AdminGetDictionariesResponse> responses = new ArrayList<>();

        for (long i = 10; i >= 1; i--) {
            final AdminGetDictionariesResponse response = new AdminGetDictionariesResponse(
                    i, "제목" + i, "런너", "ALL_ACTIVE"
            );

            responses.add(response);
        }

        final PageImpl<AdminGetDictionariesResponse> pageResponse = new PageImpl<>(
                responses, PageRequest.of(0, 10), 2
        );
        final PaginatedResponse<AdminGetDictionariesResponse> paginatedResponse = PaginatedResponse.of(
                pageResponse
        );

        // when
        when(adminQueryService.getDictionaries(anyLong(), anyInt(), anyInt())).thenReturn(paginatedResponse);

        // then
        mockMvc.perform(get("/api/v1/admin/dictionaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].dictionaryId").value(10))
                .andExpect(jsonPath("$.content[0].title").value("제목10"))
                .andExpect(jsonPath("$.content[0].category").value("런너"))
                .andExpect(jsonPath("$.content[0].status").value("ALL_ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andDo(print())
                .andDo(
                        document(
                                "admin-getDictionaries",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("사이즈")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 모든 사전 기록 목록을 페이지 형식으로 조회한다.")
    @Test
    void admin_query_controller_test_03() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        final List<AdminGetDictionariesHistoriesResponse> responses = new ArrayList<>();

        for (long i = 10; i >= 1; i--) {
            final AdminGetDictionariesHistoriesResponse response = new AdminGetDictionariesHistoriesResponse(
                    i, "제목" + i, "127.0.0.1", "런너", "ALL_ACTIVE"
            );

            responses.add(response);
        }

        final PageImpl<AdminGetDictionariesHistoriesResponse> pageResponse = new PageImpl<>(
                responses, PageRequest.of(0, 10), 2
        );
        final PaginatedResponse<AdminGetDictionariesHistoriesResponse> paginatedResponse = PaginatedResponse.of(
                pageResponse
        );

        // when
        when(adminQueryService.getDictionariesHistories(anyLong(), anyInt(), anyInt())).thenReturn(paginatedResponse);

        // then
        mockMvc.perform(get("/api/v1/admin/dictionaries/histories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].dictionaryHistoryId").value(10))
                .andExpect(jsonPath("$.content[0].title").value("제목10"))
                .andExpect(jsonPath("$.content[0].ip").value("127.0.0.1"))
                .andExpect(jsonPath("$.content[0].category").value("런너"))
                .andExpect(jsonPath("$.content[0].dictionaryHistoryStatus").value("ALL_ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andDo(print())
                .andDo(
                        document(
                                "admin-getDictionariesHistories",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("사이즈")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 모든 블랙리스트 목록을 페이지 형식으로 조회한다.")
    @Test
    void admin_query_controller_test_04() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        final List<AdminGetBlacklistResponse> responses = new ArrayList<>();

        for (long i = 10; i >= 1; i--) {
            final AdminGetBlacklistResponse response = new AdminGetBlacklistResponse(
                    i, "127.0.0." + i, "사유" + i,
                    LocalDateTime.of(2025, 5, 1, 0, 0, 0)
            );

            responses.add(response);
        }

        final PageImpl<AdminGetBlacklistResponse> pageResponse = new PageImpl<>(
                responses, PageRequest.of(0, 10), 2
        );
        final PaginatedResponse<AdminGetBlacklistResponse> paginatedResponse = PaginatedResponse.of(
                pageResponse
        );

        // when
        when(adminQueryService.getBlacklist(anyLong(), anyInt(), anyInt())).thenReturn(paginatedResponse);

        // then
        mockMvc.perform(get("/api/v1/admin/blacklist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].blacklistId").value(10))
                .andExpect(jsonPath("$.content[0].ip").value("127.0.0.10"))
                .andExpect(jsonPath("$.content[0].reason").value("사유10"))
                .andExpect(jsonPath("$.content[0].createdAt").value("2025-05-01T00:00:00"))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andDo(print())
                .andDo(
                        document(
                                "admin-getBlacklist",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("사이즈")
                                )
                        )
                )
        ;
    }
}