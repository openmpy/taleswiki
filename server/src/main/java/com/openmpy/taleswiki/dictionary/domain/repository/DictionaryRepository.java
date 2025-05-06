package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    List<Dictionary> findTop20ByStatusInOrderByUpdatedAtDesc(final List<String> statuses);

    List<Dictionary> findAllByCategoryOrderByTitle(final DictionaryCategory category);

    List<Dictionary> findAllByTitle_ValueContainingAndStatusIsNotOrderByUpdatedAtDesc(
            final String title, final DictionaryStatus status
    );

    boolean existsByTitle_ValueAndCategory(final String title, final DictionaryCategory category);
}
