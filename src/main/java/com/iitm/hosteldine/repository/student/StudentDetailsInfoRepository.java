package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.StudentRoomInfoDTO;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface StudentDetailsInfoRepository extends JpaRepository<StudentDetailsInfoEntity, String> {
    Optional<StudentDetailsInfoEntity> findByStudentIdAndActiveFlag(String studentId, String statusActive);

    boolean existsByActiveFlagAndStudentIdAndSettlementFlag(String activeFlag, String studentId, String settlementFlag);

	boolean existsByActiveFlagAndStudentIdIgnoreCaseAndSettlementFlag(String activeFlag, String studentId, String settlementFlag);

    boolean existsByActiveFlagAndStudentIdAndSettlementFlagAndDayScholar(String activeFlag, String studentId,
                                                                         String settlementFlag, String dayScholar);


    @Query(value = "SELECT a.student_id " + "FROM schooldev.\"STUDENT_DETAILS_INFO\" as a "
            + "JOIN (SELECT regexp_split_to_table(:prevId, ',') as studid) as studentid "
            + "ON (a.previous_id LIKE '%' || studentid.studid || '%') ", nativeQuery = true)
    List<String> findByPreviousId(@Param("prevId") String prevId);


    @Query(value = "SELECT a.student_id, a.previous_id, studentid.studid as studid " +
            "FROM schooldev.\"STUDENT_DETAILS_INFO\" a " +
            "JOIN (SELECT regexp_split_to_table(:studentIds, ',') as studid) as studentid " +
            "ON (a.previous_id LIKE '%' || studentid.studid || '%' OR " +
            "(a.student_id = studentid.studid AND a.active_flag = 'Y')) ",
            nativeQuery = true)
    List<Object[]> checkDuplicateNoWithSeparate(
            @Param("studentIds") String studentIds);

    @Query(value = "SELECT s FROM StudentDetailsInfoEntity s  "
            + " WHERE  upper(s.previousId) LIKE upper('%'|| :prevId|| '%')")
    List<StudentDetailsInfoEntity> checkStudentIdExistInPreviousId(@Param("prevId") String prevId);


    @Transactional(propagation = Propagation.REQUIRED)
    @Modifying
    @Query(value = "UPDATE StudentDetailsInfoEntity s "
            + "SET s.previousId = ( "
            + "  CASE "
            + "    WHEN s.previousId IS NULL THEN :prevStudId "
            + "    WHEN s.previousId IS NOT NULL AND s.previousId NOT LIKE '%'|| :prevStudId||'%' THEN CONCAT(s.previousId, ',', :prevStudId) "
            + "    ELSE s.previousId "
            + "  END "
            + ") "
            + "WHERE s.studentId = :studentId")
    int updatePreviousStudId(@Param("prevStudId") String prevStudId, @Param("studentId") String studentId);

    Optional<StudentDetailsInfoEntity> findByStudentId(String previousId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("UPDATE StudentDetailsInfoEntity s SET s.activeFlag = 'N',s.modifiedBy=:user,s.modifiedAt=:now WHERE s.studentId IN :studentIds")
    int deactivateStudentsByIds(@Param("studentIds") List<String> studentIds,String user,LocalDateTime now);


    @Query(value = "SELECT a.student_id, a.previous_id, studentid.studid AS studid " +
            "FROM schooldev.\"STUDENT_DETAILS_INFO\" AS a " +
            "JOIN (SELECT regexp_split_to_table(:previousStudId, ',') AS studid) AS studentid " +
            "ON a.previous_id NOT LIKE '%' || studentid.studid || '%' " +
            "WHERE a.student_id = :studentId  " +
            "AND a.active_flag = 'Y';", nativeQuery = true)
    List<Object[]> findMatchingStudents(
            @Param("previousStudId") String previousStudId,
            @Param("studentId") String studentId);

	@Query(value = """
    SELECT trim(id) AS prev_id
    FROM (
        SELECT regexp_split_to_table(:previousStudId, ',') AS id, 1 AS col
        UNION ALL
        SELECT regexp_split_to_table(a.previous_id, ',') AS id, 2 AS col
        FROM schooldev."STUDENT_DETAILS_INFO" a
        JOIN (SELECT regexp_split_to_table(:previousStudId, ',') AS studid  ) studentid
          ON a.student_id = studentid.studid
        WHERE a.active_flag = 'Y' AND a.previous_id IS NOT NULL AND a.previous_id <> ''
    ) t
		GROUP BY trim(id)
		ORDER BY MIN(col)
    """,nativeQuery = true)
	List<String> getPrevIdsOfPrevIds(
			@Param("previousStudId") String previousStudId);


	boolean existsByStudentIdAndActiveFlag(String studentId, String statusActive);

    @Query("""
    	    SELECT CASE WHEN SDI.lastName IS NOT NULL THEN SDI.firstName || ' ' || SDI.lastName ELSE SDI.firstName 
    	           END AS studentFullName, HRI.roomNo, HM.hostelName ,HRI.id,HM.id
    	    FROM StudentDetailsInfoEntity SDI 
    	    LEFT JOIN HostelRoomAllotmentInfoEntity HRAI 
    	        ON SDI.studentId = HRAI.studentId 
    	        AND SDI.activeFlag = HRAI.activeFlag
    	    LEFT JOIN HostelRoomInfoEntity HRI 
    	        ON HRAI.roomId = HRI.id 
    	        AND SDI.activeFlag = HRI.activeFlag
    	    LEFT JOIN HostelFloorMasterEntity HFM 
    	        ON HRI.building.id = HFM.id
    	        AND HRAI.buildingId = HFM.id 
    	        AND HFM.activeFlag = SDI.activeFlag
    	    LEFT JOIN HostelMasterEntity HM 
    	        ON HFM.hostel.id = HM.id 
    	        AND HM.activeFlag = SDI.activeFlag
    	    WHERE SDI.studentId = :studentId 
    	        AND SDI.activeFlag = :statusActive 
    	    ORDER BY HRAI.modifiedAt DESC LIMIT 1
    	""")
    	Object getStudentDetails(String studentId, String statusActive);

    @Query(value = "SELECT sdi FROM StudentDetailsInfoEntity sdi " +
            "WHERE sdi.studentId = :studentId AND sdi.activeFlag = :statusActive")
    Optional<StudentDetailsInfoEntity> getStudentInformationByStudentId(String studentId, String statusActive);
    
	@Modifying
	@Query("UPDATE StudentDetailsInfoEntity w SET w.dayScholar = :dayScholar, w.vacationCategory = :vacationCategory "
			+ ", w.modifiedBy = :userId, w.modifiedAt = :now WHERE w.studentId = :studentId")
	void updateStudentInfoDetails(String studentId, String dayScholar, String vacationCategory, String userId,
			LocalDateTime now);

	@Query(value = "SELECT s.student_id ,s.first_name ,s.last_name ,s.dob ,s.gender ,s.student_address ,s.city, s.state ,s.country \n" +
			" ,s.pin_code ,s.parent_email_id, '' as hostel_name,'' as room_no,s.alternate_contact_number " +
			" FROM "+ ModelConstants.SCHEMA+".\"STUDENT_DETAILS_INFO\" s " +
			" LEFT JOIN "+ ModelConstants.SCHEMA+".\"VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW\" h ON s.student_id = h.student_id " +
			" and h.vacate_date IS NULL AND h.shifted_date IS NULL AND h.active_flag = 'Y' " +
			" WHERE s.active_flag = 'Y' AND s.student_id = :studentId",
			nativeQuery = true)
	List<Object[]> findStudentDetailsWithHostelInfo(@Param("studentId") String studentId);


    @Query(value = "SELECT s FROM StudentDetailsInfoEntity s"
            + " WHERE UPPER(s.previousId) = UPPER(:previousId) AND s.activeFlag = :activeFlag")
    Optional<StudentDetailsInfoEntity> checkStudentIdExistInPreviousId(String previousId, String activeFlag);

    @Query(value = "SELECT s FROM StudentDetailsInfoEntity s"
            + " WHERE UPPER(s.studentId) = UPPER(:studentId) AND s.activeFlag = :activeFlag")
    Optional<StudentDetailsInfoEntity> checkStudentIdExist(String studentId, String activeFlag);

	@Query(value = "select student_id, student_name, hostel_name, hostel_id, room_number, student_mobile,gender " +
			"from schooldev.\"ALL_STUDENTS_DETAILS_VIEW\" b " +
			"WHERE settlement_flag = 'N' " +
			"and UPPER(student_id)=UPPER(:studentId)", nativeQuery = true)
	StudentRoomInfoDTO getStudentRoomDetails(String studentId);

	@Query(value = """
            Select a.settlement_flag ,b.n_fm_facility_master_id, n_fm_facility_master_name from schooldev."STUDENT_DETAILS_INFO" a  
			join schooldev."VACATED_STUDENT_VIEW" B on (a.student_id = b.v_sdi_studentid) 
			where a.student_id =upper(:studentId) and a.settlement_flag ='N';
            """, nativeQuery = true)
	List<Object[]> getPenaltyFacilityMasterId(String studentId);

	@Query(value = """
			 select a.settlement_flag, c.hostel_id, b.donated_hostel, b.donator_type, b.others_description from schooldev."STUDENT_DETAILS_INFO" a
			 join schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" b on (a.student_id = b.student_id)
			 left join  schooldev."HOSTEL_MASTER" c on (c.hostel_name=b.donated_hostel)
			 where a.student_id =upper(:studentId) and  a.settlement_flag='N';
            """, nativeQuery = true)
	List<Object[]> getDonationHostelId(String studentId);

	@Query(value = """
			SELECT student_id FROM schooldev."STUDENT_DETAILS_INFO" WHERE CONCAT(',', previous_id, ',') LIKE CONCAT('%,', :prevId, ',%') and active_flag = :activeFlag
			""", nativeQuery = true)
	List<String> getStudentIdByPrevId(String prevId, String activeFlag);
}