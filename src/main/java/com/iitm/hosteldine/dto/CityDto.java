package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CityDto {
    @JsonProperty("cityId")
    private int cityId;
    @JsonProperty("cityName")
    private String cityName;
    @JsonProperty("stateId")
    private int stateId;
}