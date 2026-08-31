package com.iitm.hosteldine.repository.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.asset.AssetCategoryInfoEntity;
import java.util.List;
import java.util.Optional;

public interface AssetCategoryInfoRepository extends JpaRepository<AssetCategoryInfoEntity, Long> {
	
	List<AssetCategoryInfoEntity> findAllByActiveFlagOrderByAssetCategory(String statusActive);

	Optional<AssetCategoryInfoEntity> findByAssetCategoryIdAndActiveFlag(Long id, String statusActive);

	Optional<AssetCategoryInfoEntity> findByActiveFlagAndAssetCategoryIgnoreCaseAndAssetCategoryIdNot(String statusActive,
			String categoryName, Long id);

	Optional<AssetCategoryInfoEntity> findByActiveFlagAndAssetCategoryShortcodeIgnoreCaseAndAssetCategoryIdNot(
			String statusActive, String assetCategoryShortcode, Long id);

	@Query("select e from AssetCategoryInfoEntity e  where e.activeFlag=:statusActive AND (e.assetCategory ILIKE CONCAT('%', :search, '%') "
			+ " OR e.assetCategoryShortcode ILIKE CONCAT('%', :search, '%')) order By e.assetCategory")
	List<AssetCategoryInfoEntity> findAllByAssetCategorySearch(String statusActive, String search);
	
	List<AssetCategoryInfoEntity> findByAssetCategoryIgnoreCaseAndActiveFlag(String assetCategory, String statusActive);
}