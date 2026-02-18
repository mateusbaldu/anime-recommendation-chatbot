package com.animerec.chat.repositories;

import com.animerec.chat.models.UserWorkStatus;
import com.animerec.chat.models.UserWorkStatusId;
import com.animerec.chat.enums.WatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWorkStatusRepository extends JpaRepository<UserWorkStatus, UserWorkStatusId> {

    List<UserWorkStatus> findByIdUserId(UUID userId);

    List<UserWorkStatus> findByIdUserIdAndStatus(UUID userId, WatchStatus status);

    Optional<UserWorkStatus> findByIdUserIdAndIdWorkId(UUID userId, UUID workId);

    boolean existsByIdUserIdAndIdWorkId(UUID userId, UUID workId);
}
