package com.openmpy.taleswiki.dictionary.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.helper.Fixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DictionaryTest {

    @DisplayName("[통과] 사전 클래스를 생성한다.")
    @Test
    void dictionary_test_01() {
        // given
        final String title = "title";
        final DictionaryCategory person = DictionaryCategory.PERSON;
        final DictionaryStatus allActive = DictionaryStatus.ALL_ACTIVE;
        final long view = 0L;

        // when
        final Dictionary dictionary = new Dictionary(title, person, allActive, view, LocalDateTime.now());

        // then
        assertThat(dictionary.getTitle()).isEqualTo("title");
        assertThat(dictionary.getCategory()).isEqualTo(DictionaryCategory.PERSON);
        assertThat(dictionary.getStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(dictionary.getView()).isZero();
    }

    @DisplayName("[통과] 사전 클래스를 생성한다.")
    @Test
    void dictionary_test_02() {
        // given
        final String title = "title";
        final DictionaryCategory guild = DictionaryCategory.GUILD;

        // when
        final Dictionary dictionary = Dictionary.create(title, guild);

        // then
        assertThat(dictionary.getTitle()).isEqualTo("title");
        assertThat(dictionary.getCategory()).isEqualTo(DictionaryCategory.GUILD);
        assertThat(dictionary.getStatus()).isEqualTo(DictionaryStatus.ALL_ACTIVE);
        assertThat(dictionary.getView()).isZero();
    }

    @DisplayName("[통과] 사전 상태를 수정한다.")
    @Test
    void dictionary_test_03() {
        // given
        final Dictionary dictionary = Fixture.DICTIONARY_01;

        // when
        dictionary.changeStatus("read_only");

        // then
        assertThat(dictionary.getStatus()).isEqualTo(DictionaryStatus.READ_ONLY);
    }

    @DisplayName("[통과] 사전 조회수를 증가시킨다.")
    @Test
    void dictionary_test_04() {
        // given
        final Dictionary dictionary = Fixture.DICTIONARY_01;

        // when
        dictionary.incrementViews(1L);

        // then
        assertThat(dictionary.getView()).isEqualTo(1L);
    }

    @DisplayName("[통과] 사전에 사전 기록을 추가한다.")
    @Test
    void dictionary_test_05() {
        // given
        final String author = "author";
        final String content = "content";
        final long size = 1L;
        final String ip = "127.0.0.1";

        final Dictionary dictionary = Fixture.DICTIONARY_01;
        final DictionaryHistory dictionaryHistory = DictionaryHistory.create(author, content, size, ip, dictionary);

        // when
        dictionary.addHistory(dictionaryHistory);

        // then
        assertThat(dictionary.getHistories()).hasSize(1);
        assertThat(dictionary.getHistories().getLast()).isEqualTo(dictionaryHistory);
        assertThat(dictionary.getCurrentHistory()).isEqualTo(dictionaryHistory);
    }
}