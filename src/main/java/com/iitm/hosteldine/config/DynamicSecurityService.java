package com.iitm.hosteldine.config;

import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.collegeInfo.SchoolGeographyInfoDto;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.adminSettings.RoleMenuPrivilegeService;
import com.iitm.hosteldine.service.collegeInfo.SchoolGeographyInfoService;
import com.iitm.hosteldine.service.collegeInfo.SimsConfigService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Service
@RequiredArgsConstructor
public class DynamicSecurityService {

    private final RoleMenuPrivilegeService roleMenuPrivilegeService;
    private final SimsConfigDataService simsConfigDataService;
    private final SchoolGeographyInfoService schoolGeographyInfoService;
    private SchoolGeographyInfoDto schoolGeographyInfoDto;

    private Map<String, List<MenuListDto>> urlRoleMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadPermissions() {
        refreshPermissions();
    }

    @PostConstruct
    public void loadCollegeInfo() {
        schoolGeographyInfoDto = schoolGeographyInfoService.getSchoolDetails();
    }

    public synchronized void refreshPermissions() {
        Map<String, List<MenuListDto>> newMap = new ConcurrentHashMap<>();

        List<Object[]> permissions = roleMenuPrivilegeService.getAllRoleMenuMapping();
        for (Object[] perm : permissions) {
            String role = "ROLE_" + perm[0];
            MenuListDto menu = (MenuListDto) perm[1];
            if (!newMap.containsKey(role)) {
                newMap.put(role, new ArrayList<>());
            }
            newMap.get(role).add(menu);
        }

        urlRoleMap = newMap;
        DynamicAuthorizationManager.enableRolePrivilege = simsConfigDataService.getSimConfigValue("ENABLE_ROLE_PRIVILEGE", "false");
    }

    public List<MenuListDto> getMenusForRole(Authentication auth, String menuType) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(urlRoleMap::containsKey)
                .flatMap(role -> {
                    if (menuType == null) return urlRoleMap.get(role).stream();
                    else return urlRoleMap.get(role).stream().filter(it -> it.getMenuType().equals(menuType)).toList().stream();
                }).distinct().toList();
    }

    public MenuListDto getMenuForRoleByUrl(Authentication auth, String menuUrl) {
        MenuListDto menuListDto = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(urlRoleMap::containsKey)
                .flatMap(role -> urlRoleMap.get(role).stream())
                .filter(menu -> menu.getUrlPath().equals(menuUrl))
                .findFirst().orElse(null);
        if (menuListDto == null) {
            String finalMenuUrl1 = menuUrl.substring(1);
            List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(urlRoleMap::containsKey).toList();
            List<MenuListDto> menuList = roles.stream().flatMap(role -> urlRoleMap.get(role).stream()).toList();
            List<MenuListDto> tabsList = menuList.stream().filter(menu -> menu.getSubTab() != null && !menu.getSubTab().isEmpty())
                    .flatMap(menu -> menu.getSubTab().stream()).toList();
            List<MenuListDto> tabsList2 = tabsList.stream().filter(menu -> menu.getUrlPath().equals(finalMenuUrl1)).toList();
            Optional<MenuListDto> menuT = tabsList2.stream().findFirst();
            menuListDto = menuT.orElse(null);

        }
        return menuListDto;
    }
}
