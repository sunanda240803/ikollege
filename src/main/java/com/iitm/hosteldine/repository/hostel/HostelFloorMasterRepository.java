package com.iitm.hosteldine.repository.hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;

public interface HostelFloorMasterRepository extends JpaRepository<HostelFloorMasterEntity, Long> {

	List<HostelFloorMasterEntity> findAllByActiveFlagOrderByHostelHostelNameAscFloorNameAsc(String statusActive);
	
	List<HostelFloorMasterEntity> findAllByActiveFlagAndHostelIdOrderByFloorName(String statusActive, long hostelId);

	Optional<HostelFloorMasterEntity> findByIdAndActiveFlag(long floorId, String statusActive);

	@Query("SELECT DISTINCT e.floorName FROM HostelFloorMasterEntity e where activeFlag =:statusActive")
	List<String> findDistinctHostelNames(String statusActive);

	List<HostelFloorMasterEntity> findByActiveFlagAndFloorNameIgnoreCaseAndHostelIdAndIdNot(String statusActive,
			String floorName, long hostelId, long id);
	
	boolean existsByActiveFlagAndHostelId(String statusActive, long hostelId);

	Page<HostelFloorMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	@Query("SELECT hfme FROM HostelFloorMasterEntity hfme join HostelMasterEntity hme on (hfme.hostel.id=hme.id AND hme.activeFlag = :statusActive)"
			+ "WHERE hfme.activeFlag = :statusActive AND (" 
			+ "hme.hostelName ILIKE CONCAT('%', :search, '%') OR "
			+ "hfme.floorName ILIKE CONCAT('%', :search, '%') OR " 
			+ "hfme.floorDesc ILIKE CONCAT('%', :search, '%'))")
	Page<HostelFloorMasterEntity> findByFloorMasterSearchList(String statusActive, Pageable pageable, String search);

}