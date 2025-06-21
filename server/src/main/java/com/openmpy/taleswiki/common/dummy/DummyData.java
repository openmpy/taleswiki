package com.openmpy.taleswiki.common.dummy;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyData {

    private static final Faker FAKER = new Faker(new Locale("ko"));

    @Profile({"dev", "local"})
    @Bean
    private CommandLineRunner init(
            final MemberRepository memberRepository,
            final DictionaryRepository dictionaryRepository,
            final BoardRepository boardRepository
    ) {
        return (args -> saveDictionaries(memberRepository, dictionaryRepository, boardRepository));
    }

    private void saveDictionaries(
            final MemberRepository memberRepository,
            final DictionaryRepository dictionaryRepository,
            final BoardRepository boardRepository
    ) {
        // 회원 관련
        final Member member01 = Member.create("test@test.com", MemberSocial.KAKAO);
        final Member member02 = Member.create("test@test.com", MemberSocial.KAKAO);
        memberRepository.saveAll(List.of(member01, member02));

        // 사전 관련
        for (int i = 1; i <= 1000; i++) {
            Dictionary dictionary;
            String title;
            DictionaryCategory category;
            DictionaryStatus status;
            DictionaryHistory dictionaryHistory;

            if (i % 2 == 0) {
                title = FAKER.name().fullName().replaceAll(" ", "") + i;
                category = DictionaryCategory.PERSON;
                status = DictionaryStatus.ALL_ACTIVE;
            } else if (i % 3 == 0) {
                title = FAKER.animal().name().replaceAll(" ", "") + i;
                category = DictionaryCategory.GUILD;
                status = DictionaryStatus.READ_ONLY;
            } else {
                title = String.valueOf(i);
                category = DictionaryCategory.PERSON;
                status = DictionaryStatus.HIDDEN;
            }

            if (title.length() > 12) {
                title = title.substring(0, 12);
            }

            dictionary = Dictionary.create(title, category);
            dictionary.changeStatus(status.name());
            dictionaryHistory = DictionaryHistory.create(
                    "작성자" + i, "내용" + i, 10L, FAKER.internet().ipV4Address(), dictionary
            );

            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        log.info("사전 더미 데이터 {}개 생성", dictionaryRepository.count());

        // 게시글 관련
        for (int i = 1; i <= 1000; i++) {
            final Board board;

            if (i % 2 == 0) {
                board = Board.save(
                        ("제목" + i).repeat(25),
                        "내용" + i,
                        "테붕이" + member01.getId(),
                        FAKER.internet().ipV4Address(),
                        member01
                );
            } else {
                board = Board.save(
                        ("제목" + i),
                        "내용" + i,
                        "테붕이" + member02.getId(),
                        FAKER.internet().ipV4Address(),
                        member02
                );
            }

            boardRepository.save(board);
        }

        log.info("게시글 더미 데이터 {}개 생성", boardRepository.count());
    }
}
