package com.iitm.hosteldine.repository.collegeInfo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;

public interface CourseMasterRepository extends JpaRepository<CourseMasterEntity, Long> {
	
	@Query("""
			select cm.courseMasterId as courseMasterId, cm.courseMasterName as courseMasterName,
			       cm.description as description, cm.degreeAwarded as degreeAwarded,
			       cm.affiliation as affiliation,
			       dd.departmentName as departmentName,cm.courseMasterHead as courseMasterHead
			from CourseMasterEntity cm
			left join DepartmentEntity dd on cm.departmentId = dd.departmentId and dd.activeFlag= :statusActive
			where cm.activeFlag = :statusActive
			order by cm.courseMasterName
			""")
	List<Object[]> findAllByActiveFlagOrderByCourseMasterName(@Param("statusActive") String statusActive);
	//List<CourseMasterEntity> findAllByActiveFlagOrderByCourseMasterName( String statusActive);

	Optional<CourseMasterEntity> findByCourseMasterIdAndActiveFlag(long courseMasterId, String statusActive);

	Optional<CourseMasterEntity> findByCourseMasterId(long id);

	Optional<CourseMasterEntity> findByActiveFlagAndCourseMasterNameIgnoreCaseAndCourseMasterIdNot(String statusActive,
			String courseMasterName, long id);

	Optional<CourseMasterEntity> findByCourseMasterHeadIgnoreCaseAndActiveFlag(String courseMasterHead, String activeFlag);

	boolean existsByCourseMasterHeadIgnoreCaseAndActiveFlag(String courseMasterHead, String activeFlag);

	Long countByDepartmentIdAndActiveFlag(long id, String statusActive);

	List<CourseMasterEntity> findByActiveFlagAndCourseMasterHeadIgnoreCaseAndCourseMasterIdNot(String statusActive, String courseCode,long id);
	@Query(value="select distinct (e.courseMasterHead) from CourseMasterEntity e where e.activeFlag=:activeFlag and e.courseMasterHead is not null")
	List<String> findAllByActiveFlagOrderByCourseMasterHeadAsc(String activeFlag);
	
	@Query("""
			select cm.courseMasterId as courseMasterId, cm.courseMasterName as courseMasterName,
			       cm.description as description, cm.degreeAwarded as degreeAwarded,
			       cm.affiliation as affiliation,
			       dd.departmentName as departmentName,cm.courseMasterHead as courseMasterHead
			from CourseMasterEntity cm
			left join DepartmentEntity dd on cm.departmentId = dd.departmentId and dd.activeFlag= :statusActive
			where cm.activeFlag = :statusActive
			""")
	Page<Object[]> findAllByActiveFlag(String statusActive, Pageable pageable);

	
	@Query("""
			select cm.courseMasterId as courseMasterId, cm.courseMasterName as courseMasterName,
			       cm.description as description, cm.degreeAwarded as degreeAwarded,
			       cm.affiliation as affiliation,
			       dd.departmentName as departmentName,cm.courseMasterHead as courseMasterHead
			from CourseMasterEntity cm
			left join DepartmentEntity dd on cm.departmentId = dd.departmentId and dd.activeFlag= :statusActive
			where (cm.courseMasterName ilike concat('%',:search,'%') OR cm.description ilike concat('%',:search,'%') 
			OR cm.courseMasterHead ilike concat('%',:search,'%') OR dd.departmentName ilike concat('%',:search,'%'))
			and cm.activeFlag = :statusActive
			""")
	Page<Object[]> findCourseMasterDetailsAndActiveFlag(String search, String statusActive, Pageable pageable);
}