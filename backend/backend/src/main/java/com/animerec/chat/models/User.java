package com.animerec.chat.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "google_sub", unique = true)
    private String googleSub;

    @Column(unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "persona_summary", columnDefinition = "text")
    private String personaSummary;

    @Column(name = "is_guest", nullable = false)
    private boolean guest = false;

    @Column(name = "guest_session_id", unique = true)
    private String guestSessionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "consented_at")
    private LocalDateTime consentedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
