package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.dto.api.StudentInfoAPIDto;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewWithSettlementEntity;
import com.iitm.hosteldine.model.student.StudentSearchViewEntity;
import com.iitm.hosteldine.service.hostel.WardenFacultyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AllStudentsDetailsViewRepository extends JpaRepository<AllStudentsDetailsViewEntity, String> {
	
    List<AllStudentsDetailsViewEntity> findAllByStudentStatus(String status);
    
    Optional<AllStudentsDetailsViewEntity> findBystudentId(String studentId);

	@Query(value = "SELECT s FROM AllStudentsDetailsViewWithSettlementEntity s" +
			" WHERE UPPER(s.studentId) = UPPER(:studentId)")
	Optional<AllStudentsDetailsViewWithSettlementEntity> getCompleteStudentDetailsWithSettlement(String studentId);


    String selectAllQuery = "SELECT * FROM schooldev.\"STUDENT_SEARCH_VIEW\"";

    String searchFilterQuery = "CASE WHEN :field = 'studentName' THEN student_name "
			+ "WHEN :field = 'studentId' THEN student_id "
			+ "WHEN :field = 'department' THEN dept_name "
			+ "WHEN :field = 'gender' THEN gender "
			+ "END";

    String searchStringQuery = " CASE WHEN :searchString = '' THEN (lower("
			+ searchFilterQuery + ") IS NOT NULL) ELSE lower(" + searchFilterQuery
			+ ") LIKE lower(CONCAT('%', :searchString, '%')) END";

    @Query(value = selectAllQuery + " WHERE" + searchStringQuery, nativeQuery = true)
	Page<StudentSearchViewEntity> getStudentDetails(String field, String searchString, Pageable pageable);

    @Query(value = selectAllQuery + " WHERE student_status = :active AND active_flag = :activeFlag AND " + searchStringQuery, nativeQuery = true)
    Page<StudentSearchViewEntity> getActiveStudentDetails(String active,String activeFlag, String field, String searchString, Pageable pageable);

    @Query(value = "SELECT s FROM AllStudentsDetailsViewEntity s" +
            " WHERE UPPER(s.studentId) = UPPER(:studentId)")
	AllStudentsDetailsViewEntity getCompleteStudentDetails(String studentId);


    String accommodationRequestQuery = "SELECT * FROM schooldev.search_students_hostel(:validationStatus,"
			+ ":category, CAST(:appointmentFromDate AS VARCHAR), CAST(:appointmentToDate AS VARCHAR),"
			+ "CAST(:stayFromDate AS VARCHAR), CAST(:stayToDate AS VARCHAR), :studentName, :studentId,"
			+ ":validatorName, :validatorEmail, :roleDesignation,"
			+ "CAST(:submittedFromDate AS VARCHAR), CAST(:submittedToDate AS VARCHAR), CAST(:approvalFromDate AS VARCHAR),"
			+ "CAST(:approvalToDate AS VARCHAR), :hostelId, :username,"
			+ ":tabNumber, :currentDayStayFlag)";
    
	@Query(value = accommodationRequestQuery, nativeQuery = true)
	Object[] getAccommodationRequestList(String validationStatus, String category, LocalDate appointmentFromDate,
			LocalDate appointmentToDate, LocalDate stayFromDate, LocalDate stayToDate, String studentName,
			String studentId, String validatorName, String validatorEmail, String roleDesignation,
			LocalDate submittedFromDate, LocalDate submittedToDate, LocalDate approvalFromDate,
			LocalDate approvalToDate, int hostelId, String username, int tabNumber, String currentDayStayFlag);

	@Query(value = accommodationRequestQuery, nativeQuery = true)
	List<Object[]> getAccommodationRequestDownloadList(String validationStatus, String category, LocalDate appointmentFromDate,
			LocalDate appointmentToDate, LocalDate stayFromDate, LocalDate stayToDate, String studentName,
			String studentId, String validatorName, String validatorEmail, String roleDesignation,
			LocalDate submittedFromDate, LocalDate submittedToDate, LocalDate approvalFromDate,
			LocalDate approvalToDate, int hostelId, String username, int tabNumber, String currentDayStayFlag);

	@Query(value = """
			select a from AllStudentsDetailsViewEntity a where a.hostelId = :hostelId
			ORDER BY CASE WHEN a.roomNumber = '' THEN NULL ELSE TO_NUMBER(a.roomNumber,'99999') end, a.studentId
	""")
	Page<AllStudentsDetailsViewEntity> getFixedEstablishmentDebitList(Long hostelId, Pageable pageable);

	@Query(value = """
			select a from AllStudentsDetailsViewEntity a where a.hostelId = :hostelId and
			a.studentId not in ( select  b.studentId from StudentExchangeProgramEntity b where b.activeFlag = :activeFlag and b.toDate >= :currentDate)
			ORDER BY CASE WHEN a.roomNumber = '' THEN NULL ELSE TO_NUMBER(a.roomNumber,'99999') end, a.studentId
	""")
	Page<AllStudentsDetailsViewEntity> getExcludeEstablishmentDebitList(Long hostelId, String activeFlag,
			LocalDate currentDate, Pageable pageable);

	@Query(value = """
				select a.studentId, a.netBal, b.studentName, b.hostelName, b.roomNumber
				from StudentsFinalBalanceEntity a
				left join AllStudentsDetailsViewEntity b on (a.studentId = b.studentId)
				where b.settlementFlag = 'N'
				and (:hostelId is null or b.hostelId = :hostelId)
				and (:studentBalance = 'all' or
					(:studentBalance = 'positive' and a.netBal > 0) or
					(:studentBalance = 'negative' and a.netBal <= 0))
				order by a.studentId
			""")
    Page<Object[]> getStudentDetailsViewReport(@Nullable Long hostelId, @Nullable String studentBalance,
			@Nullable Pageable pageable);

	@Query(value = "select a from AllStudentsDetailsViewEntity a where a.previousId is not null	and (:studentId is null or a.studentId = :studentId)")
	Page<AllStudentsDetailsViewEntity> getAllStudentDetails(String studentId, Pageable pageable);


	@Query(value = "SELECT * FROM schooldev.get_student_details_API(:fromDate, :toDate)", nativeQuery = true)
	List<Object[]> getWorkflowApiStudentDetails(String fromDate, String toDate);

	@Query(value = "select warden_name,warden_email,phone_number,ldap_username,hostel_short_code,hostel_name \n" +
			"from schooldev.\"WARDEN_INFO\" wdi \n" +
			"LEFT JOIN schooldev.\"WARDEN_HOSTEL_MAPPING\" whi on (id=warden_id) \n" +
			"LEFT JOIN schooldev.\"HOSTEL_MASTER\" fm on(whi.hostel_id=fm.hostel_id and whi.active_flag='Y' )\n" +
			"where wdi.active_flag='Y' and (date(wdi.created_at)=now()::date or date(wdi.modified_at)=now()::date or \n" +
			"date(whi.created_at)=now()::date or date(whi.modified_at)=now()::date) ", nativeQuery = true)
	List<Object[]> getWorkflowApiWardenDetails();

	@Query(value = """
	select new com.iitm.hosteldine.service.hostel.WardenFacultyRecord(wi.wardenEmail,sde.emailAddress,asdv.hostelName,asdv.studentId,
		wi.ldapUsername,um.id.username)
		from AllStudentsDetailsViewEntity asdv
		join HostelUserMappingEntity hum on(hum.id.hostel.id = asdv.hostelId and hum.activeFlag = :activeFlag)
			join UserManagementEntity um on (hum.id.user = um.id.username and um.activeFlag = :activeFlag)
				join WardenHostelMappingEntity whm on (whm.id.hostelId = asdv.hostelId and whm.activeFlag = :activeFlag)
					join WardenInfoEntity wi on (whm.id.wardenId = wi.id and wi.activeFlag = :activeFlag)
						left join StaffDetailsEntity sde on (um.id.userId = sde.facultyId and um.activeFlag = :activeFlag)
							where asdv.studentId in (:studentId)
	""")
	List<WardenFacultyRecord> getWardenAndFacultyDetails(List<String> studentId, String activeFlag);

    //Note: Don't change the order of the columns..If anything changed we have to change in StudentInfoAPIDto Constructor too..
    @Query(value = """
            SELECT REPLACE(CASE WHEN student_balance < 0 THEN student_balance || ' (Cr)' ELSE student_balance || ' (Dr)' END, '-', '') AS final_balance,
              sub.bio_data_id,sub.student_id,sub.student_name, sub.student_mobile::character varying(16), sub.gender::character varying(4),sub.category,
              family_info.family_mobile_no,sub.hostel_name,sub.room_number, sub.seat::character varying(4)
            FROM (SELECT  asdv.*, schooldev.student_balance(student_id) AS student_balance
                    FROM schooldev."ALL_STUDENTS_DETAILS_VIEW" asdv) AS sub
            LEFT JOIN (SELECT STRING_AGG(relation_type || '~' || mobile_no, ',') AS family_mobile_no, bio_data_id,application_number
                  FROM schooldev."STUDENT_BIO_DATA_FAMILY_INFO"
                  WHERE active_flag = 'Y'  AND relation_type IN ('Father', 'Mother')
                  GROUP BY bio_data_id, application_number) AS family_info ON (family_info.bio_data_id = sub.bio_data_id)
            WHERE (
            (UPPER(sub.student_id) = UPPER(COALESCE(:studentId, ''))) 
            OR 
            (sub.hostel_id = COALESCE(:hostelId, sub.hostel_id) 
            AND sub.room_id = COALESCE(:roomId, sub.room_id))
            )""",nativeQuery = true)
    List<StudentInfoAPIDto> getStudentDetailsForApp(
            @Param("studentId") String studentId,
            @Param("hostelId") long hostelId,
            @Param("roomId") long roomId
    );


}