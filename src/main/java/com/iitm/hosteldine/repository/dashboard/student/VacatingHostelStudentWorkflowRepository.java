package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.VacatingHostelStudentWorkflowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VacatingHostelStudentWorkflowRepository extends JpaRepository<VacatingHostelStudentWorkflowEntity, Long> {
    @Query(value = "SELECT s FROM VacatingHostelStudentWorkflowEntity s  "
            + " WHERE  s.studentId = :studentId and s.status = :status and s.activeFlag = :activeFlag")
    List<VacatingHostelStudentWorkflowEntity> getStudentWorkflowList(String studentId, String status, String activeFlag);
    
    List<VacatingHostelStudentWorkflowEntity> findByRequestIdAndStudentIdAndActiveFlag(Long requestId, String studentId, String activeFlag);

    List<VacatingHostelStudentWorkflowEntity> findByRequestIdAndStudentIdAndAuthorityTypeAndActiveFlag(Long requestId, String studentId, String authorityType, String activeFlag);

    Optional<VacatingHostelStudentWorkflowEntity> findByIdAndActiveFlag(Long id, String activeFlag);

}
