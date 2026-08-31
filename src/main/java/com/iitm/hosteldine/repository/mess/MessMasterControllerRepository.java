package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessMasterControllerRepository extends JpaRepository<MessMasterControllerEntity, Long> {
    Optional<MessMasterControllerEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<MessMasterControllerEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

	List<MessMasterControllerEntity> findAllByActiveFlagOrderByIdDesc(String activeFlag);


    @Query(value = """
                select count(x) from MessMasterControllerEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<MessMasterControllerEntity> findAllByCurrentActiveFlag(String statusActive);

	@Query(value = """
			  	SELECT
		        id,LEAD(id) OVER (ORDER BY id DESC) AS previousId,
		        month,LEAD(month) OVER (ORDER BY id DESC) AS previousMonth,
		        feedback_status,LEAD(feedback_status) OVER (ORDER BY id DESC) AS prevFbStatus
				FROM schooldev."MESS_MASTER_CONTROLLER" LIMIT 1
		    """, nativeQuery = true)
		Optional<Object[]> getMessPeriodDetails();
		
		@Query(value = """
			    SELECT mmc  FROM MessMasterControllerEntity mmc
			   JOIN StudentMessDetailsEntity smd
			    ON smd.fromDate = mmc.diningFromDate 
			    AND smd.toDate = mmc.diningToDate
			    AND mmc.activeFlag = 'Y' AND smd.activeFlag = 'Y' 
			    WHERE mmc.id = :previousId
			    AND smd.studentDetailsInfo.studentId = :studentId
			    AND mmc.currentActiveFlag = 'N'
			""")
	Optional<MessMasterControllerEntity> getStudentMessFeedBackStaus(Long previousId, String studentId);

	@Query(value = """
    SELECT mmc.reg_begin_date, mmc.reg_begin_time, 
           mmc.reg_end_date, mmc.reg_end_time
    FROM schooldev."MESS_MASTER_CONTROLLER"  mmc
    WHERE mmc.current_active_flag = :statusActive AND mmc.active_flag = :statusActive 
    AND CURRENT_TIMESTAMP BETWEEN 
        (mmc.reg_begin_date || ' ' || mmc.reg_begin_time)::timestamp
        AND (mmc.reg_end_date || ' ' || mmc.reg_end_time)::timestamp
""", nativeQuery = true)
	List<Object[]> checkRegistrationDate(String statusActive);


	@Query(value = """
			    SELECT mmc, smd.messMaster.id, smd.messMaster.messName FROM MessMasterControllerEntity mmc 
			    JOIN StudentMessDetailsEntity smd
			    ON smd.fromDate = mmc.diningFromDate 
			    AND smd.toDate = mmc.diningToDate
			    AND mmc.activeFlag = 'Y' JOIN MessMasterEntity mm 
			    ON (mm.id=smd.messMaster.id) where smd.studentDetailsInfo.studentId = :studentId
				AND smd.activeFlag = :statusActive AND mmc.feedbackStatus = TRUE AND mmc.id=:previousId and smd.currentActiveFlag=:statusActive
			""")
		List<Object[]> getStudentMessDetails(String studentId, Long previousId,String statusActive);

	Optional<MessMasterControllerEntity> findByCurrentActiveFlag(String statusActive);

	@Query(value = """
		select mmce from MessMasterControllerEntity  mmce where mmce.activeFlag = :activeFlag 
			AND (:search IS NULL OR :search = '' 
                       OR mmce.month ILIKE %:search% )
			order by mmce.currentActiveFlag desc,mmce.id desc
	""")
	Page<MessMasterControllerEntity> getAllByActiveFlagAndSearchFilter(String activeFlag, String search, Pageable pageable);

	@Transactional
	@Modifying
	@Query(value = """
			    update MessMasterControllerEntity mmc 
			    set mmc.currentActiveFlag = 'N' 
			    where mmc.activeFlag = :activeFlag
			""")
	void updateAllMessPeriodConfig(String activeFlag);


	@Query(value = """
			select smd,asdv from StudentMessDetailsEntity smd
				join fetch smd.messMaster mme
          		join fetch smd.studentDetailsInfo sdi
				join MessMasterControllerEntity mmc
					on (smd.fromDate = mmc.diningFromDate and smd.toDate = mmc.diningToDate
						and smd.currentActiveFlag = :activeFlag and smd.activeFlag = :activeFlag)
				join AllStudentsDetailsViewEntity asdv on (smd.studentDetailsInfo.studentId = asdv.studentId)
				where mmc.currentActiveFlag = :activeFlag and smd.mailStatus is null and mme.activeFlag = : activeFlag
			""")
	Optional<List<Object[]>> getAllMessStudents(String activeFlag);

	@Query(value = """
			    SELECT x FROM MessMasterControllerEntity x WHERE x.diningFromDate IS NOT NULL AND x.diningToDate IS NOT NULL
			    AND x.activeFlag = :activeFlag ORDER BY x.diningFromDate DESC
			""")
	Optional<List<MessMasterControllerEntity>> getMessMasterList(@Param("activeFlag") String activeFlag);
    
	@Query(value = """
			SELECT * FROM schooldev.mess_allotted_list(:studentName, :studentId, :messPeriodId, :messMasterId)
			""", nativeQuery = true)
	Page<Object[]> getMessAllottedList(@Param("studentName") String studentName, @Param("studentId") String studentId,
			@Param("messPeriodId") Long messPeriodId, @Param("messMasterId") Long messMasterId, Pageable pageable);

	@Query(value = """
			SELECT * FROM schooldev.mess_allotted_list(:studentName, :studentId, :messPeriodId, :messMasterId)
			""", nativeQuery = true)
	List<Object[]> getMessAllottedList(@Param("studentName") String studentName, @Param("studentId") String studentId,
			@Param("messPeriodId") Long messPeriodId, @Param("messMasterId") Long messMasterId);

	@Query(value = """
			SELECT * FROM schooldev.current_mess_period
			   """, nativeQuery = true)
	Optional<MessMasterControllerEntity> getCurrentMessPeriod();

	@Query(value = """
			   SELECT * FROM schooldev.next_mess_period
			""", nativeQuery = true)
	Optional<MessMasterControllerEntity> getNextMessPeriod();

	@Query(value = """
			select M.mess_name,SMD.student_id, M.mess_master_id,
			   ((DATE_PART('day', change_to_date ::timestamp - change_from_date::timestamp)+1) * M.food_court_amount) as total_amount, SMD.change_from_date, SMD.change_to_date, M.food_court_amount
			   from schooldev."STUDENT_MESS_DETAILS" SMD
			   join schooldev."MESS_MASTER" M  on (M.mess_master_id=SMD.mess_id and M.is_food_court =true and M.active_flag= :activeFlag)
			   left join schooldev."FOOD_COURT_LEDGER_VIEW" FCV  on (FCV.student_id=SMD.student_id and FCV.mess_period_id= :messPeriodId
			   and SMD.mess_id=FCV.fc_mess_id and  total_credit_amount > 0)
			   WHERE smd.mmc_id= :messPeriodId and SMD.active_flag = :activeFlag and SMD.current_active_flag = :activeFlag and FCV.student_id is null
			   and (total_credit_amount <=0 or total_credit_amount is null)
	""", nativeQuery = true)
	Optional<List<Object[]>> getFoodCourtCreditList(String activeFlag, Long messPeriodId);
	
	@Query(value = """
			SELECT * FROM schooldev.mess_billing_report(:messMaster, :messPeriod)
			   """, nativeQuery = true)
	List<Object[]> getMessBillSummaryReport(Integer messMaster, Integer messPeriod);

    Optional<MessMasterControllerEntity> findById(Long id);
    
    @Query(value = """
			select DISTINCT (to_char(dining_from_date::date, 'DD-Mon-YYYY') ||' to '||  to_char(dining_to_date::date, 'DD-Mon-YYYY')) as mess_period,
			id from schooldev."MESS_MASTER_CONTROLLER"  where dining_from_date is not null and dining_to_date is not null order by id desc
						""", nativeQuery = true)
	List<Object[]> getMessPeriod();
	
	@Query(value = "SELECT * FROM schooldev.self_mess_allotted_list( " +
			" cast(:fromdate as varchar), " +
			" cast(:todate as varchar), " +
			" cast(:id as bigint) " +
			");", nativeQuery = true)
	Page<Object[]> getStudentMessSelfAllocationList(
			@Param("fromdate") String fromDate,
			@Param("todate") String todate,
			@Param("id") Long id,
			Pageable pageable);
	
}