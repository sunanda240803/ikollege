package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.service.office.VacatingStudentDueListRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.iitm.hosteldine.model.hostel.StudentHostelRoomVacatingRequestViewEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentHostelRoomVacatingRequestViewRepository
		extends JpaRepository<StudentHostelRoomVacatingRequestViewEntity, Long> {

	 @Query(value = "SELECT s FROM StudentHostelRoomVacatingRequestViewEntity s"
	 		+ " WHERE UPPER(s.studentId) = UPPER(:studentId) AND s.activeFlag = :activeFlag AND"
	 		+ " s.rejoiningDate IS NULL AND s.approvalDate IS NOT NULL AND"
	 		+ " s.hostelOrWardenApprovalStatus = :status AND s.vacatingReason = :reason")
		Optional<StudentHostelRoomVacatingRequestViewEntity> checkStudentApprovalVacatingForm(String studentId,
				String activeFlag, String status, String reason);

	@Query(value = "SELECT s FROM StudentHostelRoomVacatingRequestViewEntity s"
			+ " WHERE UPPER(s.studentId) = UPPER(:studentId) AND s.activeFlag = :activeFlag AND"
			+ " s.rejoiningDate IS NULL ")
	Optional<StudentHostelRoomVacatingRequestViewEntity> getStudentCharges(String studentId, String activeFlag);

	List<StudentHostelRoomVacatingRequestViewEntity> findByStudentId(String studentID);

    @Query(value = """
        select new com.iitm.hosteldine.service.office.VacatingStudentDueListRecord(a.id, null, a.studentId, b.studentName, b.hostelName, b.roomNumber, a.vacatingDate, a.vacatingReason, null, '', null)
            from StudentHostelRoomVacatingRequestViewEntity a left join
             AllStudentsDetailsViewEntity b on ((b.studentId=a.studentId) and a.activeFlag = :activeFlag)
        where a.activeFlag = :activeFlag and a.duesPermissionRequired= :duePermissionRequired and a.hostelOrWardenApprovalStatus = :hostelOrWardenApprovalStatus
          and b.settlementFlag = :settlementFlag
    """)
    List<VacatingStudentDueListRecord> getVacatingStudentDueList(String activeFlag, String settlementFlag, String duePermissionRequired, String hostelOrWardenApprovalStatus);

    @Query(value = """
        select new com.iitm.hosteldine.service.office.VacatingStudentDueListRecord(a.id, null, a.studentId, b.studentName, b.hostelName, b.roomNumber, a.vacatingDate, a.vacatingReason, null, '', null)
        from StudentHostelRoomVacatingRequestViewEntity a
        left join AllStudentsDetailsViewEntity b on ((b.studentId=a.studentId) and a.activeFlag = :activeFlag)
        where a.activeFlag = :activeFlag and a.duesPermissionRequired = :duePermissionRequired
          and a.hostelOrWardenApprovalStatus = :hostelOrWardenApprovalStatus and b.settlementFlag = :settlementFlag
          and ((a.studentId ILIKE CONCAT('%', :searchKey, '%')) or
              (b.studentName ILIKE CONCAT('%', :searchKey, '%')) or
              (b.hostelName ILIKE CONCAT('%', :searchKey, '%')) or
              (a.vacatingReason ILIKE CONCAT('%', :searchKey, '%')))
    """)
    List<VacatingStudentDueListRecord> getFilteredVacatingStudentDueList(
            String activeFlag, String settlementFlag, String duePermissionRequired, String hostelOrWardenApprovalStatus, String searchKey);


    @Query(value = """
        select student_balance, student_id, acount_name, vacating_date, vacating_reason
        from schooldev.student_balance(:studentId)
        left join schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
            on a.student_id = :studentId
        where a.active_flag = :activeFlag
          and a.dues_permission_required = :duePermissionRequired
    """, nativeQuery = true)
    Optional<Object[]> getVacatingStudentDueDetails(String studentId, String activeFlag, String duePermissionRequired);

}