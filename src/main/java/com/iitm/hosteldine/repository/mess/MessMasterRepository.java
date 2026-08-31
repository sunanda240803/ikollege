package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MessMasterRepository extends JpaRepository<MessMasterEntity, Long> {
    Optional<MessMasterEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<MessMasterEntity> findAllByActiveFlagOrderByMessNameAsc(String activeFlag);

	List<MessMasterEntity> findAllByActiveFlagAndDescriptionOrderByMessNameAsc(String statusActive,String description);
    
	List<MessMasterEntity> findAllByActiveFlagAndOnlineCouponAndIsVegNonVegOrderByMessNameAsc(String statusActive,Boolean onlineCoupon,String vegOrNonveg);

	List<MessMasterEntity> findAllByActiveFlagAndMessNameIgnoreCaseAndIdNot(String statusActive, String messName, long id);

	List<MessMasterEntity> findAllByActiveFlagAndMessHeadIgnoreCaseAndIdNot(String statusActive, String messHead, long id);

	@Query("SELECT "
            + "c.id, "
            + "c.messName, "
            + "c.applicableStatus, "
            + "c.capacity, "
            + "COALESCE(d.allotted_count, 0) AS allotted_count, "
            + "c.capacity - COALESCE(d.allotted_count, 0) AS available_count "
            + "FROM MessMasterEntity c "
            + "LEFT JOIN ( "
            + "    SELECT COUNT(*) AS allotted_count, b.messMaster.id AS mess_id "
            + "    FROM MessMasterControllerEntity a "
            + "    JOIN StudentMessDetailsEntity b "
            + "    ON a.diningFromDate = b.fromDate "
            + "    AND a.diningToDate = b.toDate "
            + "    AND b.currentActiveFlag = :statusActive "
            + "    AND a.currentActiveFlag = :statusActive "
            + "    GROUP BY b.messMaster.id "
            + ") d ON c.id = d.mess_id "
            + "WHERE c.activeFlag = :statusActive "
            + "ORDER BY c.messName ASC")
List<Object[]> getMessMasterConfigList(String statusActive);

	Page<MessMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	 @Query("SELECT mm FROM MessMasterEntity mm WHERE mm.activeFlag = :statusActive AND (" +
	           "mm.messName ILIKE CONCAT('%', :search, '%') OR " +
	           "mm.messFloorName ILIKE CONCAT('%', :search, '%') OR " +
	           "mm.messHead ILIKE CONCAT('%', :search, '%') OR " +
	           "mm.description ILIKE CONCAT('%', :search, '%'))")
	Page<MessMasterEntity> getMessMasterSearchList(String statusActive, Pageable pageable, String search);
	 
	
	 
	 /*@Query("SELECT m.id FROM MessMasterEntity m " +
		       "WHERE m.messType = :messType " +
		       "AND m.activeFlag = 'Y' " +
		       "AND m.sickFoodAvail = true")
		Optional<Long> getMessMasterIdByMessType(String messType);*/

	@Query("SELECT m.id FROM MessMasterEntity m " +
			"WHERE m.activeFlag = 'Y' " +
			"AND m.sickFoodAvail = true")
	Optional<Long> getMessMasterIdByMessType();


	 @Query(value="""
		 		SELECT mm FROM MessMasterEntity mm WHERE mm.activeFlag = :statusActive AND mm.id  IN :option ORDER BY messName
		 		""")
	List<MessMasterEntity> getPriorityMessMasterList(List<Long> option ,String statusActive);

	 @Query(value="SELECT mm FROM MessMasterEntity mm WHERE mm.activeFlag = :statusActive")
	 List<MessMasterEntity> getMessMasterList(String statusActive);

	// Fetch mess ID and mess name for a given caterer name and active flag
	@Query(value = "SELECT a.caterer_name, d.mess_master_id, d.mess_name FROM "
			+ ModelConstants.SCHEMA + ".\"CATERER_LEDGER_MAPPING\" a "
			+ "JOIN " + ModelConstants.SCHEMA + ".\"MESS_ALLOCATION\" c "
			+ "ON c.vendor_code = acchead AND c.active_flag = :activeFlag "
			+ "JOIN " + ModelConstants.SCHEMA + ".\"MESS_MASTER\" d "
			+ "ON d.mess_master_id = c.mess_master_id AND d.active_flag = :activeFlag "
			+ "WHERE caterer_name = :userName AND a.active_flag = :activeFlag",
			nativeQuery = true)
	List<Object[]> getMessDetailsForCaterer(String userName, String activeFlag);


	@Query(value = """
             SELECT coalesce(mm.mess_master_id, 0), coalesce(mm.mess_name, null)
                FROM schooldev.current_mess_period cmp
                         LEFT JOIN schooldev."STUDENT_MESS_DETAILS" smd
                         ON  smd.mmc_id = cmp.id
                         AND smd.student_id = :studentId
                         AND smd.current_active_flag = :statusActive
                         AND smd.active_flag = :statusActive
                         LEFT JOIN schooldev."MESS_MASTER" mm ON mm.mess_master_id = smd.mess_id
	               \s""", nativeQuery = true)
		List<Object[]> getMessList( String studentId, String statusActive);

	 @Query("""
				SELECT new com.iitm.hosteldine.dto.mess.MessVendorAllocationDto(mess.id, mess.messFloorName)
				FROM MessMasterEntity mess
				WHERE mess.activeFlag = :isActive
				""")
	 List<MessVendorAllocationDto> getMessFloorsByActiveFlag(String isActive);

		@Query(value = """
				SELECT mm FROM MessMasterEntity mm WHERE mm.activeFlag = :activeFlag AND LOWER(mm.messHead) = LOWER(:messHead)
				""")
		Optional<MessMasterEntity> getMessMasterDetailsByMessHead(String messHead, String activeFlag);

	Optional<MessMasterEntity> findFirstByActiveFlagAndMessHeadIgnoreCaseOrderByCreatedAtAsc(String activeFlag, String messHead);


	@Query(value = """
        SELECT 
            b.v_sdi_firstname AS student_name,
            b.v_sdi_studentid AS student_id,
            mess_head,
            b.mess_name,
            c.dined_days,
            b.in_date AS date,
            sum(case when (mess_session_id='BF') then 1 else 0 end) AS breakfast_count,
            sum(case when (mess_session_id='LC') then 1 else 0 end) AS lunch_count,
            sum(case when (mess_session_id='DR') then 1 else 0 end) AS dinner_count
        FROM schooldev.students_present_for_mess_view b 
        JOIN schooldev.student_dined_summary_view c 
            ON b.v_sdi_studentid = c.v_sdi_studentid 
            AND b.mess_master_id = c.mess_master_id
        WHERE b.mess_master_id =:messMasterId
            AND c.mmc_n_id = :mmcNId
        GROUP BY 
            b.v_sdi_firstname,
            b.v_sdi_studentid,
            mess_head,
            b.mess_name,
            c.dined_days,
            b.in_date
        ORDER BY b.v_sdi_studentid, b.in_date
        """,
			nativeQuery = true)
	List<Object[]> findMessAttendanceDataNative( @Param("mmcNId") Long mmcNId ,@Param("messMasterId") Long messMasterId);

	@Query(value = """
        SELECT 
            ROW_NUMBER() OVER () as serialNumber,
            a.student_id as studentId,
            (b.first_name || ' ' || b.last_name) AS studentName,
            c.mess_name as messName,
            a.from_date as fromDate,
            a.to_date as toDate
        FROM schooldev."STUDENT_MESS_DETAILS" a
        JOIN schooldev."STUDENT_DETAILS_INFO" b ON (a.student_id = b.student_id)  
        JOIN schooldev."MESS_MASTER" c ON (c.mess_master_id = a.mess_id)  
        WHERE a.student_id NOT IN (
            SELECT d.v_sdi_studentid
            FROM schooldev.student_dined_summary_view AS d 
            WHERE (:fromDate = d.mmc_d_dining_fromdate::date  
                   AND :toDate <= d.mmc_d_dining_todate::date)
        ) 
        AND (:fromDate = a.from_date::date  
             AND :toDate <= a.to_date::date)
        AND a.mess_id = :messId AND a.active_flag = 'Y'
        """, nativeQuery = true)
	List<Object[]> findStudentsNotDinedRaw(
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			@Param("messId") Long messId);

	@Query(value = """
			select b.from_date,b.to_date,d.mess_name,e.mess_master_id,a.id,e.rate,
			(DATE_PART('day', b.to_date::timestamp- b.from_date::timestamp)+1) as total_days,count(*) as student_alloted
			from schooldev."MESS_MASTER_CONTROLLER" a
			join schooldev."STUDENT_MESS_DETAILS" b on (a.dining_from_date=b.from_date and a.dining_to_date = b.to_date )
			join schooldev."STUDENT_DETAILS_INFO" c on (c.student_id=b.student_id)
			left join schooldev."MESS_MASTER"  d on (d.mess_master_id=b.mess_id ) 
			left join schooldev."MESS_ALLOCATION" e  on (e.mess_master_id= b.mess_id and e.mess_effective_date::date <=b.from_date)
			WHERE a.id= :messPeriodId
			and b.mess_id= :messId
			group by b.from_date,b.to_date,d.mess_name
			,e.mess_master_id,a.id,e.rate,e.created_at order by e.created_at desc limit 1 
        """, nativeQuery = true)
	List<Object[]> getMessAllottedDineDetails(
			@Param("messPeriodId") Long messPeriodId,
			@Param("messId") Long messId);

	@Query(value = """
        SELECT 
            (SELECT count(*) as session_total_count 
             FROM schooldev.students_present_for_mess_view a
             WHERE in_date::date >= :fromDate AND in_date::date <= :toDate  
             AND mess_master_id = :messId
             GROUP BY mess_name) as session_total_count, 
            (SELECT sum(a.total_count) as day_total_count 
             FROM schooldev.student_mess_dineddays_summary_view a  
             WHERE a.mmc_n_id = :messPeriodId AND a.mess_master_id = :messId) as day_total_count
        """, nativeQuery = true)
	List<Object[]> getMessAllottedDinedCount(
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			@Param("messId") Long messId,
			@Param("messPeriodId") Long messPeriodId);

}