package com.iitm.hosteldine.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.model.SimsConfigDataEntity;
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;

public interface SimsConfigDataRepository extends JpaRepository<SimsConfigDataEntity, Long> {
   
	Page<SimsConfigDataEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	Optional<SimsConfigDataEntity> findByIdAndActiveFlag(Long id, String statusActive);
	
	@Query("SELECT sce FROM SimsConfigDataEntity sce WHERE sce.activeFlag = :statusActive AND (" +
		       "sce.configKey ILIKE CONCAT('%', :search, '%') OR " +
		       "sce.configValue ILIKE CONCAT('%', :search, '%'))")
	Page<SimsConfigDataEntity> getSimsSearchList(String statusActive, Pageable pageable, String search);

	Optional<SimsConfigDataEntity> findTopByActiveFlagIgnoreCaseAndConfigKeyAndIdNot(String statusActive, String configKey,long id);

	Optional<SimsConfigDataEntity> findByConfigKeyIgnoreCaseAndActiveFlag(String key, String statusActive);
	

}