package com.openmpy.taleswiki.admin.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openmpy.taleswiki.admin.dto.request.AdminBlacklistSaveRequest;
import com.openmpy.taleswiki.helper.ControllerTestSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AdminCommandControllerTest extends ControllerTestSupport {

    @DisplayName("[통과] 사전 상태를 변경한다.")
    @Test
    void admin_command_controller_test_02() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        // when
        doNothing().when(adminCommandService).changeDictionaryStatus(anyLong(), anyLong(), anyString());

        // then
        mockMvc.perform(patch("/api/v1/admin/dictionaries/{dictionaryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .queryParam("status", "hidden")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document(
                                "admin-changeDictionaryStatus",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("dictionaryId").description("사전 ID")
                                ),
                                queryParameters(
                                        parameterWithName("status").description("변경할 사전 상태")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 사전을 삭제한다.")
    @Test
    void admin_command_controller_test_03() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        // when
        doNothing().when(adminCommandService).delete(anyLong(), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/admin/dictionaries/{dictionaryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document(
                                "admin-delete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("dictionaryId").description("사전 ID")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 사전 기록 상태를 변경한다.")
    @Test
    void admin_command_controller_test_04() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        // when
        doNothing().when(adminCommandService).changeDictionaryHistoryStatus(anyLong(), anyLong(), anyString());

        // then
        mockMvc.perform(patch("/api/v1/admin/dictionaries/histories/{dictionaryHistoriesId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .queryParam("status", "hidden")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document(
                                "admin-changeDictionaryHistoryStatus",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("dictionaryHistoriesId").description("사전 기록 ID")
                                ),
                                queryParameters(
                                        parameterWithName("status").description("변경할 사전 상태")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 블랙리스트에 클라이언트 IP를 등록한다.")
    @Test
    void admin_command_controller_test_05() throws Exception {
        // given
        final Cookie cookie = new Cookie("access_token", "success");

        final AdminBlacklistSaveRequest request = new AdminBlacklistSaveRequest("127.0.0.1", "사유");
        final String payload = objectMapper.writeValueAsString(request);

        // when
        doNothing().when(adminCommandService).saveBlacklist(anyLong(), any());

        // then
        mockMvc.perform(post("/api/v1/admin/blacklist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(payload)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document(
                                "admin-saveBlacklist",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("ip").description("클라이언트 IP"),
                                        fieldWithPath("reason").description("사유")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 블랙리스트에 올라간 클라이언트 IP를 삭제한다.")
    @Test
    void admin_command_controller_test_06() throws Exception {
        // given
        final Cookie cookie = new Cookie("admin_token", "success");

        // when
        doNothing().when(adminCommandService).deleteBlacklist(anyLong(), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/admin/blacklist/{blacklistId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document(
                                "admin-deleteBlacklist",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("blacklistId").description("블랙리스트 ID")
                                )
                        )
                )
        ;
    }
}