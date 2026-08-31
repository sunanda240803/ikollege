package com.iitm.hosteldine.repository.student.wellness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.student.wellness.StudentWellnessCategoricalDataEntity;

public interface StudentWellnessCategoricalDataRepository extends JpaRepository<StudentWellnessCategoricalDataEntity, Long> {
	
	Optional<StudentWellnessCategoricalDataEntity> findByIdAndActiveFlag(Long wellnessId, String activeFlag);
	
	@Query(value = "SELECT s FROM StudentWellnessCategoricalDataEntity s"
			+ " WHERE UPPER(s.studentId) = UPPER(:studentId) AND s.activeFlag = :activeFlag")
	StudentWellnessCategoricalDataEntity getWellnessEntityByStudentIdAndActiveFlag(String studentId, String activeFlag);
	
	@Query(value = "SELECT s, a FROM StudentWellnessCategoricalDataEntity s"
			+ " LEFT JOIN AllStudentsDetailsViewEntity a on (s.studentId = a.studentId)"
			+ " WHERE s.id = :wellnessId AND s.activeFlag = :activeFlag")
	Object[] getWellnessDetailsByIdAndActiveFlag(Long wellnessId, String activeFlag);

    
    static final String selectQuery = "SELECT * FROM schooldev.wellness_data_list(:studentName, :studentId, "
			+ "NULL, NULL, CAST(:referralDateFrom AS TEXT), CAST(:referralDateTo AS TEXT), "
			+ ":referralType, :concernType, NULL, :coordinatorName, :get, "
			+ "CAST(:visitDateFrom AS TEXT), CAST(:visitDateTo AS TEXT), "
			+ ":username, :role, :departmentCode)";
    
	@Query(value = selectQuery, nativeQuery = true)
	Page<Object[]> getAllStudentWellnessDetails(String studentName, String studentId, LocalDate referralDateFrom,
			LocalDate referralDateTo, String referralType, String concernType, String coordinatorName, String get,
			LocalDate visitDateFrom, LocalDate visitDateTo, String username, String role, String departmentCode,
			Pageable pageable);

	@Query(value = selectQuery, nativeQuery = true)
	List<Object[]> getAllStudentWellnessDetails(String studentName, String studentId, LocalDate referralDateFrom,
			LocalDate referralDateTo, String referralType, String concernType, String coordinatorName, String get,
			LocalDate visitDateFrom, LocalDate visitDateTo, String username, String role, String departmentCode);

	
}