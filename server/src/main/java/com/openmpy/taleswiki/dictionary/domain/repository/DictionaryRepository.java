package com.openmpy.taleswiki.dictionary.domain.repository;

import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryCategory;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    @Query("SELECT d FROM Dictionary d LEFT JOIN FETCH d.histories WHERE d.id = :id")
    @Override
    Optional<Dictionary> findById(@Param("id") final Long id);

    @Query("SELECT d FROM Dictionary d LEFT JOIN FETCH d.currentHistory WHERE d.status IN :statuses ORDER BY d.modifiedAt DESC")
    List<Dictionary> findDictionariesByStatusOrderByModifiedAtDesc(
            @Param("statuses") final List<String> statuses, final Pageable pageable
    );

    List<Dictionary> findAllByCategoryOrderByTitle(final DictionaryCategory category);

    @Query("SELECT d FROM Dictionary d LEFT JOIN FETCH d.currentHistory WHERE d.id >= :id ORDER BY d.id ASC")
    List<Dictionary> findFirstByIdGreaterThanEqualOrderByIdAsc(
            @Param("id") final Long id, final Pageable pageable
    );

    boolean existsByTitle_ValueAndCategory(final String title, final DictionaryCategory category);
}
