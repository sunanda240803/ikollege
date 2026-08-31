package com.iitm.hosteldine.controller.hostel;

import java.util.Objects;

import com.iitm.hosteldine.config.DynamicSecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.PrincipalDashBoardDTO;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.TabPrivilegeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.hostel.tab-privilege}")
@RequiredArgsConstructor
public class TabPrivilegeController {

    private final CommonResponseUtil commonResponseUtil;
    private final TabPrivilegeService menuService;
    private final DynamicSecurityService dynamicSecurityService;

    @Value("${url.hostel.tab-privilege}")
    private String baseUrl;

    @GetMapping
    public String getTabPrivilege(PaginationForm form, ModelMap model, HttpServletRequest request) {
    	if(!Objects.requireNonNull(SecurityCtxUtil.userRole()).equals("SoftwareAdmin")) {
    		model.addAttribute("roleList", null);
    		PrincipalDashBoardDTO principalDashBoardDTO =  menuService.getAdminTabPrivileges(Objects.requireNonNull(SecurityCtxUtil.userName()));
            model.addAttribute("tabPrevilegeList", principalDashBoardDTO.getTabPrevilegeList());
            model.addAttribute("role", Objects.requireNonNull(SecurityCtxUtil.userRole()));
    	}
    	model.addAttribute("roleList", menuService.getRoles());
    	model.addAttribute("roleId", Objects.requireNonNull(SecurityCtxUtil.userRole()));
        commonResponseUtil.updateCommonModelAttributes(model, request, null, form);
        return HTMLPage.TAB_PRIVILEGE_LIST;
    }

    @GetMapping("/{role}")
    public String getMenuList(@PathVariable("role") String role, PaginationForm form, ModelMap model, HttpServletRequest request) {
        if(role != null) {
            PrincipalDashBoardDTO principalDashBoardDTO =  menuService.getAdminTabPrivileges(role);
            model.addAttribute("tabPrevilegeList", principalDashBoardDTO.getTabPrevilegeList());
            model.addAttribute("role", role);
        }
        model.addAttribute("roleId", Objects.requireNonNull(SecurityCtxUtil.userRole()));
        model.addAttribute("roleList", menuService.getRoles());
        commonResponseUtil.updateCommonModelAttributes(model, request, null, form);
        return HTMLPage.TAB_PRIVILEGE_LIST;
    }

    @PostMapping
    public String saveAdminTabPrivileges(@RequestBody  PrincipalDashBoardDTO principalDashBoardDTO,
                                         RedirectAttributes redirectAttributes) {
    	String status = null;
    	if(!Objects.requireNonNull(SecurityCtxUtil.userRole()).equals("SoftwareAdmin")) {
    		status = menuService.saveUserTabPrivileges(principalDashBoardDTO);
    	}else {    		
    		status = menuService.saveAdminTabPrivileges(principalDashBoardDTO);
    	}
        String saveStatus = status.equalsIgnoreCase(Constants.SAVED) ? "message.guest.coupon.config.save" :
                "message.guest.coupon.config.update";
        dynamicSecurityService.refreshPermissions();
        commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttributes);

        // Redirect after successful save/update
        return "redirect:" + baseUrl+"/"+principalDashBoardDTO.getRole();

    }
}
