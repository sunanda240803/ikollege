package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessCouponUserMappingDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.UserManagementService;
import com.iitm.hosteldine.service.mess.MessCouponUserMappingService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "${url.mess.coupon.mapping}")
@RequiredArgsConstructor
public class MessCouponUserMappingController {

    private final MessCouponUserMappingService messCouponUserMappingService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessMasterService messMasterService;
    private final UserManagementService userManagementService;

    @Value("${url.mess.coupon.mapping}")
    private String baseUrl;

    @GetMapping
    public String getMessCouponUserMapping(HttpServletRequest request, PaginationForm form, ModelMap modelMap) {

        Page<MessCouponUserMappingDto> allMessCouponUserMappings = messCouponUserMappingService.getAllMessCouponUserMappings(form);
        commonResponseUtil.updateCommonModelAttributes(modelMap, request, allMessCouponUserMappings, form);
        List<MessMasterDto> messMasterList = messMasterService.getMessMasterList();
        modelMap.addAttribute("messMasterList", messMasterList);

        MessCouponUserMappingDto messCouponUserMappingDto = commonResponseUtil.handleModalFormError(request, modelMap, "messCouponUserMappingDto",
                MessCouponUserMappingDto.class);
        modelMap.addAttribute("messCouponUserMappingDto", messCouponUserMappingDto);
        modelMap.addAttribute("userList", userManagementService.getUserList());

        Optional.ofNullable(commonResponseUtil.getRedirectedValues(request, "exists", Boolean.class))
                .ifPresent(exists -> modelMap.addAttribute("exists", true));

        return HTMLPage.MESS_COUPON_USER_MAPPING;
    }

    @PostMapping
    public String saveMessCouponUserMapping(@ModelAttribute MessCouponUserMappingDto messCouponUserMappingDto,
                                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        messCouponUserMappingService.validateMessCouponUserMapping(messCouponUserMappingDto, bindingResult);
        if (bindingResult.hasErrors()) {
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, messCouponUserMappingDto);
        } else {
            Map<String, Boolean> status = messCouponUserMappingService.saveMessCouponUserMapping(messCouponUserMappingDto);

            status.forEach((key, value) -> {
                if (key.equals(Constants.SAVED)) {
                    String message = "message.mess.coupon.user.mapping.save";
                    commonResponseUtil.updateSaveResponseByStatus(key, redirectAttributes, message);
                }

                if (value) {
                    redirectAttributes.addFlashAttribute("exists", true);
                }
            });
        }

        return Constants.REDIRECT + baseUrl;
    }

    @DeleteMapping(value = "${id}")
    public @ResponseBody BaseResponse deleteCouponUserMapping(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(messCouponUserMappingService.deleteMessCouponUserMapping(id));
        } catch (RecordNotExistsException e) {
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }
}