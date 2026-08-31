package com.iitm.hosteldine.dto.hostel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Integer;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.asset.AssetCategoryInfoEntity;
import java.time.LocalDate;

@Data
public class HostelRoomInventoryDto {
    @JsonProperty("inventoryId")
    private Integer inventoryId;
    @JsonProperty("room")
    private HostelRoomInfoEntity room;
    @JsonProperty("itemId")
    private String itemId;
    @JsonProperty("category")
    private AssetCategoryInfoEntity category;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("assetCondition")
    private String assetCondition;
    @JsonProperty("assetConditionDate")
    private LocalDate assetConditionDate;
    @JsonProperty("assetCode")
    private String assetCode;
}