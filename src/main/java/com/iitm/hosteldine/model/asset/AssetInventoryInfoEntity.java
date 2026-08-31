package com.iitm.hosteldine.model.asset;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "\"ASSET_INVENTORY_INFO\"", schema = ModelConstants.SCHEMA)
public class AssetInventoryInfoEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "asset_id", nullable = false)
	private Long assetId;

	@Column(name = "asset_category_id")
	private Integer assetCategoryId;

	@Column(name = "asset_name", length = 64)
	private String assetName;

	@Column(name = "asset_ownership_status", length = 1)
	private String assetOwnershipStatus;

	@Column(name = "asset_vehicle_number", length = 64)
	private String assetVehicleNumber;

	@Column(name = "asset_vehicle_rc_number", length = 64)
	private String assetVehicleRcNumber;

	@Column(name = "asset_vehicle_reg_number", length = 64)
	private String assetVehicleRegNumber;

	@Column(name = "seating_capacity")
	private Integer seatingCapacity;

	@Column(name = "asset_quantity", nullable = false)
	private Integer assetQuantity;

	@Column(name = "asset_price", nullable = false)
	private BigDecimal assetPrice;

	@Column(name = "asset_discount")
	private BigDecimal assetDiscount;

	@Column(name = "asset_tax")
	private BigDecimal assetTax;

	@Column(name = "asset_net_amount")
	private BigDecimal assetNetAmount;

	@Column(name = "asset_location", length = 64)
	private String assetLocation;

	@Column(name = "asset_in_use_status_flag", length = 1)
	private String assetInUseStatusFlag;

	@Column(name = "asset_bill_no", length = 32)
	private String assetBillNo;

	@Column(name = "asset_purchase_date")
	private LocalDate assetPurchaseDate;

	@Column(name = "asset_warranty_date")
	private LocalDate assetWarrantyDate;

	@Column(name = "asset_description", length = 256)
	private String assetDescription;

	@Column(name = "asset_supplier_name", length = 64)
	private String assetSupplierName;

	@Column(name = "asset_supplier_address", length = 256)
	private String assetSupplierAddress;

	@Column(name = "asset_supplier_land_no")
	private Long assetSupplierLandNo;

	@Column(name = "asset_supplier_mobile_no")
	private Long assetSupplierMobileNo;

	@Column(name = "asset_condition_status", length = 64)
	private String assetConditionStatus;

	@Column(name = "asset_condition_remarks", length = 256)
	private String assetConditionRemarks;

	@Column(name = "asset_transport_status", length = 1)
	private String assetTransportStatus;

	@Column(name = "asset_lender_id", length = 16)
	private String assetLenderId;

	@Column(name = "asset_lender_type", length = 16)
	private String assetLenderType;

	@Column(name = "asset_vehicle_chassis_number", length = 64)
	private String assetVehicleChassisNumber;

	@Column(name = "asset_vehicle_make", length = 64)
	private String assetVehicleMake;

	@Column(name = "asset_vehicle_model", length = 64)
	private String assetVehicleModel;

	@Column(name = "asset_vehicle_permit", length = 64)
	private String assetVehiclePermit;

	@Column(name = "asset_fc")
	private LocalDate assetFc;

	@Column(name = "asset_road_tax")
	private LocalDate assetRoadTax;

	@Column(name = "asset_insurance")
	private LocalDate assetInsurance;

	@Column(name = "asset_pollution")
	private LocalDate assetPollution;

	@Column(name = "asset_service")
	private LocalDate assetService;

	@Column(name = "asset_condition_date")
	private LocalDate assetConditionDate;

	@Column(name = "asset_condition", length = 64)
	private String assetCondition;

	@Column(name = "asset_code", length = 32)
	private String assetCode;

}
