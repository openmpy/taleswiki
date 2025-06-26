package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import com.openmpy.taleswiki.helper.EmbeddedRedisConfig;
import com.openmpy.taleswiki.helper.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

@Transactional
@Import(EmbeddedRedisConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionarySearchServiceTest {

    @Autowired
    private DictionarySearchService dictionarySearchService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @MockitoBean
    private ImageS3Service imageS3Service;

    @MockitoBean
    private S3Client s3Client;

    @DisplayName("[통과] 문서를 제목으로 검색한다.")
    @Test
    void dictionary_search_service_test_01() {
        // given
        dictionaryRepository.save(Fixture.createDictionary());

        // when
        final DictionarySearchDictionariesResponse response = dictionarySearchService.searchByTitle("제목");

        // then
        assertThat(response.dictionaries()).hasSize(1);
    }
}