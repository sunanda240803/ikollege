package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleDto {

    private Long roleId;
    private String roleName;
    private boolean checked;

    @Override
    public String toString() {
        return "Role(" + roleId + ", " + roleName + ')';
    }
}
