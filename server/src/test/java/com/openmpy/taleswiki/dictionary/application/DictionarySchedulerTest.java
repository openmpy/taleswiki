package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.helper.EmbeddedRedisConfig;
import com.openmpy.taleswiki.helper.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

@Transactional
@Import(EmbeddedRedisConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionarySchedulerTest {

    @Autowired
    private DictionaryScheduler dictionaryScheduler;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private ImageS3Service imageS3Service;

    @MockitoBean
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @DisplayName("[통과] 문서 조회수를 DB에 동기화한다.")
    @Test
    void dictionary_scheduler_test_01() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        final String key = String.format("dictionary-view:%d", dictionary.getId());
        redisTemplate.opsForValue().increment(key, 10L);

        // when
        dictionaryScheduler.syncViewCountsToDataBase();

        // then
        final Dictionary foundDictionary = dictionaryRepository.findById(dictionary.getId()).get();
        assertThat(foundDictionary.getView()).isEqualTo(10L);
    }
}