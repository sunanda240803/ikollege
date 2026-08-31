package com.iitm.hosteldine.repository.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.student.StudentComplaintDetailsEntity;
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentComplaintDetailsRepository extends JpaRepository<StudentComplaintDetailsEntity, Long> {
  

	

	Page<StudentComplaintDetailsEntity> findAllByStudentIdAndActiveFlag(String studentId, String statusActive,
			Pageable pageable);
	
	@Query("SELECT c FROM StudentComplaintDetailsEntity c " +
	           "WHERE c.studentId = :studentId AND c.activeFlag = :statusActive " +
	           "AND (LOWER(c.stuComplaintType) LIKE LOWER(CONCAT('%', :search, '%')) " +
	           "OR LOWER(c.complaints) LIKE LOWER(CONCAT('%', :search, '%')) " +
	           "OR LOWER(c.complaintDesc) LIKE LOWER(CONCAT('%', :search, '%')))")
	    Page<StudentComplaintDetailsEntity> findByComplaintSearchList(
	           String studentId,
	           String statusActive,
	            String search,
	            Pageable pageable);
	
	
	@Query("SELECT s.stuComplaintType, COUNT(s) AS complaintCount " +
		       "FROM StudentComplaintDetailsEntity s " +
		       "WHERE s.activeFlag = :statusActive " +
		       "AND s.stuComplaintType IN :complaintTypes " +
		       "AND s.studentId = :studentId " +
		       "GROUP BY s.stuComplaintType")
		List<Object[]> getComplaintCountsByType(String statusActive, 
		                                        String studentId, 
		                                        List<String> complaintTypes);

	@Query("""
		SELECT s FROM StudentComplaintDetailsEntity s
		WHERE s.activeFlag = :activeFlag
		  AND s.createdAt BETWEEN :startDate AND :endDate
		  AND (:complaintType IS NULL OR s.stuComplaintType = :complaintType)
	""")
	List<StudentComplaintDetailsEntity> findComplaints(String complaintType, LocalDateTime startDate, LocalDateTime endDate, String activeFlag);
}