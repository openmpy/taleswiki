package com.openmpy.taleswiki.common.dummy;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.document.DictionaryDocument;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionarySearchRepository;
import java.util.ArrayList;
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
            final DictionaryRepository dictionaryRepository,
            final DictionarySearchRepository dictionarySearchRepository
    ) {
        return (args -> saveDictionaries(dictionaryRepository, dictionarySearchRepository));
    }

    private void saveDictionaries(
            final DictionaryRepository dictionaryRepository,
            final DictionarySearchRepository dictionarySearchRepository
    ) {
        final List<DictionaryDocument> dictionaryDocuments = new ArrayList<>();

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
            final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

            final DictionaryDocument dictionaryDocument = DictionaryDocument.of(savedDictionary);
            dictionaryDocuments.add(dictionaryDocument);
        }
        dictionarySearchRepository.saveAll(dictionaryDocuments);

        log.info("사전 더미 데이터 {}개 생성", dictionaryRepository.count());
    }
}
