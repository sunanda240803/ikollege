package com.iitm.hosteldine.dto.asset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;

@Data
public class AssetMaintenanceTypeDto {
    @JsonProperty("maintenanceTypeId")
    private Long maintenanceTypeId;
    @JsonProperty("maintenanceType")
    private String maintenanceType;
}