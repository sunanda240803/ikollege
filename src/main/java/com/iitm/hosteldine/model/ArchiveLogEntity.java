package com.iitm.hosteldine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "archive_log", schema = "archive")
public class ArchiveLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "archive_id", nullable = false)
    private LocalDateTime archiveId;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Column(name = "segment", nullable = false, length = Integer.MAX_VALUE)
    private String segment;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "remaining_records")
    private Integer remainingRecords;

    @Column(name = "time_taken")
    private Long timeTaken;

    @Column(name = "time_remaining")
    private Long timeRemaining;

    @Column(name = "estimated_time_of_completion")
    private LocalDateTime estimatedTimeOfCompletion;

    @Column(name = "additional_log", length = Integer.MAX_VALUE)
    private String additionalLog;

    public ArchiveLogEntity(LocalDateTime archiveId, String segment, String additionalLog) {
        this.archiveId = archiveId;
        this.segment = segment;
        this.additionalLog = additionalLog;
    }

    public ArchiveLogEntity(LocalDateTime archiveId, String segment, Integer totalRecords, Integer remainingRecords, String additionalLog) {
        this(archiveId, segment, additionalLog);
        this.totalRecords = totalRecords;
        this.remainingRecords = remainingRecords;
    }

    public ArchiveLogEntity(LocalDateTime archiveId, String segment, Integer totalRecords, Integer remainingRecords, Long timeTaken, Long timeRemaining, LocalDateTime estimatedTimeOfCompletion, String additionalLog) {
        this(archiveId, segment, totalRecords, remainingRecords, additionalLog);
        this.timeTaken = timeTaken;
        this.timeRemaining = timeRemaining;
        this.estimatedTimeOfCompletion = estimatedTimeOfCompletion;
    }
}