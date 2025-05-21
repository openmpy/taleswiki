package com.openmpy.taleswiki.common.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.helper.TestcontainerSupport;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class RedisServiceTest extends TestcontainerSupport {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.delete(redisTemplate.keys("*"));
    }

    @DisplayName("[통과] redis get 기능 검사")
    @Test
    void redis_service_test_01() {
        // given
        redisTemplate.opsForValue().set("key", "value");

        // when
        final Object value = redisService.get("key");

        // then
        assertThat(value).hasToString("value");
    }

    @DisplayName("[통과] redis keys 기능 검사")
    @Test
    void redis_service_test_02() {
        // given
        redisTemplate.opsForValue().set("key1", "value1");
        redisTemplate.opsForValue().set("key2", "value2");

        // when
        final Set<String> keys = redisService.getKeys("key*");

        // then
        assertThat(keys).containsExactlyInAnyOrder("key1", "key2");
    }

    @DisplayName("[통과] redis increment 기능 검사")
    @Test
    void redis_service_test_03() {
        // given
        redisTemplate.opsForValue().set("key", 10);

        // when
        redisService.increment("key");

        // then
        assertThat(redisTemplate.opsForValue().get("key")).isEqualTo(11);
    }

    @DisplayName("[통과] redis delete 기능 검사")
    @Test
    void redis_service_test_04() {
        // given
        redisTemplate.opsForValue().set("key", 10);

        // when
        redisService.delete("key");

        // then
        assertThat(redisTemplate.hasKey("key")).isFalse();
    }

    @DisplayName("[통과] redis setIfAbsent 기능 검사")
    @Test
    void redis_service_test_05() {
        // given
        redisTemplate.opsForValue().set("key", 10);

        // when
        final boolean firstTry = redisService.setIfAbsent("key", "20", Duration.ofSeconds(60));
        final boolean secondTry = redisService.setIfAbsent("key2", "30", Duration.ofSeconds(60));

        // then
        assertThat(firstTry).isFalse();
        assertThat(secondTry).isTrue();
    }

    @DisplayName("[통과] redis reverseRangeWithScores 기능 검사")
    @Test
    void redis_service_test_06() {
        // given
        redisTemplate.opsForZSet().add("key", "value1", 10);

        // when
        redisService.reverseRangeWithScores("key", 0, -1);

        // then
        assertThat(redisTemplate.opsForZSet().score("key", "value1")).isEqualTo(10.0);
        assertThat(redisTemplate.opsForZSet().range("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().reverseRange("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().size("key")).isEqualTo(1);
        assertThat(redisTemplate.opsForZSet().zCard("key")).isEqualTo(1);
    }

    @DisplayName("[통과] redis incrementScore 기능 검사")
    @Test
    void redis_service_test_07() {
        // given
        redisTemplate.opsForZSet().add("key", "value1", 10);

        // when
        redisService.incrementScore("key", "value1", 10);

        // then
        assertThat(redisTemplate.opsForZSet().score("key", "value1")).isEqualTo(20.0);
        assertThat(redisTemplate.opsForZSet().range("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().reverseRange("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().size("key")).isEqualTo(1);
        assertThat(redisTemplate.opsForZSet().zCard("key")).isEqualTo(1);
    }

    @DisplayName("[통과] redis range 기능 추가")
    @Test
    void redis_service_test_08() {
        // given
        redisTemplate.opsForZSet().add("key", "value1", 10);

        // when
        redisService.range("key", 0, -1);

        // then
        assertThat(redisTemplate.opsForZSet().score("key", "value1")).isEqualTo(10.0);
        assertThat(redisTemplate.opsForZSet().range("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().reverseRange("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().size("key")).isEqualTo(1);
        assertThat(redisTemplate.opsForZSet().zCard("key")).isEqualTo(1);
    }

    @DisplayName("[통과] redis score 기능 검사")
    @Test
    void redis_service_test_09() {
        // given
        redisTemplate.opsForZSet().add("key", "value1", 10);

        // when
        redisService.score("key", "value1");

        // then
        assertThat(redisTemplate.opsForZSet().score("key", "value1")).isEqualTo(10.0);
        assertThat(redisTemplate.opsForZSet().range("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().reverseRange("key", 0, -1)).containsExactly("value1");
        assertThat(redisTemplate.opsForZSet().size("key")).isEqualTo(1);
        assertThat(redisTemplate.opsForZSet().zCard("key")).isEqualTo(1);
    }

    @DisplayName("[통과] redis remove 기능 검사")
    @Test
    void redis_service_test_10() {
        // given
        redisTemplate.opsForZSet().add("key", "value1", 10);

        // when
        redisService.remove("key", "value1");

        // then
        assertThat(redisTemplate.opsForZSet().score("key", "value1")).isNull();
        assertThat(redisTemplate.opsForZSet().range("key", 0, -1)).isEmpty();
        assertThat(redisTemplate.opsForZSet().reverseRange("key", 0, -1)).isEmpty();
        assertThat(redisTemplate.opsForZSet().size("key")).isZero();
        assertThat(redisTemplate.opsForZSet().zCard("key")).isZero();
    }

    @DisplayName("[통과] redis add 기능 검사")
    @Test
    void redis_service_test_11() {
        // given
        redisTemplate.opsForZSet().add("key", "value1", 10);

        // when
        redisService.add("key", "value2", 20);

        // then
        assertThat(redisTemplate.opsForZSet().score("key", "value1")).isEqualTo(10.0);
        assertThat(redisTemplate.opsForZSet().score("key", "value2")).isEqualTo(20.0);
        assertThat(redisTemplate.opsForZSet().range("key", 0, -1)).containsExactly("value1", "value2");
        assertThat(redisTemplate.opsForZSet().reverseRange("key", 0, -1)).containsExactly("value2", "value1");
        assertThat(redisTemplate.opsForZSet().size("key")).isEqualTo(2);
        assertThat(redisTemplate.opsForZSet().zCard("key")).isEqualTo(2);
    }
}