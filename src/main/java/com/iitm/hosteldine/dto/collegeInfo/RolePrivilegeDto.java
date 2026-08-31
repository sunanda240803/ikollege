package com.iitm.hosteldine.dto.collegeInfo;

import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePrivilegeDto {
    private long id;
    private RoleDto role;
    private MenuListDto menu;
    private Boolean checked;
    private String menuId;
    private String activeFlag;
    private List<RolePrivilegeDto> submenu;


    public int getSubmenuChecked() {
        int cnt = 0;
        if (submenu != null && !submenu.isEmpty()) {
            for (RolePrivilegeDto dto : submenu) {
                if (dto.checked!=null && dto.checked) cnt++;
            }
        }
        return cnt;
    }

    @Override
    public String toString() {
        return "RPDto{" + role + " | " + menu + " | " + activeFlag + " | " + id + '}';
    }
}