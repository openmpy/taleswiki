package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    boolean existsByTitle_ValueAndCategory(final String title, final DictionaryCategory category);
}
