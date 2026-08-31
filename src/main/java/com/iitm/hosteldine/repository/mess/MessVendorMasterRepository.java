package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.MessVendorMasterEntity;

import java.util.List;
import java.util.Optional;

public interface MessVendorMasterRepository extends JpaRepository<MessVendorMasterEntity, String> {

	Optional<MessVendorMasterEntity> findByVendorCodeAndActiveFlag(String id, String activeFlag);

	List<MessVendorMasterEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

	List<MessVendorMasterEntity> findAllByActiveFlagAndVendorCodeIgnoreCase(String activeFlag, String vendorCode);

	Page<MessVendorMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	@Query("SELECT mvm FROM MessVendorMasterEntity mvm WHERE mvm.activeFlag = :statusActive AND (" +
	           "mvm.vendorName ILIKE CONCAT('%', :search, '%') OR " +
	           "mvm.vendorCode ILIKE CONCAT('%', :search, '%') OR " +
	           "mvm.email ILIKE CONCAT('%', :search, '%'))") 
	Page<MessVendorMasterEntity> findByVendorMasterSearchList(String statusActive, Pageable pageable, String search);

	@Query("""
			SELECT new com.iitm.hosteldine.dto.mess.MessVendorAllocationDto(ven.vendorCode, ven.vendorName)
			FROM MessVendorMasterEntity ven
			WHERE ven.activeFlag = :isActive
			""")
	List<MessVendorAllocationDto> getVendorNamesByActiveStatus(String isActive);

	@Query(value = """
    select mvm from MessVendorMasterEntity as mvm
        left join MessVendorAllocationEntity as mva on mva.id.vendorCode = mvm.vendorCode and mva.activeFlag = :activeFlag
            left join MessMasterEntity as mm on mm.id = mva.id.messId and mm.activeFlag = :activeFlag
                where mm.id = :messId and mvm.activeFlag = :activeFlag order by mva.effectiveDate desc limit 1
    """)
	MessVendorMasterEntity getMessVendorMasterByMessId(Long messId,String activeFlag);
}