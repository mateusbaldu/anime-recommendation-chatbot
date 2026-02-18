package com.animerec.chat.repositories;

import com.animerec.chat.models.Work;
import com.animerec.chat.enums.WorkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {

    Optional<Work> findByMalId(Integer malId);

    List<Work> findByTitleContainingIgnoreCase(String title);

    Page<Work> findByMediaType(WorkType mediaType, Pageable pageable);

    boolean existsByMalId(Integer malId);
}
