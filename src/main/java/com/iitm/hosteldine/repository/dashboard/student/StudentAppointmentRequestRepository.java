package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentAppointmentRequestRepository extends JpaRepository<StudentAppointmentRequestEntity, Long> {
    @Query("select sse from StudentAppointmentRequestEntity sse " +
        "where sse.studentId = :studentId and sse.activeFlag = :activeFlag")
    Page<StudentAppointmentRequestEntity> getScholarStayExtension(String studentId, String activeFlag, Pageable pageable);

    @Query("select sse from StudentAppointmentRequestEntity sse " +
            " where sse.studentId = :username and sse.activeFlag = :statusActive" +
            " or sse.thesisStatus ILIKE CONCAT('%', :search, '%') ")
    Page<StudentAppointmentRequestEntity> getScholarStayExtensionWithSearchOption(String username, String statusActive, Pageable pageable, String search);

    @Query(value = """
            SELECT sar, iwsw, iwsw.modifiedAt as stud_wf_modified FROM StudentAppointmentRequestEntity sar 
            LEFT JOIN StudentWorkflowEntity iwsw ON iwsw.requestId = sar.id 
            AND iwsw.studentId = sar.studentId 
            AND (UPPER(iwsw.authorityType) LIKE UPPER(CONCAT('%', :authorityType, '%')) or UPPER(iwsw.authorityType) LIKE UPPER(CONCAT('%', :dean, '%'))) 
            AND iwsw.status IN :includedStatuses
            AND iwsw.activeFlag = :statusActive 
            WHERE sar.studentId = :studentId 
            AND sar.id = :requestId 
            AND sar.activeFlag = :statusActive""")
	Optional<List<Object[]>> getStudentAppointmentRequest(String authorityType, String studentId, Long requestId, String statusActive,
														  List<String> includedStatuses,String dean);

    @Query("SELECT s FROM StudentAppointmentRequestEntity s " +
            "WHERE s.studentId = :studentId " +
            "AND s.stayFrom <= :appointmentTo " +
            "AND s.stayTo >= :appointmentFrom " +
            "AND s.id <> :requestId " +
            "AND s.status NOT IN :excludedStatus")
    List<StudentAppointmentRequestEntity> getScholarStayExtensionOverlappingRequests(String studentId, LocalDate appointmentFrom, LocalDate appointmentTo, Long requestId, List<String> excludedStatus);

    @Modifying
    @Query("UPDATE StudentAppointmentRequestEntity s SET s.status = :cancelStatus, " +
            "s.modifiedBy = :studentId, s.modifiedAt = CURRENT_TIMESTAMP, s.statusNotes = :statusNotes " +
            "WHERE s.studentId = :studentId " +
            "AND s.stayFrom <= :appointmentTo " +
            "AND s.stayTo >= :appointmentFrom " +
            "AND s.id <> :stayRequestId " +
            "AND s.status NOT IN :excludedStatuses")
    void updateStatusToCancelled(String studentId, LocalDate appointmentFrom, LocalDate appointmentTo, Long stayRequestId, String statusNotes, List<String> excludedStatuses, String cancelStatus);

    @Query(value = "SELECT sar, sdi FROM StudentAppointmentRequestEntity sar " +
            "LEFT JOIN StudentDetailsInfoEntity sdi ON sdi.studentId = sar.studentId " +
            "AND sdi.activeFlag = sar.activeFlag " +
            "WHERE sar.id = :requestId " +
            "AND sar.activeFlag = :statusActive")
    Optional<StudentAppointmentRequestEntity> getInformationForMail(Long requestId, String statusActive);


    @Query(value = "SELECT sar FROM StudentAppointmentRequestEntity sar " +
            "WHERE sar.studentId = :studentId " +
            "AND sar.activeFlag = :activeStatus " +
            "ORDER BY sar.createdAt DESC LIMIT 1")
    Optional<StudentAppointmentRequestEntity> getRecentStatus(String studentId, String activeStatus);

    @Query(value = "SELECT sar FROM StudentAppointmentRequestEntity sar " +
            "WHERE sar.studentId = :studentId " +
            "AND sar.activeFlag = :activeStatus " +
            "AND sar.id = :requestId ")
    Optional<StudentAppointmentRequestEntity> getRequestIdStatus(String studentId, Long requestId, String activeStatus);

    @Query(value = "SELECT (CASE WHEN (NOW()::DATE-resend_date::DATE)::INT >= :resendMailCount "
    		+ "THEN resend_date ELSE NULL  END) AS resend_mail_date ,* "
    		+ "FROM schooldev.\"IIT_W_STUDENT_APPOINTMENT_REQUEST\" "
    		+ "WHERE student_id = :studentId AND active_flag = :statusActive AND category <> :category "
            + "ORDER BY created_at DESC", 
            nativeQuery = true)
	Page<Object[]> getStudentHostelAccommodationList(Integer resendMailCount, String studentId, String category, String statusActive, Pageable pageable);

	@Modifying
	@Query("UPDATE StudentAppointmentRequestEntity spr"
	        + " SET spr.status = :status, spr.modifiedBy = :studentId,"
	        + " spr.modifiedAt = :now, spr.statusNotes = :noteKey"
	        + " WHERE spr.studentId = :studentId AND ("
	        + "      (:appointmentFrom BETWEEN spr.stayFrom AND spr.stayTo)"
	        + "   OR (:appointmentTo BETWEEN spr.stayFrom AND spr.stayTo)"
	        + "   OR (spr.stayFrom >= :appointmentFrom AND spr.stayTo <= :appointmentTo)"
	        + " ) AND spr.id <> :requestId AND"
	        + " spr.status NOT IN (:excludedStatuses)")
	void updatePreviousStatusToCancelled(String status, String studentId, LocalDateTime now, String noteKey,
	                                      LocalDate appointmentFrom, LocalDate appointmentTo,
	                                      Long requestId, List<String> excludedStatuses);

	@Query("SELECT sdi.studentAddress, sdi.contactNumber, sdi.studentPersonalEmail,"
			+ " sar.appointmentFrom, sar.appointmentTo, sar.stayFrom, sar.stayTo, sar.category, sar.purpose,"
			+ " CASE WHEN sdi.lastName IS NOT NULL AND sdi.lastName <> ''"
			+ " THEN CONCAT(sdi.firstName, ' ', sdi.lastName) ELSE sdi.firstName END"
			+ " FROM StudentAppointmentRequestEntity sar"
	        + " LEFT JOIN StudentDetailsInfoEntity sdi"
	        + " ON sdi.studentId = sar.studentId AND sdi.activeFlag = :activeFlag"
	        + " WHERE sar.id = :requestId")
	Object getStudentInformationForMail(String activeFlag, Long requestId);

	@Modifying
	@Query("UPDATE StudentAppointmentRequestEntity spr"
	        + " SET spr.resendDate = :now WHERE spr.id = :requestId AND spr.studentId = :studentId")
	void updateResendDate(Long requestId, String studentId, LocalDate now);

	static final String updateCancelQuery = "UPDATE StudentAppointmentRequestEntity spr"
			+ " SET spr.modifiedBy = :user, spr.modifiedAt = :now, spr.status = :status, spr.statusNotes = :note"
			+ " WHERE spr.id = :requestId";

	@Modifying
	@Query(updateCancelQuery)
	void updateCancelRequestForValidatingAccommodation(String user, LocalDateTime now, String status, String note,
			Long requestId);

	@Modifying
	@Query(updateCancelQuery + " AND spr.studentId = :studentId")
	int updateCancelRequestForValidatingAccommodation(String user, LocalDateTime now, String status, String note,
			Long requestId, String studentId);

	@Modifying
	@Query("UPDATE StudentAppointmentRequestEntity spr SET"
			+ " spr.modifiedBy = :studentId, spr.modifiedAt = :now, spr.status = :status,"
			+ " spr.statusNotes = :note, spr.cancelDescription = :description WHERE spr.id = :requestId")
	void updateCancelRequestForApprovedAccommodation(String studentId, LocalDateTime now, String status, String note,
			String description, Long requestId);


	@Query("SELECT sar, sdi FROM StudentAppointmentRequestEntity sar " +
		       "LEFT JOIN AllStudentsDetailsViewEntity sdi ON sdi.studentId = sar.studentId " +
		       "WHERE sar.id = :requestId AND sar.activeFlag = :statusActive")
	Optional<List<Object[]>> getInformationForPdf(Long requestId,String statusActive);

	Optional<StudentAppointmentRequestEntity> findByIdAndActiveFlag(Long id, String activeFlag);
}
