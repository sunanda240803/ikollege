package com.iitm.hosteldine.repository.staff;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.staff.StaffDesignationMasterEntity;

public interface StaffDesignationMasterRepository extends JpaRepository<StaffDesignationMasterEntity, Long> {
	
    Optional<StaffDesignationMasterEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    
    List<StaffDesignationMasterEntity>findAllByActiveFlagOrderByDesignationName(String statusActive);

	
	Optional<StaffDesignationMasterEntity> findByActiveFlagAndDesignationNameIgnoreCaseAndIdNot(String statusActive,
			String designationName, long id);
}