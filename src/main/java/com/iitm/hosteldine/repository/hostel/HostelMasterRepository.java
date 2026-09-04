package com.iitm.hosteldine.repository.hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.hostel.HostelMasterEntity;

public interface HostelMasterRepository extends JpaRepository<HostelMasterEntity, Long> {

	List<HostelMasterEntity> findAllByActiveFlagOrderByHostelName(String statusActive);

	Optional<HostelMasterEntity> findByIdAndActiveFlag(long hostelId, String statusActive);

	List<HostelMasterEntity> findByActiveFlagAndHostelNameIgnoreCaseAndIdNot(String statusActive, String hostelName,
			Long id);

	List<HostelMasterEntity> findByActiveFlagAndHostelShortCodeIgnoreCaseAndIdNot(String statusActive,
			String hostelCode, long id);

	Page<HostelMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	@Query("SELECT hme FROM HostelMasterEntity hme WHERE hme.activeFlag = :statusActive AND (" +
	           "hme.hostelName ILIKE CONCAT('%', :search, '%') OR " +
	           "hme.hostelOfficeEmail ILIKE CONCAT('%', :search, '%') OR " +
	           "hme.hostelShortCode ILIKE CONCAT('%', :search, '%'))") 
	Page<HostelMasterEntity> findByHostelMasterSearchList(String statusActive, Pageable pageable, String search);

    @Query(value = "SELECT h FROM HostelMasterEntity h"
            + " WHERE UPPER(h.hostelName) = UPPER(:hostelName) AND h.activeFlag = :activeFlag")
    HostelMasterEntity checkHostelExist(String hostelName, String activeFlag);

    @Query(value = "SELECT a, b, c FROM HostelMasterEntity a"
    		+ " LEFT JOIN HostelFloorMasterEntity b ON (a.id = b.hostel.id AND b.activeFlag = :activeFlag)"
    		+ " LEFT JOIN HostelRoomInfoEntity c ON (b.id = c.building.id AND c.activeFlag = :activeFlag)"
    		+ " WHERE UPPER(a.hostelName) = UPPER(:hostelName) AND c.roomNo = :roomNo AND a.activeFlag = :activeFlag")
    Object[] getHostelAndFloorAndRoomDetails(String hostelName, String roomNo, String activeFlag);

	List<HostelMasterEntity> findByActiveFlagAndHostelName(String statusActive, String hostelName);
}