package com.openmpy.taleswiki.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.taleswiki.dictionary.application.DictionaryCommandService;
import com.openmpy.taleswiki.dictionary.application.DictionaryQueryService;
import com.openmpy.taleswiki.dictionary.application.DictionarySearchService;
import com.openmpy.taleswiki.dictionary.presentation.DictionaryCommandController;
import com.openmpy.taleswiki.dictionary.presentation.DictionaryQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfigureRestDocs
@WebMvcTest(
        controllers = {
                DictionaryCommandController.class,
                DictionaryQueryController.class
        },
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                WebMvcConfigurer.class
        })
)
public abstract class ControllerTestSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected DictionaryCommandService dictionaryCommandService;

    @MockitoBean
    protected DictionaryQueryService dictionaryQueryService;

    @MockitoBean
    protected DictionarySearchService dictionarySearchService;
}
