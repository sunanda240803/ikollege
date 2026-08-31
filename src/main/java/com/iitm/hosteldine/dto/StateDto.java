package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StateDto {
    @JsonProperty("stateId")
    private int stateId;
    @JsonProperty("stateName")
    private String stateName;
    @JsonProperty("countryId")
    private int countryId;
}