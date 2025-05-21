package com.openmpy.taleswiki.dictionary.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionarySchedulerTest extends ServiceTestSupport {

    @Autowired
    private DictionaryScheduler dictionaryScheduler;

    @MockitoBean
    private DictionaryCommandService dictionaryCommandService;

    @MockitoBean
    private RedisService redisService;

    @DisplayName("[통과] 레디스에 저장된 사전 조회수를 DB에 동기화한다.")
    @Test
    void dictionary_scheduler_test_01() {
        // given
        final String key = "dictionary-view:1:127.0.0.1";

        // stub
        when(redisService.getKeys("dictionary-view:*")).thenReturn(Set.of(key));
        when(redisService.get("dictionary-view:1")).thenReturn("1");

        // when
        dictionaryScheduler.syncViewCountsToDataBase();

        // then
        verify(redisService, atLeastOnce()).getKeys("dictionary-view:*");
        verify(redisService).get("dictionary-view:1");
        verify(dictionaryCommandService).incrementViews(1L, 1L);
        verify(redisService).delete(key);
    }

    @DisplayName("[통과] 레디스에 저장된 사전 조회수가 null일 경우 건너뛴다.")
    @Test
    void dictionary_scheduler_test_02() {
        // given
        final String key = "dictionary-view:1:127.0.0.1";

        // stub
        when(redisService.getKeys("dictionary-view:*")).thenReturn(Set.of(key));
        when(redisService.get("dictionary-view:2")).thenReturn(null);

        // when
        dictionaryScheduler.syncViewCountsToDataBase();

        // then
        verify(redisService, atLeastOnce()).getKeys("dictionary-view:*");
        verify(redisService, never()).get("dictionary-view:2");
        verify(dictionaryCommandService, never()).incrementViews(2L, 1L);
        verify(redisService, never()).delete("dictionary-view:2:127.0.0.1");
    }

    @DisplayName("[통과] 사전 인기도 점수가 갱신 또는 제거 된다.")
    @Test
    void dictionary_scheduler_test_03() {
        // given
        final String dictionary1 = "1";
        final String dictionary2 = "2";
        final LinkedHashSet<Object> dictionaries = new LinkedHashSet<>(Set.of(dictionary1, dictionary2));

        // stub
        when(redisService.range("popular_dictionaries", 0, -1)).thenReturn(dictionaries);
        when(redisService.score("popular_dictionaries", dictionary1)).thenReturn(1.0);
        when(redisService.score("popular_dictionaries", dictionary2)).thenReturn(0.05);

        // when
        dictionaryScheduler.decayPopularDictionaryScores();

        // then
        verify(redisService).score("popular_dictionaries", dictionary1);
        verify(redisService).add("popular_dictionaries", dictionary1, 0.9);

        verify(redisService).score("popular_dictionaries", dictionary2);
        verify(redisService).remove("popular_dictionaries", dictionary2);

        verify(redisService, never()).add(eq("popular_dictionaries"), eq(dictionary2), anyDouble());
    }

    @DisplayName("[통과] 사전 인기도 점수가 null일 경우 건너뛴다.")
    @Test
    void dictionary_scheduler_test_04() {
        // given
        final String dictionary1 = "1";
        final LinkedHashSet<Object> dictionaries = new LinkedHashSet<>(Set.of(dictionary1));

        // stub
        when(redisService.range("popular_dictionaries", 0, -1)).thenReturn(dictionaries);
        when(redisService.score("popular_dictionaries", dictionary1)).thenReturn(null);

        // when
        dictionaryScheduler.decayPopularDictionaryScores();

        // then
        verify(redisService).score("popular_dictionaries", dictionary1);
        verify(redisService, never()).remove(anyString(), any());
        verify(redisService, never()).add(anyString(), any(), anyDouble());
    }
}