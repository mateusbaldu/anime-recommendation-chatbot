package com.animerec.chat.models;

import com.animerec.chat.enums.WorkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "works")
@Getter
@Setter
@NoArgsConstructor
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "mal_id", unique = true)
    private Integer malId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private DataSource source;

    @Column(nullable = false)
    private String title;

    @Column(name = "title_english")
    private String titleEnglish;

    @Column(columnDefinition = "text")
    private String synopsis;

    @Column(columnDefinition = "text[]")
    private String[] genres;

    @Column(columnDefinition = "text[]")
    private String[] themes;

    @Convert(converter = WorkTypeConverter.class)
    @Column(name = "media_type")
    private WorkType mediaType;

    @Column(name = "popularity_count")
    private Integer popularityCount;

    @Column(name = "external_score", precision = 4, scale = 2)
    private BigDecimal externalScore;

    @Column(name = "embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    private float[] embedding;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
