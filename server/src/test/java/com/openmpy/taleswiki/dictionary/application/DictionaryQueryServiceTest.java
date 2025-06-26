package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response;
import com.openmpy.taleswiki.helper.EmbeddedRedisConfig;
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
class DictionaryQueryServiceTest {

    @Autowired
    private DictionaryQueryService dictionaryQueryService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @MockitoBean
    private ImageS3Service imageS3Service;

    @MockitoBean
    private S3Client s3Client;

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
}