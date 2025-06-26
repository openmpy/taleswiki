package com.openmpy.taleswiki.dictionary.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.helper.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryHistoryTest {

    @DisplayName("[통과] 사전 기록 클래스를 생성한다.")
    @Test
    void dictionary_history_test_01() {
        // given
        final String author = "author";
        final String content = "content";
        final long version = 1L;
        final long size = 1L;
        final String ip = "127.0.0.1";
        final DictionaryStatus status = DictionaryStatus.ALL_ACTIVE;
        final Dictionary dictionary = Fixture.DICTIONARY_01;

        // when
        final DictionaryHistory dictionaryHistory = new DictionaryHistory(
                author, content, version, size, ip, status, dictionary
        );

        // then
        assertThat(dictionaryHistory.getAuthor()).isEqualTo("author");
        assertThat(dictionaryHistory.getContent()).isEqualTo("content");
        assertThat(dictionaryHistory.getVersion()).isEqualTo(1L);
        assertThat(dictionaryHistory.getSize()).isEqualTo(1L);
        assertThat(dictionaryHistory.getIp()).isEqualTo("127.0.0.1");
        assertThat(dictionaryHistory.getStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(dictionaryHistory.getDictionary()).isEqualTo(dictionary);
    }

    @DisplayName("[통과] 사전 기록 클래스를 생성한다.")
    @Test
    void dictionary_history_test_02() {
        // given
        final String author = "author";
        final String content = "content";
        final long size = 1L;
        final String ip = "127.0.0.1";
        final Dictionary dictionary = Fixture.DICTIONARY_01;

        // when
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(author, content, size, ip, dictionary);

        // then
        assertThat(dictionaryHistory.getAuthor()).isEqualTo("author");
        assertThat(dictionaryHistory.getContent()).isEqualTo("content");
        assertThat(dictionaryHistory.getVersion()).isEqualTo(1L);
        assertThat(dictionaryHistory.getSize()).isEqualTo(1L);
        assertThat(dictionaryHistory.getIp()).isEqualTo("127.0.0.1");
        assertThat(dictionaryHistory.getStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(dictionaryHistory.getDictionary()).isEqualTo(dictionary);
    }

    @DisplayName("[통과] 사전 기록을 업데이트한다.")
    @Test
    void dictionary_history_test_03() {
        // given
        final String author = "author";
        final String content = "content";
        final long version = 1L;
        final long size = 1L;
        final String ip = "127.0.0.1";
        final Dictionary dictionary = Fixture.DICTIONARY_01;

        // when
        final DictionaryHistory dictionaryHistory = DictionaryHistory.update(
                author, content, version, size, ip, dictionary
        );

        // then
        assertThat(dictionaryHistory.getAuthor()).isEqualTo("author");
        assertThat(dictionaryHistory.getContent()).isEqualTo("content");
        assertThat(dictionaryHistory.getVersion()).isEqualTo(1L);
        assertThat(dictionaryHistory.getSize()).isEqualTo(1L);
        assertThat(dictionaryHistory.getIp()).isEqualTo("127.0.0.1");
        assertThat(dictionaryHistory.getStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(dictionaryHistory.getDictionary()).isEqualTo(dictionary);
    }

    @DisplayName("[통과] 사전 기록 상태를 변경한다.")
    @Test
    void dictionary_history_test_04() {
        // given
        final DictionaryHistory dictionaryHistory = Fixture.DICTIONARY_HISTORY_01;

        // when
        dictionaryHistory.changeStatus(DictionaryStatus.HIDDEN);

        // then
        assertThat(dictionaryHistory.getStatus()).isEqualTo(DictionaryStatus.HIDDEN);
    }
}