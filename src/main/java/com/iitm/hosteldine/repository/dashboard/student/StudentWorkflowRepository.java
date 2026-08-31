package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentWorkflowRepository extends JpaRepository<StudentWorkflowEntity, Long> {

    @Query(value = """
				SELECT s FROM StudentWorkflowEntity s 
				WHERE s.studentId = :studentId 
				AND s.requestId = :requestId 
				AND s.status <> :statusDefault 
				AND s.activeFlag = :statusActive
				ORDER BY s.approvalLevel
            """)
    List<StudentWorkflowEntity> getStudentWorkflowList(String studentId, Long requestId, String statusActive, String statusDefault);

    @Query(value = "SELECT s FROM StudentWorkflowEntity s "
            + "WHERE s.studentId = :studentId "
            + "AND s.requestId = :requestId "
            + "AND s.status = :status "
            + "AND s.category = :category "
            + "AND s.approvalLevel = :approvalLevel "
            + "AND s.activeFlag = :statusActive")
    List<StudentWorkflowEntity> getStudentWorkflowByApprovalStatus(String studentId, Long requestId, String status, String category, Integer approvalLevel, String statusActive);

    @Query(value = "SELECT s FROM StudentWorkflowEntity s "
            + "WHERE s.studentId = :studentId "
            + "AND s.requestId = :requestId "
            + "AND s.activeFlag = :statusActive ")
    List<StudentWorkflowEntity> getStudentWorkflowStatus(String studentId, Long requestId, String statusActive);

    @Modifying
    @Query("UPDATE StudentWorkflowEntity w SET w.status = :cancelStatus, w.modifiedBy = :studentId, " +
            "w.modifiedAt = CURRENT_TIMESTAMP " +
            "WHERE w.requestId IN (SELECT s.id FROM StudentAppointmentRequestEntity s " +
            "WHERE s.studentId = :studentId " +
            "AND s.stayFrom <= :appointmentTo " +
            "AND s.stayTo >= :appointmentFrom " +
            "AND s.id <> :stayRequestId " +
            "AND s.status NOT IN :excludedStatuses)")
    void updateWorkflowToCancelled(String studentId, LocalDate appointmentFrom, LocalDate appointmentTo, Long stayRequestId, List<String> excludedStatuses, String cancelStatus);

	@Modifying
	@Query("UPDATE StudentWorkflowEntity w SET w.modifiedBy = :studentId, w.modifiedAt = :now, w.status = :status"
			+ " WHERE w.requestId IN (SELECT s.id FROM StudentAppointmentRequestEntity s "
			+ " WHERE s.studentId = :studentId AND ("
			+ " :appointmentFrom BETWEEN s.stayFrom AND s.stayTo "
			+ " OR :appointmentTo BETWEEN s.stayFrom AND s.stayTo "
			+ " OR (s.stayFrom >= :appointmentFrom AND s.stayTo <= :appointmentTo))"
			+ " AND s.id <> :requestId AND s.status IN :status)")
	void updatePreviousWorkflowToCancelled(String status, String studentId, LocalDateTime now, LocalDate appointmentFrom,
			LocalDate appointmentTo, Long requestId);

    @Query("SELECT swe FROM StudentWorkflowEntity swe"
            + " WHERE swe.category = :category"
            + " AND swe.status = :status"
            + " AND swe.requestId = :requestId"
            + " AND swe.approvalLevel = :approvalLevel")
    List<StudentWorkflowEntity> getStudentWorkflowList(String category, Long requestId, String status, Integer approvalLevel);

    @Query("SELECT swe FROM StudentWorkflowEntity swe"
            + " WHERE swe.id = (SELECT MAX(subSwe.id) FROM StudentWorkflowEntity subSwe"
            + " WHERE subSwe.status = :status AND subSwe.studentId = :studentId AND subSwe.requestId = :requestId)")
    List<StudentWorkflowEntity> getStudentWorkflowList(String status, String studentId, Long requestId);
    
    static final String updateCancelQuery = "UPDATE StudentWorkflowEntity swe SET"
    		+ " swe.modifiedBy = :user, swe.modifiedAt = :now, swe.status = :status"
    		+ " WHERE swe.requestId = :requestId and swe.activeFlag = :activeFlag";
    
	@Modifying
	@Query(updateCancelQuery)
	void updateCancelRequestForAccommodation(String user, LocalDateTime now, String status,
			String activeFlag, Long requestId);

	@Modifying
	@Query(updateCancelQuery + " AND swe.studentId = :studentId " +
			"AND (LOWER(swe.authorityType) LIKE LOWER(CONCAT('%', :ccw, '%')) or LOWER(swe.authorityType) LIKE LOWER(CONCAT('%', :dean, '%')))")
	int updateCancelRequestForAccommodation(String user, LocalDateTime now, String status,
			String activeFlag, Long requestId, String studentId, String ccw,String dean);

//	StudentWorkflowEntity findByStatusAndStudentIdAndRequestId(String status, String studentId, Long requestId);
//	@Query(updateCancelQuery + " AND swe.authorityType LIKE '%:role%'")
//	void updateCancelRequestForAccommodation(String user, LocalDateTime now, String status,
//			String activeFlag, Long requestId, String role);

	@Query("SELECT w FROM StudentWorkflowEntity w WHERE w.requestId = :requestId AND w.studentId = :studentId " +
			"AND (LOWER(w.authorityType) LIKE LOWER(CONCAT('%', :ccw, '%')) or LOWER(w.authorityType) LIKE LOWER(CONCAT('%', :dean, '%'))) " +
			"AND w.activeFlag = :statusActive")
	Optional<StudentWorkflowEntity> getStudentWorkflowDetails(Long requestId, String studentId, String ccw, String dean, String statusActive);

	//Optional<StudentWorkflowEntity> findByRequestIdAndActiveFlagAndAuthorityTypeContaining(Long requestId, String activeFlag, String authorityType);

	@Query(""" 
		SELECT s FROM StudentWorkflowEntity s
   		WHERE s.requestId = :requestId AND s.activeFlag = :activeFlag
      	AND (LOWER(s.authorityType) LIKE LOWER(CONCAT('%', :ccw, '%'))  OR LOWER(s.authorityType) LIKE LOWER(CONCAT('%', :dean, '%')))
		""")
	Optional<StudentWorkflowEntity> findByAuthorityTypeCCWOrDean(
			@Param("requestId") Long requestId,
			@Param("activeFlag") String activeFlag,
			@Param("ccw") String ccw,
			@Param("dean") String dean
	);


	@Query("""
				select b from StudentAppointmentRequestEntity a join  StudentWorkflowEntity b on (a.id=b.requestId and b.activeFlag= :statusActive)
				where b.id = :workflowId 
				and (LOWER(b.authorityType) not like LOWER(CONCAT('%', :ccw, '%')) and LOWER(b.authorityType) not like LOWER(CONCAT('%', :dean, '%'))) 
				and a.status = :statusReject
			""")
	Optional<StudentWorkflowEntity> checkRejectedDetails(Long workflowId, String statusReject, String statusActive,String ccw, String dean);

	@Query("""
			SELECT a FROM StudentAppointmentRequestEntity a 
			WHERE a.id = (
				SELECT b.requestId 
				FROM StudentWorkflowEntity b
				WHERE b.id = :workflowId
			) 
			AND a.status NOT IN :excludedStatus 
			AND a.activeFlag = :statusActive
	""")
	Optional<StudentAppointmentRequestEntity> getAppointmentDetailsByWorkflowRequestId(Long workflowId, LocalDateTime modifiedAt, List<String> excludedStatus, String statusActive);

	Optional<StudentWorkflowEntity> findByIdAndActiveFlagAndStatusIn(Long workflowId, String activeFlag, List<String> includedStatus);

	Optional<StudentWorkflowEntity> findByIdAndActiveFlag(Long workflowId, String statusActive);

	@Query(value = "SELECT * FROM schooldev.process_workflow_student(:workflowId, :modifiedAt, :status, :rejectReason, :approvalNotes, :modifiedBy)",
			nativeQuery = true)
	Optional<List<Object[]>> updateApprovalStatus(Long workflowId, LocalDateTime modifiedAt, String status, String rejectReason, String approvalNotes, String modifiedBy);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
			UPDATE StudentAppointmentRequestEntity s 
			SET s.modifiedAt = :modifiedAt, 
				s.approvalDate = :approvedDate, 
				s.stayFrom = COALESCE(:stayFrom, s.stayFrom),
				s.stayTo = COALESCE(:stayTo, s.stayTo),
				s.modifiedBy = :modifiedBy
				WHERE s.id = :requestId AND s.studentId = :studentId 
				AND s.activeFlag = :statusActive AND s.status = :status
	""")
	void updateStayRequestDates(Long requestId, String studentId, LocalDate stayFrom, LocalDate stayTo, String status, String statusActive, LocalDateTime modifiedAt, String modifiedBy, LocalDate approvedDate);

	@Modifying
	@Query("""
			UPDATE StudentAppointmentRequestEntity s 
			SET s.approvalDate = :approvedDate, s.modifiedBy = :modifiedBy, s.modifiedAt = :modifiedAt
				WHERE s.id = :requestId AND s.studentId = :studentId 
				AND s.activeFlag = :statusActive
	""")
	void updateApprovalDate(Long requestId, String studentId, String statusActive, LocalDate approvedDate, LocalDateTime modifiedAt, String modifiedBy);

	@Modifying
     @Query("""
              	UPDATE StudentAppointmentRequestEntity s 
                SET s.modifiedAt = :modifiedAt, 
	            s.occupancy = :occupancyStatus, s.modifiedBy = :modifiedBy
                WHERE s.id = :requestId AND s.studentId = :studentId 
                AND s.activeFlag = :statusActive
      """)
      void updateOccupancyStatus(Long requestId, String studentId, String occupancyStatus, String statusActive, LocalDateTime modifiedAt, String modifiedBy);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
			UPDATE StudentAppointmentRequestEntity s 
			SET s.modifiedAt = :modifiedAt, 
			 s.approvalDate = :approvedDate, s.modifiedBy = :modifiedBy
			 WHERE s.id = :requestId AND s.studentId = :studentId 
			 AND s.activeFlag = :statusActive AND s.status = :status
	""")
	void updateStayRequestDatesWhenDatesAreNull(Long requestId, String studentId, String status, String statusActive, LocalDateTime modifiedAt, String modifiedBy, LocalDate approvedDate);
}
