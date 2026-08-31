package com.iitm.hosteldine.dto.asset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;

@Data
public class AssetCategoryInfoDto {
    @JsonProperty("assetCategoryId")
    private Long assetCategoryId;
    @JsonProperty("assetCategory")
    private String assetCategory;
}