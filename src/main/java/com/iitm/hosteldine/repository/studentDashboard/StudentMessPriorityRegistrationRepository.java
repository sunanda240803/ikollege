package com.iitm.hosteldine.repository.studentDashboard;

import com.iitm.hosteldine.model.mess.StudentMessPriorityRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentMessPriorityRegistrationRepository extends JpaRepository<StudentMessPriorityRegistrationEntity, Long> {

    @Query(value = """
                select count(x) from StudentMessPriorityRegistrationEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);
    

	@Query(value = """
			    select e, mm.id,mm.messName,mm.description,mm.capacity from StudentMessPriorityRegistrationEntity e JOIN
			    MessMasterEntity mm on (e.id.priorityMessId = mm.id)
			    WHERE e.id.studentId =:studentId AND e.activeFlag =:statusActive AND
			    e.id.mmcNId = :messMasterControllerId ORDER BY e.id.priorityOrder
			""")
	List<Object[]> getPriorityMessRegistrationList(String studentId, String statusActive,
			Long messMasterControllerId);


	StudentMessPriorityRegistrationEntity findByActiveFlagAndIdStudentIdAndIdMmcNId(String statusActive,
			String studentId, int mmcId);


	StudentMessPriorityRegistrationEntity findByActiveFlagAndIdStudentIdAndIdMmcNIdAndIdPriorityMessId(String statusInactive, String studentId,
			int mmcId, int priorityMessId);


	List<StudentMessPriorityRegistrationEntity> findByActiveFlagAndIdMmcNIdAndIdStudentId(String statusInactive,
			int mmcId, String studentId);

	@Query(value = "SELECT SUM(student_count) AS student_count FROM (" +
            "    SELECT COUNT(DISTINCT(studentid)) AS student_count " +
            "    FROM schooldev.\"STUDENT_MESS_PRIORITY_REGISTRATION\" smpr " +
            "    JOIN schooldev.\"MESS_MASTER_CONTROLLER\" mmc " +
            "        ON smpr.mmc_n_id = mmc.id AND mmc.active_flag = :statusActive " +
            "    WHERE smpr.active_flag= :statusActive AND mmc.current_active_flag = :statusActive  " +
            "      AND smpr.priority_messid = :messId " +
            "      AND (CURRENT_DATE >= mmc.reg_begin_date::DATE " +
            "           AND CURRENT_DATE <= mmc.reg_end_date::DATE) " +
            "    UNION ALL " +
            "    SELECT COUNT(DISTINCT(smlp.student_id)) AS student_count " +
            "    FROM schooldev.\"STUDENT_MESS_LOGIN_ISSUE_PRIORITY\" smlp " +
            "    JOIN schooldev.\"MESS_MASTER_CONTROLLER\" mmc " +
            "        ON smlp.mmc_id = mmc.id AND mmc.active_flag = :statusActive " +
            "    WHERE smlp.active_flag = :statusActive AND mmc.current_active_flag = :statusActive " +
            "      AND smlp.priority_mess_id = :messId " +
            "      AND (CURRENT_DATE >= mmc.reg_begin_date::DATE " +
            "           AND CURRENT_DATE <= mmc.reg_end_date::DATE)" +
            ") AS a", nativeQuery = true)
	int getMessCount(int messId, String statusActive);


	@Query(value = "SELECT SUM(student_count) AS student_count FROM ( " +
            "    SELECT COUNT(DISTINCT smpr.studentid) AS student_count " +
            "    FROM schooldev.\"STUDENT_MESS_PRIORITY_REGISTRATION\" smpr " +
            "    JOIN schooldev.\"STUDENT_DETAILS_INFO\" sdi " +
            "        ON smpr.studentid = sdi.student_id " +
            "           AND sdi.active_flag = :statusActive " +
            "           AND smpr.active_flag = :statusActive " +
            "    JOIN schooldev.\"MESS_MASTER_CONTROLLER\" mmc " +
            "        ON smpr.mmc_n_id = mmc.id " +
            "    WHERE mmc.current_active_flag = 'Y' " +
            "      AND smpr.priority_messid = :messId " +
            "      AND (CURRENT_DATE >= mmc.reg_begin_date::DATE " +
            "           AND CURRENT_DATE <= mmc.reg_end_date::DATE) " +
            "      AND sdi.gender = 'F' " +
            "    UNION ALL " +
            "    SELECT COUNT(DISTINCT smlp.student_id) AS student_count " +
            "    FROM schooldev.\"STUDENT_MESS_LOGIN_ISSUE_PRIORITY\" smlp " +
            "    JOIN schooldev.\"MESS_MASTER_CONTROLLER\" mmc " +
            "        ON smlp.mmc_id = mmc.id " +
            "    WHERE mmc.current_active_flag = :statusActive " +
            
            "      AND smlp.priority_mess_id = :messId " +
            "      AND (CURRENT_DATE >= mmc.reg_begin_date::DATE " +
            "           AND CURRENT_DATE <= mmc.reg_end_date::DATE) " +
            "      AND smlp.gender = 'F' " +
            ") AS a", nativeQuery = true)
	int getGirlsRegCount(int messId, String statusActive);
	
	@Query("SELECT CONCAT(c.firstName, ' ', c.lastName) AS studentName, " +
		       "c.emailId AS parentEmailId, " +
		       "b.messName AS messName " +
		       "FROM StudentMessPriorityRegistrationEntity a " +
		       "JOIN MessMasterEntity b on (a.id.priorityMessId = b.id) " +
		       "JOIN StudentDetailsInfoEntity c on (a.id.studentId = c.studentId) " +
		       "WHERE b.activeFlag = :statusActive " +
		       "AND a.activeFlag = :statusActive " +
		       "AND a.id.studentId = :studentId " +
		       "AND a.id.mmcNId = :currentId " +
		       "ORDER BY a.id.priorityOrder")
	
	List<Object[]> getStudentMessPriorityList(Long currentId, String studentId, String statusActive);
	


	@Query(value = """
		((
        select
        	sem_mon,
        	studentid ,
        	b.student_name ,
        	gender,
        	priority_order,
        	mm.mess_name as messname,
        			 to_char(a.modified_at, 'DD-Mon-YY  HH24:MI') as Registrationtime,
        			 'Individual':: character varying(32) as student_type,
        	description,
        	null as joining_time
        from
        	schooldev."STUDENT_MESS_PRIORITY_REGISTRATION" a
        left join schooldev."MESS_MASTER" mm on
        	(priority_messid = mm.mess_master_id)
        left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on
        	(a.studentid = b.student_id)
        where
        	mmc_n_id = :messPeriodId
        	and priority_messid is not null
        	and priority_messid <> 0
        	and group_id = 0
        	and a.active_flag = :activeStatus
        order by
        	a.modified_at asc)
        union
        (
        select
        sem_mon,
        studentid,
        b.student_name,
        gender,
        gpm.priority_order,
        mm.mess_name,
        			 to_char(gpm.modified_at, 'DD-Mon-YY  HH24:MI') as Registrationtime,
        'Group':: character varying(32) as student_type,
        			 description,
        to_char(gpm.created_at, 'DD-Mon-YY  HH24:MI')
        from
        schooldev."STUDENT_MESS_GROUP_PRIORITY_REGISTRATION" gpm
        join schooldev."student_mess_group_view" sg on
        (sg.group_id = gpm.group_id)
        left join schooldev."MESS_MASTER" mm on
        (gpm.priority_messid = mess_master_id)
        left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on
        (studentid = b.student_id)
        where
        sg.mmc_n_id = :messPeriodId
        and priority_messid is not null
        and gpm.priority_messid <> 0
        and gpm.group_id>0
        and gpm.active_status = :activeStatus)
        order by  Registrationtime asc)
	""", nativeQuery = true)
	List<Object[]> getMessPriorityDetailsForReport(Integer messPeriodId, String activeStatus);


	@Query(value = """
		select
 		GM.group_name as groupname,
 		GM.created_student_id as leader ,
 		sd1.student_name as leadername,
 		sd1.gender as leadergender ,
 		PM.studentid as member,
 		sd2.student_name as membername
 		from
 			schooldev."STUDENT_MESS_GROUP_DETAILS" GM
 		join
 		(
 			select
 				studentid,
 				group_id,
 				count(*)
 			from
 				schooldev."STUDENT_MESS_PRIORITY_REGISTRATION"
 			where
 				mmc_n_id = :messPeriodId
 			group by
 				studentid,
 				group_id) PM on
 			(GM.group_id = PM.group_id)
 		left join schooldev."ALL_STUDENTS_DETAILS_VIEW" sd1 on
 			GM.created_student_id::text = sd1.student_id::text
 		left join schooldev."ALL_STUDENTS_DETAILS_VIEW" sd2 on
 			PM.studentid::text = sd2.student_id::text
 		order by
 			GM.group_name
	""",nativeQuery = true)
	List<Object[]> getStudentMessGroupList(Integer messPeriodId);
}