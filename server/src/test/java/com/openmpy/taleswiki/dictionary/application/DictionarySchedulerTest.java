package com.openmpy.taleswiki.dictionary.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DictionarySchedulerTest extends ServiceTestSupport {

    @Autowired
    private DictionaryScheduler dictionaryScheduler;

    @Autowired
    private DictionaryRepository dictionaryRepository;

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

    @DisplayName("[통과] 문서 인기도 수치를 내린다.")
    @Test
    void dictionary_scheduler_test_02() {
        // given
        redisTemplate.opsForZSet().incrementScore("popular_dictionaries", 1L, 1.0);
        redisTemplate.opsForZSet().incrementScore("popular_dictionaries", 2L, 0.1);

        // when
        dictionaryScheduler.decayPopularDictionaryScores();

        // then
        final Double score01 = redisTemplate.opsForZSet().score("popular_dictionaries", 1L);
        final Double score02 = redisTemplate.opsForZSet().score("popular_dictionaries", 2L);

        assertThat(score01).isEqualTo(0.9);
        assertThat(score02).isNull();
    }

    @DisplayName("[통과] 문서 번호 전체를 동기화한다.")
    @Test
    void dictionary_scheduler_test_03() {
        // given
        final Dictionary dictionary = dictionaryRepository.save(Fixture.createDictionary());

        // when
        dictionaryScheduler.cacheAllDictionaryIds();

        // then
        final Set<Object> values = redisTemplate.opsForSet().members("dictionary:ids");

        for (Object value : values) {
            final Long dictionaryId = Long.valueOf(value.toString());
            assertThat(dictionaryId).isEqualTo(dictionary.getId());
        }
    }
}