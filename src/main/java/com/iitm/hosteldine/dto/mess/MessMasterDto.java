package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MessMasterDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("messName")
    private String messName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("capacity")
    private Integer capacity;
    @JsonProperty("activeFlag")
    private String activeFlag;
    @JsonProperty("isFoodCourt")
    private Boolean isFoodCourt;
    @JsonProperty("messAllottedCapacity")
    private Integer messAllottedCapacity;
    @JsonProperty("fromDate")
    private LocalDate fromDate;
    @JsonProperty("toDate")
    private LocalDate toDate;
    @JsonProperty("isJainFood")
    private Boolean isJainFood;
    @JsonProperty("genderOption")
    private String genderOption;
    @JsonProperty("messOrder")
    private Integer messOrder=1;
    @JsonProperty("messType")
    private String messType;
    @JsonProperty("messFloorName")
    private String messFloorName;
    @JsonProperty("messHead")
    private String messHead;
    @JsonProperty("applicableStatus")
    private Boolean applicableStatus;
    @JsonProperty("girlsThresholdCount")
    private Integer girlsThresholdCount;
    @JsonProperty("sickFoodAvail")
    private Boolean sickFoodAvail;
    @JsonProperty("onlineCoupon")
    private Boolean onlineCoupon;
    private Double foodCourtAmount;
    private List<MessMasterDto> messMasterConfigList;
    private long allocatedCount;
    private long vacancyCount;
	private boolean check;
	private String isVegNonVeg;

    public MessMasterDto(Long id, String messName) {
        this.id = id;
        this.messName = messName;
    }

    public MessMasterDto() {

    }
}