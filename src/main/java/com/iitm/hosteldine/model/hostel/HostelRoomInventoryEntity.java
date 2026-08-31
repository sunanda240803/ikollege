package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;
import com.iitm.hosteldine.model.asset.AssetCategoryInfoEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_ROOM_INVENTORY\"", schema = ModelConstants.SCHEMA)
public class HostelRoomInventoryEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inventory_id", nullable = false)
	private Long inventoryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private HostelRoomInfoEntity room;

	@Column(name = "item_id", length = 16, nullable = false)
	private String itemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private AssetCategoryInfoEntity category;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "asset_condition", length = 64)
	private String assetCondition;

	@Column(name = "asset_condition_date")
	private LocalDate assetConditionDate;

	@Column(name = "asset_code", length = 32)
	private String assetCode;
}
