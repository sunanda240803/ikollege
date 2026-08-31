package com.iitm.hosteldine.repository.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.asset.AssetMaintenanceTypeEntity;

import java.util.List;
import java.util.Optional;

public interface AssetMaintenanceTypeRepository extends JpaRepository<AssetMaintenanceTypeEntity, Long> {

	List<AssetMaintenanceTypeEntity> findAllByActiveFlagOrderByMaintenanceType(String statusActive);

	Optional<AssetMaintenanceTypeEntity> findByMaintenanceTypeIdAndActiveFlag(Long id, String statusActive);

	Optional<AssetMaintenanceTypeEntity> findByActiveFlagAndMaintenanceTypeIgnoreCaseAndMaintenanceTypeIdNot(String statusActive,
			String categoryName, Long id);

	@Query("select e from AssetMaintenanceTypeEntity e  where e.activeFlag=:statusActive AND (e.maintenanceType ILIKE CONCAT('%', :search, '%') "
			+ " ) order By e.maintenanceType")
	List<AssetMaintenanceTypeEntity> findByMaintenanceTypeSearch(String statusActive, String search);
}