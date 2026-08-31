package com.iitm.hosteldine.service.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.DashboardWidgetMasterDto;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.mapper.DashboardWidgetMasterMapper;
import com.iitm.hosteldine.model.DashboardWidgetPrivilegeEntity;
import com.iitm.hosteldine.repository.DashboardWidgetPrivilegeRepository;
import com.iitm.hosteldine.repository.UserRolesRepository;
import com.iitm.hosteldine.repository.collegeInfo.RoleMenuPrivilegeRepository;
import com.iitm.hosteldine.service.MenuService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final MenuService menuService;

	public ArrayList<MenuListDto> getDashboardList() throws Exception {
		ArrayList<MenuListDto> list = menuService.getMenuList("dashboard");
		if (list != null) {
			for (MenuListDto menuListDto : list) {
				Long menuOrder = menuListDto.getMenuOrder().longValue();
				menuListDto.setSubMenu(menuService.getSubDashboardById(menuOrder));
			}
		}
		return list;
	}
}
