package com.iitm.hosteldine.repository.studentDashboard;

import com.iitm.hosteldine.model.mess.StudentMessLoginIssuePriorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentMessLoginIssuePriorityRepository extends JpaRepository<StudentMessLoginIssuePriorityEntity, Long> {
   // Optional<StudentMessLoginIssuePriorityEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<StudentMessLoginIssuePriorityEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StudentMessLoginIssuePriorityEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<StudentMessLoginIssuePriorityRepository> findByActiveFlagAndId_StudentIdAndId_MmcNId(String statusActive,
			String studentId, int currentId);

    @Query(value = """
        select
    	a.student_id,
    	a.student_name,
    	gender,
    	priority_order,
    	mm.mess_name,
    	mm.description,
    	to_char(a.created_at, 'DD-Mon-YY  HH24:MI') as registrationtime
        from
        	schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" a
        left join schooldev."MESS_MASTER" mm on
        	(a.priority_mess_id = mm.mess_master_id)
        where
        	mmc_id = :messPeriodId
        	and priority_mess_id is not null
        	and priority_mess_id <> 0
            and a.active_flag = :activeFlag
        order by a.created_at asc
    """, nativeQuery = true)
    List<Object[]> getStudentLoginIssueForReport(Integer messPeriodId,String activeFlag);
}