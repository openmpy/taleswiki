package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DictionaryHistoryRepository extends JpaRepository<DictionaryHistory, Long> {

    @Query("SELECT d FROM DictionaryHistory d LEFT JOIN FETCH d.dictionary WHERE d.id = :dictionaryHistoryId")
    @Override
    Optional<DictionaryHistory> findById(final Long dictionaryHistoryId);

    @EntityGraph(attributePaths = {"dictionary"})
    @Override
    Page<DictionaryHistory> findAll(final Pageable pageable);
}
