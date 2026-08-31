package com.iitm.hosteldine.service.dashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.DashboardWidgetMasterDto;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.mapper.DashboardWidgetMasterMapper;
import com.iitm.hosteldine.model.DashboardWidgetMasterEntity;
import com.iitm.hosteldine.model.DashboardWidgetPrivilegeEntity;
import com.iitm.hosteldine.repository.DashboardWidgetPrivilegeRepository;
import com.iitm.hosteldine.repository.collegeInfo.RoleMenuPrivilegeRepository;
import com.iitm.hosteldine.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardWidgetPrivilegeService {

    private final DashboardWidgetPrivilegeRepository dashboardWidgetPrivilegeRepository;
    private final MenuService menuService;
    private final RoleMenuPrivilegeRepository roleMenuPrivilegeRepository;

    public void getWidgetListByMenu(MenuListDto menu) {
        List<DashboardWidgetMasterDto> dashboardWidgetMasterDtoList = null;
        long menuId = menu.getMenuId();
        MenuListDto menuListDto = menuService.getMenuList(menuId);
        if (menuListDto != null) {
            if (menuListDto.getMenuType().equals("subDashboard")) {
                MenuListDto dashboard = menuService.getMenuListByMainMenuId(menuListDto.getMenuOrder());
                if (dashboard != null) {
                    menuId = dashboard.getMenuId();
                }
            }
            if (menuListDto.getMenuType().equals("subDashboard")) {
                MenuListDto dashboard = menuService.getMenuListByTypeAndURL("dashboard", menuListDto.getUrlPath());
                if (dashboard != null) {
                    menuId = dashboard.getMenuId();
                }
            }
            List<DashboardWidgetMasterEntity> list = dashboardWidgetPrivilegeRepository
                            .getWidgetListByMenuId(menuId, ModelConstants.STATUS_ACTIVE);
            if (list != null && !list.isEmpty()) {
                dashboardWidgetMasterDtoList = list.stream().map(
                        DashboardWidgetMasterMapper.INSTANCE::fromDashboardWidgetMasterEntity).toList();
            }
        }
        if (dashboardWidgetMasterDtoList != null) {
            menu.getWidgetList().addAll(dashboardWidgetMasterDtoList);
        }
    }

}
