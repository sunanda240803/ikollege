package com.iitm.hosteldine.form.asset;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssetCategoryForm {
	private Long id;
	private String categoryType;
	private String categoryName;
	private LocalDateTime modifiedAt;
	private Double minorRepairCost;
	private Double majorRepairCost;
	private Double replacementCost;
	private String assetCategoryDescription;
	private String assetCategoryShortcode;
	private String minorCostString;
	private String majorCostString;
	private String replacementCostString;

	public AssetCategoryForm() {
	}
	
	public AssetCategoryForm(Long id, String categoryName, String assetCategoryShortcode) {
		this.id = id;
		this.categoryName = categoryName;
		this.assetCategoryShortcode = assetCategoryShortcode;
	}
}
