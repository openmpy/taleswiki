package com.openmpy.taleswiki.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.taleswiki.admin.application.AdminCommandService;
import com.openmpy.taleswiki.admin.application.AdminQueryService;
import com.openmpy.taleswiki.admin.presentation.AdminCommandController;
import com.openmpy.taleswiki.admin.presentation.AdminQueryController;
import com.openmpy.taleswiki.common.properties.CookieProperties;
import com.openmpy.taleswiki.dictionary.application.DictionaryCommandService;
import com.openmpy.taleswiki.dictionary.application.DictionaryQueryService;
import com.openmpy.taleswiki.dictionary.application.DictionarySearchService;
import com.openmpy.taleswiki.dictionary.presentation.DictionaryCommandController;
import com.openmpy.taleswiki.dictionary.presentation.DictionaryQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfigureRestDocs
@EnableConfigurationProperties(CookieProperties.class)
@WebMvcTest(
        controllers = {
                DictionaryCommandController.class,
                DictionaryQueryController.class,
                AdminCommandController.class,
                AdminQueryController.class
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebMvcConfigurer.class,
                })
        }
)
@Import(TestWebMvcConfig.class)
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

    @MockitoBean
    protected AdminCommandService adminCommandService;

    @MockitoBean
    protected AdminQueryService adminQueryService;
}
