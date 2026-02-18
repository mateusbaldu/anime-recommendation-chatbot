package com.animerec.chat.models;

import com.animerec.chat.enums.WatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_work_status")
@Getter
@Setter
@NoArgsConstructor
public class UserWorkStatus {

    @EmbeddedId
    private UserWorkStatusId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workId")
    @JoinColumn(name = "work_id")
    private Work work;

    @Convert(converter = WatchStatusConverter.class)
    @Column(nullable = false)
    private WatchStatus status;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
