package com.iitm.hosteldine.repository.studentDashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.StudentExcessMessDetailsEntity;

import java.util.List;
import java.util.Optional;

public interface StudentExcessMessDetailsRepository extends JpaRepository<StudentExcessMessDetailsEntity, Long> {
    Optional<StudentExcessMessDetailsEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<StudentExcessMessDetailsEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StudentExcessMessDetailsEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	Optional<StudentExcessMessDetailsEntity> findByActiveFlagAndMmcIdAndStudentDetailsInfoStudentIdAndMessMasterId(String statusActive, int currentId,
			String studentId, int priorityMessId);
}