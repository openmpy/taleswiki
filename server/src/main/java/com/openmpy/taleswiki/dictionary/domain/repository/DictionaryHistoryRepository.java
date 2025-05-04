package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryHistoryRepository extends JpaRepository<DictionaryHistory, Long> {
}
