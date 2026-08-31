package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.entity.student.StudentHostelPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentHostelPaymentRepository extends JpaRepository<StudentHostelPaymentEntity, Long> {
    Optional<List<StudentHostelPaymentEntity>> findAllByStudentConfirmStatusAndHostelOfficeEnrollmentNotAndStudentIdAndActiveFlagOrderByModifiedAtDesc(
            String studentConfirmStatus, String hostelOfficeEnrollment, String studentId, String activeFlag);

    Optional<StudentHostelPaymentEntity> findByIdAndActiveFlagAndHostelOfficeEnrollmentEqualsIgnoreCase(Long id, String activeFlag, String hostelOfficeEnrollment);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END " +
            "FROM StudentHostelPaymentEntity s " +
            "WHERE s.studentId = :studentId " +
            "AND (s.hostelOfficeEnrollment IS NULL OR s.hostelOfficeEnrollment IN (:validating, :checkedIn)) " +
            "AND (s.overrideApprove IS NULL OR s.overrideApprove = :overrideApprove) " +
            "AND s.activeFlag = 'Y' AND s.paymentReferenceNo = upper(:duNumber) ")
    boolean existsByDuNumberAndConditions(String studentId, String duNumber,String validating,String checkedIn,String overrideApprove);

    Optional<StudentHostelPaymentEntity> findFirstByStudentConfirmStatusAndStudentIdAndActiveFlagOrderByModifiedAtDesc(
            String studentConfirmStatus, String studentId, String activeFlag);

    Optional<List<StudentHostelPaymentEntity>> findByStudentIdEqualsIgnoreCaseAndStudentConfirmStatusAndHostelOfficeEnrollmentAndOverrideApproveIsNull
                                        (String studentId, String studentConfirmStatus, String hostelOfficeEnrollment);


}