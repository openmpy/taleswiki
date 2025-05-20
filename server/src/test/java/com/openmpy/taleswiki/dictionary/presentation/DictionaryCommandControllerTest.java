package com.openmpy.taleswiki.dictionary.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySaveResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryUpdateResponse;
import com.openmpy.taleswiki.helper.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DictionaryCommandControllerTest extends ControllerTestSupport {

    @DisplayName("[통과] 사전을 작성한다.")
    @Test
    void dictionary_command_controller_test_01() throws Exception {
        // given
        final DictionarySaveRequest request = new DictionarySaveRequest("제목", "런너", "작성자", "내용");
        final DictionarySaveResponse response = new DictionarySaveResponse(1L);
        final String payload = objectMapper.writeValueAsString(request);

        // when
        when(dictionaryCommandService.save(any(), any())).thenReturn(response);

        // then
        mockMvc.perform(post("/api/v1/dictionaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.dictionaryHistoryId").value(1))
                .andDo(
                        document(
                                "dictionary-save",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("category").description("카테고리"),
                                        fieldWithPath("author").description("작성자"),
                                        fieldWithPath("content").description("내용")
                                ),
                                responseFields(
                                        fieldWithPath("dictionaryHistoryId").description("사전 기록 ID")
                                )
                        )
                )
        ;
    }

    @DisplayName("[통과] 사전을 수정한다.")
    @Test
    void dictionary_command_controller_test_02() throws Exception {
        // given
        final DictionaryUpdateRequest request = new DictionaryUpdateRequest("작성자", "내용");
        final DictionaryUpdateResponse response = new DictionaryUpdateResponse(1L);
        final String payload = objectMapper.writeValueAsString(request);

        // when
        when(dictionaryCommandService.update(anyLong(), any(), any())).thenReturn(response);

        // then
        mockMvc.perform(put("/api/v1/dictionaries/{dictionaryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.dictionaryHistoryId").value(1))
                .andDo(
                        document(
                                "dictionary-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("dictionaryId").description("사전 ID")
                                ),
                                requestFields(
                                        fieldWithPath("author").description("작성자"),
                                        fieldWithPath("content").description("내용")
                                ),
                                responseFields(
                                        fieldWithPath("dictionaryHistoryId").description("업데이트 된 사전 기록 ID")
                                )
                        )
                )
        ;
    }
}