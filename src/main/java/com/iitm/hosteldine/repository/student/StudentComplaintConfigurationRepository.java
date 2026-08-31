package com.iitm.hosteldine.repository.student;

import java.util.List;
import java.util.Optional;

import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.student.StudentComplaintConfigurationEntity;

public interface StudentComplaintConfigurationRepository extends JpaRepository<StudentComplaintConfigurationEntity, Long> {

	List<StudentComplaintConfigurationEntity> findByActiveFlagAndComplaintType(String activeFlag, String complaintType);

	@Query("SELECT configMailId FROM StudentComplaintConfigurationEntity WHERE activeFlag = :activeFlag AND complaintType = :complaintType AND complaintName = :complaintName")
	Optional<String> getEmail(String activeFlag, String complaintType, String complaintName);

	@Query("select scc from StudentComplaintConfigurationEntity scc where scc.activeFlag = :statusActive and (scc.complaintName ILIKE CONCAT('%', :search, '%')" +
			"or scc.complaintType ILIKE CONCAT('%', :search, '%') or scc.configMailId ILIKE CONCAT('%', :search, '%'))")
	Page<StudentComplaintConfigurationEntity> getStudentComplaintConfigList(String statusActive, Pageable pageable, String search);

	Page<StudentComplaintConfigurationEntity> findAllByActiveFlag(String statusActive, Pageable pageable);


	
  

	

	

	
}