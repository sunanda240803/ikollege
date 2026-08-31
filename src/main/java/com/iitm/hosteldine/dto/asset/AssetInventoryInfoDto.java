package com.iitm.hosteldine.dto.asset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.lang.Integer;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetInventoryInfoDto {
    @JsonProperty("assetId")
    private Long assetId;
    @JsonProperty("assetCategoryId")
    private Integer assetCategoryId;
    @JsonProperty("assetName")
    private String assetName;
    @JsonProperty("assetOwnershipStatus")
    private String assetOwnershipStatus;
    @JsonProperty("assetVehicleNumber")
    private String assetVehicleNumber;
    @JsonProperty("assetVehicleRcNumber")
    private String assetVehicleRcNumber;
    @JsonProperty("assetVehicleRegNumber")
    private String assetVehicleRegNumber;
    @JsonProperty("seatingCapacity")
    private Integer seatingCapacity;
    @JsonProperty("assetQuantity")
    private Integer assetQuantity;
    @JsonProperty("assetPrice")
    private BigDecimal assetPrice;
    @JsonProperty("assetDiscount")
    private BigDecimal assetDiscount;
    @JsonProperty("assetTax")
    private BigDecimal assetTax;
    @JsonProperty("assetNetAmount")
    private BigDecimal assetNetAmount;
    @JsonProperty("assetLocation")
    private String assetLocation;
    @JsonProperty("assetInUseStatusFlag")
    private String assetInUseStatusFlag;
    @JsonProperty("assetBillNo")
    private String assetBillNo;
    @JsonProperty("assetPurchaseDate")
    private LocalDate assetPurchaseDate;
    @JsonProperty("assetWarrantyDate")
    private LocalDate assetWarrantyDate;
    @JsonProperty("assetDescription")
    private String assetDescription;
    @JsonProperty("assetSupplierName")
    private String assetSupplierName;
    @JsonProperty("assetSupplierAddress")
    private String assetSupplierAddress;
    @JsonProperty("assetSupplierLandNo")
    private Long assetSupplierLandNo;
    @JsonProperty("assetSupplierMobileNo")
    private Long assetSupplierMobileNo;
    @JsonProperty("assetConditionStatus")
    private String assetConditionStatus;
    @JsonProperty("assetConditionRemarks")
    private String assetConditionRemarks;
    @JsonProperty("assetTransportStatus")
    private String assetTransportStatus;
    @JsonProperty("assetLenderId")
    private String assetLenderId;
    @JsonProperty("assetLenderType")
    private String assetLenderType;
    @JsonProperty("assetVehicleChassisNumber")
    private String assetVehicleChassisNumber;
    @JsonProperty("assetVehicleMake")
    private String assetVehicleMake;
    @JsonProperty("assetVehicleModel")
    private String assetVehicleModel;
    @JsonProperty("assetVehiclePermit")
    private String assetVehiclePermit;
    @JsonProperty("assetFc")
    private LocalDate assetFc;
    @JsonProperty("assetRoadTax")
    private LocalDate assetRoadTax;
    @JsonProperty("assetInsurance")
    private LocalDate assetInsurance;
    @JsonProperty("assetPollution")
    private LocalDate assetPollution;
    @JsonProperty("assetService")
    private LocalDate assetService;
    @JsonProperty("assetConditionDate")
    private LocalDate assetConditionDate;
    @JsonProperty("assetCondition")
    private String assetCondition;
    @JsonProperty("assetCode")
    private String assetCode;
}