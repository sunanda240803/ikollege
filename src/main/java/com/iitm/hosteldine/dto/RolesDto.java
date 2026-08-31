package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RolesDto {
    @JsonProperty("roleId")
    private long roleId;
    @JsonProperty("roleName")
    private String roleName;
}