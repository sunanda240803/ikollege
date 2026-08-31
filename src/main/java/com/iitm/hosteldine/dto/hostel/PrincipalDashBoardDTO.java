package com.iitm.hosteldine.dto.hostel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
public class PrincipalDashBoardDTO {
    private String role;
    private List<TabDTO> tabPrevilegeList;
    private List<String> checkedTabList;
}

