package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CountryDto {
    @JsonProperty("countryId")
    private int countryId;
    @JsonProperty("countryName")
    private String countryName;
}