package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.ArchiveLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArchiveLogRepository extends JpaRepository<ArchiveLogEntity, Long> {
    Optional<ArchiveLogEntity> findFirstByArchiveIdOrderByIdDesc(LocalDateTime archiveId);
    Optional<ArchiveLogEntity> findFirstByArchiveIdAndSegmentOrderByIdDesc(LocalDateTime archiveId, String segment);
}