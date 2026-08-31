package com.iitm.hosteldine.repository.student.wellness;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.iitm.hosteldine.model.student.wellness.WellnessUserManagementEntity;

public interface WellnessUserManagementRepository extends JpaRepository<WellnessUserManagementEntity, String> {
	
	@Query(value = "SELECT w.password FROM WellnessUserManagementEntity w WHERE w.username = :username")
	String getUserPassword(String username);
	
}