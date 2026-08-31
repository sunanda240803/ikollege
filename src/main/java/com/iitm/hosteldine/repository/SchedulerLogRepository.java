package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.SchedulerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SchedulerLogRepository extends JpaRepository<SchedulerLog, Long> {

    @Query("""
from SchedulerLog log where cast(log.createdAt as date) = cast(:createdAt as date)
""")
    List<SchedulerLog> getAllByCreatedDate(LocalDateTime createdAt);
}
