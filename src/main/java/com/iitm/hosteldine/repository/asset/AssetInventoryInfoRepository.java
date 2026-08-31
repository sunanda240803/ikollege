package com.iitm.hosteldine.repository.asset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.asset.AssetInventoryInfoEntity;

public interface AssetInventoryInfoRepository extends JpaRepository<AssetInventoryInfoEntity, Long> {

	Optional<AssetInventoryInfoEntity> findByActiveFlagAndAssetCode(String statusActive, String assetCode);

	static final String roomInventoryDetails = "SELECT AII.asset_id, AII.asset_code, AII.asset_name, HM.hostel_name,"
			+ " HR.room_no, AII.asset_condition_status, HM.hostel_id, HR.room_id, HRI.inventory_id, HRI.asset_condition"
			+ " FROM schooldev.\"ASSET_INVENTORY_INFO\" AII"
			+ " JOIN schooldev.\"HOSTEL_ROOM_INVENTORY\" HRI"
			+ " ON AII.asset_id = HRI.item_id::int8 AND AII.active_flag = HRI.active_flag"
			+ " JOIN schooldev.\"HOSTEL_ROOM_INFO\" HR on HR.room_id = HRI.room_id"
			+ " JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" HFM ON HFM.floor_id = HR.building_id"
			+ " JOIN schooldev.\"HOSTEL_MASTER\" HM ON HM.hostel_id = HFM.hostel_id"
			+ " WHERE AII.active_flag = :statusActive";
	
	static final String roomInventorySearch = " AND(HM.hostel_name ILIKE CONCAT('%', :search, '%') "
			+ " OR AII.asset_name ILIKE CONCAT('%', :search, '%')"
			+ " OR AII.asset_code ILIKE CONCAT('%', :search, '%') "
			+ " OR HR.room_no ILIKE CONCAT('%', :search, '%') "
			+ " OR AII.asset_condition_status ILIKE CONCAT('%', :search, '%'))";

	@Query(value = roomInventoryDetails + " AND HM.hostel_id = :hostelId", nativeQuery = true)
	List<Object[]> getAllRoomInventoryList(String statusActive, Long hostelId);
	
	@Query(value = roomInventoryDetails + " AND HM.hostel_id = :hostelId AND"
			+ "  LOWER(AII.asset_condition_status) = :assetCondition", nativeQuery = true)
	List<Object[]> getRoomInventoryList(String statusActive, Long hostelId, String assetCondition);

	@Query(value = roomInventoryDetails + " AND AII.asset_id = :id", nativeQuery = true)
	List<Object[]> getRoomInventoryDetailsByAssetId(String statusActive, Long id);

	AssetInventoryInfoEntity findByAssetIdAndActiveFlag(Long id, String statusActive);

	boolean existsByActiveFlagAndAssetCategoryId(String statusActive, Long id);

	@Query(value = roomInventoryDetails + " AND HM.hostel_id = :hostelId AND"
			+ "  LOWER(AII.asset_condition_status) = :assetCondition " + roomInventorySearch, nativeQuery = true)
	Page<Object[]> getRoomInventoryList(String statusActive, long hostelId, String assetCondition, String search, Pageable pageable);

	@Query(value = roomInventoryDetails + " AND HM.hostel_id = :hostelId " + roomInventorySearch, nativeQuery = true)
	Page<Object[]> getAllRoomInventoryListSearch(String statusActive, long hostelId, String search, Pageable pageable);

	@Query(value = roomInventoryDetails
			+ " AND HM.hostel_id = :hostelId"
			+ " AND (LOWER(AII.asset_condition_status) = 'to be replaced' OR LOWER(AII.asset_condition_status) = 'to be repaired')"
			+ roomInventorySearch, nativeQuery = true)
	Page<Object[]> getRoomInventoryReplacedList(String statusActive, Long hostelId, String search, Pageable pageable);
}