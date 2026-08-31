package com.iitm.hosteldine.controller.adminSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.config.DynamicSecurityService;
import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.dto.collegeInfo.RolePrivilegeDto;
import com.iitm.hosteldine.exception.InternalErrorException;
import com.iitm.hosteldine.form.collegeInfo.RoleMenuPrivilegeForm;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.adminSettings.RoleMenuPrivilegeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.role.menu.privileges}")
@RequiredArgsConstructor
public class RoleMenuPrivilegesController {
    private final CommonResponseUtil commonResponseUtil;
    private final CommonService commonService;
    private final RoleMenuPrivilegeService roleMenuPrivilegeService;
    private final DynamicSecurityService dynamicSecurityService;

    @Value("${url.role.menu.privileges}")
    private String baseUrl;

    @GetMapping
    public String getRoleMenuList(ModelMap map, HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        RoleMenuPrivilegeForm roleMenuPrivilegeForm = flashInputMap != null && flashInputMap.get("roleMenuPrivilegeForm") != null
                ? ((RoleMenuPrivilegeForm) flashInputMap.get("roleMenuPrivilegeForm")) : new RoleMenuPrivilegeForm();
        roleMenuPrivilegeForm.setRoleList(commonService.getRoleslist());
        if (roleMenuPrivilegeForm.getRoleSelected() != null) {
            Long selectedRole = roleMenuPrivilegeForm.getRoleSelected();
//            if (selectedRole > 0L) {
                Optional<RoleDto> role = roleMenuPrivilegeForm.getRoleList().stream()
                        .filter(f -> selectedRole.equals(f.getRoleId())).findFirst();
                RoleDto roleMasterDto;
                if (role.isPresent()) {
                    roleMasterDto = role.get();
                } else {
                    roleMasterDto = new RoleDto();
                    roleMasterDto.setRoleName(roleMenuPrivilegeForm.getNewRoleName());
                }
                List<RolePrivilegeDto> rolePrivilegeDto = roleMenuPrivilegeService.getRolePrivilegeListByRole(selectedRole);

                if (rolePrivilegeDto != null) {
                    RolePrivilegeDto dto = new RolePrivilegeDto();
                    ArrayList<RolePrivilegeDto> resultMenuList = new ArrayList<>();
                    resultMenuList.add(new RolePrivilegeDto());
                    resultMenuList.add(new RolePrivilegeDto());
                    resultMenuList.add(new RolePrivilegeDto());
                    for (int i = 0; i < rolePrivilegeDto.size(); i++) {
                        RolePrivilegeDto menu = rolePrivilegeDto.get(i);
                        RolePrivilegeDto parentMenu = resultMenuList.get(i % 3);
                        if (parentMenu.getSubmenu() == null) {
                            parentMenu.setRole(menu.getRole());
                            parentMenu.setMenu(menu.getMenu());
                            parentMenu.setSubmenu(new ArrayList<>());
                        }
                        parentMenu.getSubmenu().add(menu);
                    }
                    dto.setRole(roleMasterDto);
                    dto.setSubmenu(resultMenuList);
                    map.addAttribute("root", dto);
                }
//            }
        }
        map.addAttribute("roleMenuPrivilegeForm", roleMenuPrivilegeForm);
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.ROLE_MENU_PRIVILEGES;
    }

    @PostMapping(value = "/role")
    public String getMenuPrivileges(@ModelAttribute RoleMenuPrivilegeForm roleMenuPrivilegeForm, RedirectAttributes redirectAttrs) {
        if (!roleMenuPrivilegeForm.getRoleSelected().equals(-1L)) {
            roleMenuPrivilegeForm.setNewRoleName(null);
        } else {
            String newRole = roleMenuPrivilegeForm.getNewRoleName();
            newRole = Utility.capitalizeEachWordStream(newRole);
            if (newRole.length() > 25) {
                newRole = newRole.substring(0, 25);
            }
            roleMenuPrivilegeForm.setNewRoleName(newRole);

        }
        redirectAttrs.addFlashAttribute("roleMenuPrivilegeForm", roleMenuPrivilegeForm);
        return "redirect:" + baseUrl;
    }

    @PostMapping()
    public String updatePrivilege(@ModelAttribute RolePrivilegeDto root, RedirectAttributes redirectAttrs) throws InternalErrorException {
        ArrayList<RolePrivilegeDto> menuList = new ArrayList<>();
        ArrayList<Long> menuIds = new ArrayList<>();
        for (RolePrivilegeDto parentMenu : root.getSubmenu()) {//column level
            if (parentMenu.getSubmenu() != null) {
                for (RolePrivilegeDto rowMenu : parentMenu.getSubmenu()) {//row level
                    if (rowMenu.getSubmenu() != null) {
                        boolean subMenuChecked = false;
                        int menuIndex = menuList.size();
                        for (RolePrivilegeDto roleMenu : rowMenu.getSubmenu()) {//child menu
                            if (roleMenu.getSubmenu() != null && !roleMenu.getSubmenu().isEmpty()) {
                                boolean subSubMenuChecked = false;
                                for (RolePrivilegeDto subRoleMenu : roleMenu.getSubmenu()) {//sub menu
                                    if (subRoleMenu.getChecked() != null && subRoleMenu.getChecked()) {
                                        if (!menuIds.contains(subRoleMenu.getMenu().getMenuId())) {
                                            menuList.add(subRoleMenu);
                                            menuIds.add(subRoleMenu.getMenu().getMenuId());
                                        }
                                        subSubMenuChecked = true;
                                    }
                                }
                                if (subSubMenuChecked) {
                                    subMenuChecked = true;
                                    roleMenu.getSubmenu().clear();
                                    if (!menuIds.contains(roleMenu.getMenu().getMenuId())) {
                                        menuList.add(menuIndex, roleMenu);
                                        menuIds.add(roleMenu.getMenu().getMenuId());
                                    }
                                }
                            } else {
                                if (roleMenu.getChecked() != null && roleMenu.getChecked()) {
                                    if (!menuIds.contains(roleMenu.getMenu().getMenuId())) {
                                        menuList.add(roleMenu);
                                        menuIds.add(roleMenu.getMenu().getMenuId());
                                    }
                                    subMenuChecked = true;
                                }
                            }
                        }
                        if (subMenuChecked) {
                            rowMenu.getSubmenu().clear();
                            if (!menuIds.contains(rowMenu.getMenu().getMenuId())) {
                                menuList.add(menuIndex, rowMenu);
                                menuIds.add(rowMenu.getMenu().getMenuId());
                            }
                        }
                    }
                }
            }
        }
        root.setSubmenu(menuList);

//        ValidationForm validationForm = rolePrivilegeValidator.validateRolePrivilegeDto(rolePrivilegeDto);
//        if (validationForm.getValidationStatus() == ValidationStatus.VALIDATION_SUCCESS) {
        String saveStatus = roleMenuPrivilegeService.updateRolePrivilege(root);
//      }
        RoleMenuPrivilegeForm form = new RoleMenuPrivilegeForm();
        if (root.getRole().getRoleId() > 0) {
            form.setRoleSelected(root.getRole().getRoleId());
        }
        String message = null;
        if (saveStatus != null && !saveStatus.equals("Error")) {
            message = "role.menu.privilege.update.successfully";
            dynamicSecurityService.refreshPermissions();
        }else{
            message = "role.menu.privilege.update.error";
            dynamicSecurityService.refreshPermissions();
        }
        redirectAttrs.addFlashAttribute("roleMenuPrivilegeForm", form);
        commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, message);
        return "redirect:" + baseUrl;
    }

    @PostMapping(value = "/delete-role/{roleId}")
    @ResponseBody
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes redirectAttrs) {
        return roleMenuPrivilegeService.deleteRole(roleId);
    }
}
