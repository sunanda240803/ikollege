package com.iitm.hosteldine.repository.biometric;

import com.iitm.hosteldine.model.biometric.DeviceFRLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.sql.Timestamp;
import java.util.List;

public interface DeviceFRLogsRepository extends JpaRepository<DeviceFRLogs, Long> {

    @Query("""
		SELECT s FROM DeviceFRLogs s
		WHERE s.logTimestamp BETWEEN :startDate AND :endDate
		  AND (:studentId IS NULL OR s.studentId = :studentId)
		  AND (:messId IS NULL OR s.messId = :messId)
	""")
    List<DeviceFRLogs> findDeviceFRLogs(Timestamp startDate, Timestamp endDate, String studentId, Long messId);
}