package com.iitm.hosteldine.repository.api;

import com.iitm.hosteldine.generated.model.MessQrApplicationEntity;
import com.iitm.hosteldine.model.mess.MessSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessQrApplicationRepository extends JpaRepository<MessQrApplicationEntity, Long> {
    @Query("""
    SELECT m 
    FROM MessQrApplicationEntity m 
    WHERE UPPER(m.studentId) = UPPER(:studentId)
      AND m.activeFlag = :activeFlag
      AND m.createdAt >= :startOfDay AND m.createdAt < :endOfDay
      AND m.messSession = :messSession
""")
    Optional<MessQrApplicationEntity> findTodayQrApplicationsByStudentIdAndSession(String studentId, String messSession, String activeFlag,
                                                                                   LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("""
    SELECT m, mm
    FROM MessQrApplicationEntity m 
    LEFT JOIN MessMasterEntity mm ON (m.messId=mm.id and mm.activeFlag =:activeFlag)
      WHERE m.activeFlag = :activeFlag AND m.qrId = :qrId AND m.qrNumber = :qrNumber """)
    List<Object[]> getMessTokenQrDetails(Long qrId, String qrNumber, String activeFlag);

}