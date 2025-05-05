package com.openmpy.taleswiki.common.dummy;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyData {

    @Bean
    private CommandLineRunner init(
            final DictionaryRepository dictionaryRepository
    ) {
        return (args -> saveDictionaries(dictionaryRepository));
    }

    private void saveDictionaries(final DictionaryRepository dictionaryRepository) {
        for (int i = 1; i <= 100; i++) {
            DictionaryCategory category = DictionaryCategory.PERSON;

            if (i % 2 == 0) {
                category = DictionaryCategory.GUILD;
            }

            final Dictionary dictionary = Dictionary.create("제목" + i, category);
            final DictionaryHistory dictionaryHistory = DictionaryHistory.create(
                    "작성자" + i, "내용" + i, 1L, "192.168.0." + i, dictionary
            );

            if (i % 5 == 0) {
                dictionary.changeStatus("hidden");
            }
            dictionary.addHistory(dictionaryHistory);
            dictionaryRepository.save(dictionary);
        }

        log.info("사전 더미 데이터 {}개 생성", dictionaryRepository.count());
    }
}
