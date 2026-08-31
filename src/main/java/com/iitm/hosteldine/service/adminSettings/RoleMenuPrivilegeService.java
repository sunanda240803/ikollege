package com.iitm.hosteldine.service.adminSettings;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.dto.collegeInfo.RolePrivilegeDto;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.exception.InternalErrorException;
import com.iitm.hosteldine.mapper.MenuListMapper;
import com.iitm.hosteldine.mapper.RoleMapper;
import com.iitm.hosteldine.mapper.collegeInfo.RoleMenuPrivilegeMapper;
import com.iitm.hosteldine.model.MenuListEntity;
import com.iitm.hosteldine.model.collegeInfo.RoleMenuPrivilegeEntity;
import com.iitm.hosteldine.repository.RoleRepository;
import com.iitm.hosteldine.repository.collegeInfo.RoleMenuPrivilegeRepository;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.service.MenuService;
import com.iitm.hosteldine.service.dashboard.DashboardWidgetPrivilegeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleMenuPrivilegeService {
    private final RoleRepository roleRepository;

    private final RoleMenuPrivilegeRepository rolePrivilegeRepository;
    private final RoleService roleService;
    private final MenuService menuService;
    private final RoleMenuPrivilegeRepository roleMenuPrivilegeRepository;
    private final DashboardWidgetPrivilegeService dashboardWidgetPrivilegeService;
    private final DynamicUserTabRepository dynamicUserTabRepository;

    public List<RolePrivilegeDto> getRolePrivilegeListByRole(Long role) {
        RoleDto roleDto;
        if (role > 0) {
            roleDto = roleService.getRoleById(role);
        } else {
            roleDto = new RoleDto();
        }
        RolePrivilegeDto parentPrivilegeDto;
        parentPrivilegeDto = new RolePrivilegeDto();
        parentPrivilegeDto.setRole(roleDto);

        List<MenuListDto> menuListEntities = new ArrayList<>();
        List<Object[]> privilegeList = rolePrivilegeRepository.getAllMenuWithPrivilegeByRole(roleDto.getRoleId());
        privilegeList.forEach(privilegeObject -> {
            MenuListDto menu = MenuListMapper.INSTANCE.fromMenuListEntity((MenuListEntity) privilegeObject[0]);
            if (privilegeObject[1] != null) {
                menu.setRolePrivilegeDto(RoleMenuPrivilegeMapper.INSTANCE.fromRoleMenuPrivilegeEntityWithoutRole((RoleMenuPrivilegeEntity) privilegeObject[1]));
            } else {
                menu.setRolePrivilegeDto(new RolePrivilegeDto());
                menu.getRolePrivilegeDto().setActiveFlag(ModelConstants.STATUS_INACTIVE);
            }
            menu.getRolePrivilegeDto().setChecked(menu.getRolePrivilegeDto().getActiveFlag().equals(ModelConstants.STATUS_ACTIVE));
            menuListEntities.add(menu);
        });
        List<RolePrivilegeDto> rolePrivilegeList = convertMenuToRolePrivilege(menuService.hierarchySort(menuListEntities, true));
        List<RolePrivilegeDto> resultList = new ArrayList<>(rolePrivilegeList);
        rolePrivilegeList.forEach(menu -> {
            if (menu.getSubmenu() != null && !menu.getSubmenu().isEmpty()) {
                resultList.addAll(menu.getSubmenu());
            }
            menu.setSubmenu(new ArrayList<>());
            menu.getSubmenu().add(RoleMenuPrivilegeMapper.INSTANCE.excludeSubmenu(menu));
        });
        return resultList;
    }

    private List<RolePrivilegeDto> convertMenuToRolePrivilege(List<MenuListDto> menuList) {
        List<RolePrivilegeDto> rolePrivilegeList = new ArrayList<>();
        if (menuList != null && !menuList.isEmpty()) {
            menuList.forEach(menu -> {
                RolePrivilegeDto rpDto =  menu.getRolePrivilegeDto();
                rpDto.setMenu(menu);
              //  menu.setRolePrivilegeDto(null);
                if (menu.getSubMenu() != null && !menu.getSubMenu().isEmpty()) {
                    rpDto.setSubmenu(convertMenuToRolePrivilege(menu.getSubMenu()));
                }
                rolePrivilegeList.add(rpDto);

            });
        }
        return rolePrivilegeList;
    }

    @Transactional
    public String updateRolePrivilege(RolePrivilegeDto rolePrivilegeDto) throws InternalErrorException {
        String result = null;

        roleService.checkForNewRole(rolePrivilegeDto.getRole());

        Integer deleteResult = deleteRolePrivilegeByRole(rolePrivilegeDto);
        if (deleteResult >= 0) {
            if(rolePrivilegeDto.getSubmenu().size()>0) {
            ArrayList<RoleMenuPrivilegeEntity> rolePrivilegeEntities = new ArrayList<>();
            RoleEntity roleEntity = RoleMapper.INSTANCE.toRoleEntity(rolePrivilegeDto.getRole());
                for (RolePrivilegeDto privilegeDto : rolePrivilegeDto.getSubmenu()) {
                    RoleMenuPrivilegeEntity rolePrivilege;
                    Optional<RoleMenuPrivilegeEntity> existingPrivilege = Optional.empty();
                    if (roleEntity != null && privilegeDto.getMenu() != null) {
                        existingPrivilege = rolePrivilegeRepository.
                                findFirstByMenuMenuIdAndRoleRoleId(privilegeDto.getMenu().getMenuId(), roleEntity.getRoleId());
                    }
                    log.debug("privilegeDto: {}", privilegeDto);
                    log.debug("existingPrivilege: {}", existingPrivilege.isPresent());
                    if (existingPrivilege.isPresent()) {
                        rolePrivilege = existingPrivilege.get();
                        rolePrivilege.setActiveFlag(ModelConstants.STATUS_ACTIVE);
                    } else {
                        rolePrivilege = RoleMenuPrivilegeMapper.INSTANCE.toRoleMenuPrivilegeEntity(privilegeDto);
                        rolePrivilege.onCreate();
                        rolePrivilege.setRole(roleEntity);
                    }
                    rolePrivilegeEntities.add(rolePrivilege);

                }
                List<RoleMenuPrivilegeEntity> resultList = rolePrivilegeRepository.saveAllAndFlush(rolePrivilegeEntities);
                if (!resultList.isEmpty()) {
                    result = "Success";
                }
            }else{
                result="Nothing to update";
            }
        }
        if (result == null) {
//            throw new InternalErrorException("Error in updating Menu Privilege");
            result="Error";
        }
        return result;
    }

    public Integer deleteRolePrivilegeByRole(RolePrivilegeDto rolePrivilegeDto) {
        Integer result = rolePrivilegeRepository.updateAllByRoleRoleId(rolePrivilegeDto.getRole().getRoleId(),
                ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_INACTIVE);
        return result;
    }

    /** Return true if user has access to the menu. Return false if not having access.*/
    public boolean hasAccessToMenu(String urlEntry, UserManagementDto userObj) {
        Optional<List<MenuListEntity>> menuListEntity = rolePrivilegeRepository.getMenuByHeading(userObj.getId().getUserId(), urlEntry);
        boolean result = menuListEntity.isPresent() && !menuListEntity.get().isEmpty();
        return result;
    }

    public List<Object[]> getAllRoleMenuMapping() {
        return roleMenuPrivilegeRepository.getAllRoleMenuMapping().stream().map(it -> {
            Object[] data = new Object[2];
            data[0] = it[0];
            MenuListDto menu = MenuListMapper.INSTANCE.fromMenuListEntity((MenuListEntity) it[1]);
            data[1] = menu;
            if (menu != null && (menu.getMenuType().equals("dashboard") || menu.getMenuType().equals("subDashboard"))) {
                dashboardWidgetPrivilegeService.getWidgetListByMenu(menu);
                if (menu.getMenuHeading().startsWith("CCW")) {
                    List<Object[]> tabList = dynamicUserTabRepository.getTabList(data[0].toString());
                    menu.setSubTab(new ArrayList<>());
                    for (Object[] tabData: tabList) {
                        MenuListDto tab = new MenuListDto();
                        tab.setMenuHeading(String.valueOf(tabData[0]));
                        tab.setMenuId(Long.parseLong(tabData[1].toString()));
                        tab.setUrlPath(String.valueOf(tabData[3]));
                        menu.getSubTab().add(tab);
                    }
                }
            }
            return data;
        }).collect(Collectors.toList());
    }

    @Transactional
    public String deleteRole(Long role) {
        RoleDto roleDto = roleService.getRoleById(role);
        List<MenuListDto> menuListEntities = new ArrayList<>();
        List<Object[]> privilegeList = rolePrivilegeRepository.getAllMenuWithPrivilegeByRole(roleDto.getRoleId());
        privilegeList.forEach(privilegeObject -> {
            MenuListDto menu = MenuListMapper.INSTANCE.fromMenuListEntity((MenuListEntity) privilegeObject[0]);
            if (privilegeObject[1] != null) {
                menu.setRolePrivilegeDto(RoleMenuPrivilegeMapper.INSTANCE.fromRoleMenuPrivilegeEntityWithoutRole((RoleMenuPrivilegeEntity) privilegeObject[1]));
            } else {
                menu.setRolePrivilegeDto(new RolePrivilegeDto());
                menu.getRolePrivilegeDto().setActiveFlag(ModelConstants.STATUS_INACTIVE);
            }
            menu.getRolePrivilegeDto().setChecked(menu.getRolePrivilegeDto().getActiveFlag().equals(ModelConstants.STATUS_ACTIVE));
            menuListEntities.add(menu);
        });
        long activeCount = menuListEntities.stream()
                .filter(menu -> menu.getRolePrivilegeDto() != null)
                .filter(menu -> ModelConstants.STATUS_ACTIVE.equals(menu.getRolePrivilegeDto().getActiveFlag()))
                .count();
        if (activeCount > 0) {
            return "Menus has been assigned to this role.";
        } else {
            RoleEntity roleEntity = roleRepository.findByRoleIdAndActiveFlag(role, ModelConstants.STATUS_ACTIVE).orElse(null);
            if (Objects.nonNull(roleEntity)) {
                roleEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                roleEntity.setModifiedBy(SecurityCtxUtil.userId());
                roleEntity.setModifiedAt(LocalDateTime.now());
                roleRepository.save(roleEntity);
                return Constants.SAVED;
            } else {
                return "Role not found";
            }
        }
    }
}