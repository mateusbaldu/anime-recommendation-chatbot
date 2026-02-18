package com.animerec.chat.repositories;

import com.animerec.chat.models.UserReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, UUID> {

    Page<UserReview> findByUserId(UUID userId, Pageable pageable);

    List<UserReview> findByUserId(UUID userId);

    Optional<UserReview> findByUserIdAndWorkId(UUID userId, UUID workId);

    long countByUserId(UUID userId);

    boolean existsByUserIdAndWorkId(UUID userId, UUID workId);
}
