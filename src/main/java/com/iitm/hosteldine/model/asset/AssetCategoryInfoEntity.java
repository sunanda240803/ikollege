package com.iitm.hosteldine.model.asset;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"ASSET_CATEGORY_INFO\"", schema = ModelConstants.SCHEMA)
public class AssetCategoryInfoEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "asset_category_id", nullable = false)
	private Long assetCategoryId;

	@Column(name = "asset_category", nullable = false, length = 64)
	private String assetCategory;
	
	@Column(name = "minor_repair_cost")
	private Double minorRepairCost;
	
	@Column(name = "major_repair_cost")
	private Double majorRepairCost;
	
	@Column(name = "replacement_cost")
	private Double replacementCost;
	
	@Column(name = "asset_category_description")
	private String assetCategoryDescription;
	
	@Column(name = "asset_category_shortcode", length = 4)
	private String assetCategoryShortcode;
	
}
