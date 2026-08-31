package com.iitm.hosteldine.form.collegeInfo;

import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.dto.collegeInfo.RolePrivilegeDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoleMenuPrivilegeForm {
    private List<RoleDto> roleList;
    private Long roleSelected;
    private String newRoleName;
    private RolePrivilegeDto menu;
    private ArrayList<RolePrivilegeDto> menuList;
}
