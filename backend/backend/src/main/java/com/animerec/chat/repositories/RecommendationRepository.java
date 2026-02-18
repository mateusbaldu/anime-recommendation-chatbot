package com.animerec.chat.repositories;

import com.animerec.chat.models.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    Page<Recommendation> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Recommendation> findByChatSessionId(UUID chatSessionId);

    boolean existsByUserIdAndWorkId(UUID userId, UUID workId);
}
