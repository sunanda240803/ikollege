package com.iitm.hosteldine.repository.studentDashboard;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;

public interface StudentMessDetailsRepository extends JpaRepository<StudentMessDetailsEntity, Long> {
    Optional<StudentMessDetailsEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<StudentMessDetailsEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StudentMessDetailsEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);
    
    @Query(value = """
			    SELECT smd.studentDetailsInfo.studentId, smd.messMaster.id, smd.messMaster.messName
			    FROM StudentMessDetailsEntity smd
			    JOIN MessMasterEntity mm
			        ON mm.id = smd.messMaster.id
			        AND mm.activeFlag = smd.activeFlag
			    JOIN MessMasterControllerEntity mmc
			        ON mmc.diningFromDate = smd.fromDate
			        AND smd.toDate = mmc.diningToDate
			    WHERE smd.studentDetailsInfo.studentId = :studentId
			      AND mmc.id = :mmcId
			      AND smd.activeFlag = :statusActive
			      AND smd.currentActiveFlag = :statusActive
    	""")
	List<Object[]> fetchStudentDetails(String studentId, Long mmcId, String statusActive);

	Optional<StudentMessDetailsEntity> findByStudentDetailsInfo_StudentIdAndFromDateAndToDateAndActiveFlagAndCurrentActiveFlag(String studentId,
																										   LocalDate fromDate,
																										   LocalDate toDate,
																										   String activeFlag,String currentActiveFlag);

	@Query(value = """
	select smde from StudentMessDetailsEntity smde where smde.studentDetailsInfo.studentId = :studentId 
		and smde.activeFlag = :activeFlag and smde.mmcId = :mmcId
	""")
	Optional<StudentMessDetailsEntity> getStudentMessDetailsByStudentIdAndFromDateAndToDate(String studentId,String activeFlag,
																							Long mmcId);

	@Query(value = """
				SELECT smd.id, smd.studentDetailsInfo.studentId, smd.fromDate, smd.toDate, sdv.studentName, sdv.gender,
			mm.messName, mm.messHead, smd.messMaster.id, smd.changeFromDate , smd.changeToDate
			FROM StudentMessDetailsEntity smd
			JOIN AllStudentsDetailsViewEntity sdv ON (sdv.studentId = smd.studentDetailsInfo.studentId)
			JOIN MessMasterEntity mm ON (mm.id = smd.messMaster.id)
			WHERE smd.activeFlag = :status AND smd.currentActiveFlag = :status AND smd.id = :id
				""")
	Object[] getStudentMessDetailsByIdAndActiveFlag(@Param("id") Long id, @Param("status") String status);

	@Query(value = """
				SELECT smd.fromDate, smd.toDate,mm.messName
			FROM StudentMessDetailsEntity smd
			LEFT JOIN MessMasterEntity mm ON (mm.id = smd.messMaster.id and mm.activeFlag= :status)
			WHERE upper(smd.studentDetailsInfo.studentId)  = :studentId and :fromDate <=  smd.toDate and :toDate >= smd.fromDate and
						smd.activeFlag = :status AND smd.currentActiveFlag = :status ORDER BY smd.id DESC limit 1
				""")
	Object[] checkStudentAllottedMessPeriod(String studentId,String status,LocalDate fromDate,LocalDate toDate);


	boolean existsByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(String studentId, String activeFlag,
			Long id);

	@Modifying
	@Query("UPDATE StudentMessDetailsEntity s SET s.messMaster.id = :messId "
			+ "WHERE s.studentDetailsInfo.studentId = :studentId AND s.mmcId = :mmcId AND s.activeFlag = :activeFlag")
	void updateByMessByStudentIDFromToDates(Long messId, String studentId, Long mmcId, String activeFlag);

	Optional<StudentMessDetailsEntity> findByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(String studentId,
			String activeFlag, Long id);

	@Modifying
	@Query("UPDATE StudentMessDetailsEntity s SET s.changeFromDate = :fromdate, s.comments = :comments, s.modifiedBy = :user, s.modifiedAt = :now "
			+ "WHERE s.studentDetailsInfo.studentId = :studentId AND s.mmcId = :mmcId AND s.currentActiveFlag = :activeFlag")
	int updateFromDateAndCommentsToEntity(LocalDate fromdate, String comments, String user, LocalDateTime now,
			String studentId, Long mmcId, String activeFlag);
}