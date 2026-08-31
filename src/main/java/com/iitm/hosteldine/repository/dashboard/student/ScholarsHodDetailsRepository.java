package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.ScholarsHodDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ScholarsHodDetailsRepository extends JpaRepository<ScholarsHodDetailEntity, Long> {
    @Query("select sse from ScholarsHodDetailEntity sse " +
            "where sse.departmentCode = :deptCode " +
            "and sse.activeFlag = :statusActive")
    Optional<ScholarsHodDetailEntity> getDepartmentCode(String deptCode, String statusActive);
}
