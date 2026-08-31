package com.iitm.hosteldine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.model.MenuListEntity;

import lombok.Data;

@Data
public class UserRolesDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("role")
    private RoleEntity role;
    @JsonProperty("roleDescription")
    private String roleDescription;
    @JsonProperty("menu")
    private MenuListEntity menu;
}