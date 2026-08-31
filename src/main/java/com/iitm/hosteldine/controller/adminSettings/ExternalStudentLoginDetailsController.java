package com.iitm.hosteldine.controller.adminSettings;

import com.iitm.hosteldine.dto.UserManagementOnlineDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.adminSettings.ExtraStudentLoginDetailsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "${url.external.login.student}")
@RequiredArgsConstructor
public class ExternalStudentLoginDetailsController {

    private final ExtraStudentLoginDetailsService extraStudentLoginDetailsService;
    private final CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String getExternalStudentLoginDetails(PaginationForm paginationForm, ModelMap model, HttpServletRequest request) {
        Page<UserManagementOnlineDto> extraStudentLoginDetails = extraStudentLoginDetailsService.getExtraStudentLoginDetails(paginationForm);
        commonResponseUtil.updateCommonModelAttributes(model, request,extraStudentLoginDetails,paginationForm);
        return HTMLPage.EXTRA_STUDENT_LOGIN_DETAILS;
    }

    @PostMapping(value = "${id}")
    public ResponseEntity<Boolean> activateBlockedUser(@PathVariable("id") String id){
        return ResponseEntity.ok(extraStudentLoginDetailsService.activeBlockedUser(id));
    }
}
