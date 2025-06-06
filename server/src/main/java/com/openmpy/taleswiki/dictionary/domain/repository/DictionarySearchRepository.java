package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionarySearchRepository extends JpaRepository<Dictionary, Long> {

    List<Dictionary> findByTitle_ValueContaining(final String keyword);
}
