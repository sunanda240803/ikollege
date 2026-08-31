package com.iitm.hosteldine.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.entity.CourseAllocationInfoEntity;
import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;

@Repository
public interface CourseAllocationInfoRepository extends JpaRepository<CourseAllocationInfoEntity, Integer> {
    boolean existsByStudentIdAndActiveFlag(String studentId, String activeFlag);

    Optional<CourseAllocationInfoEntity> findByStudentIdAndActiveFlag(String studentId, String activeFlag);

	List<CourseAllocationInfoEntity> findAllByCourseIdAndActiveFlag(long courseId, String statusActive);

	CourseAllocationInfoEntity findByActiveFlagAndStudentId(String statusActive, String studentId);

	@Query("SELECT cm.courseMasterName FROM CourseAllocationInfoEntity cai JOIN CourseMasterEntity cm ON cai.courseId = cm.courseMasterId WHERE cai.activeFlag = :statusActive AND cai.studentId = :studentId")
	String findCourseNameByActiveFlagAndStudentId(String statusActive, String studentId);

	@Query("SELECT CM "
			+ "FROM CourseAllocationInfoEntity CAI "
			+ "JOIN CourseMasterEntity CM "
			+ "ON CAI.courseId = CM.courseMasterId "
			+ "WHERE CAI.activeFlag = :statusActive AND CAI.studentId = :studentId")
	CourseMasterEntity getStudentCourseDetails(String statusActive, String studentId);
}
