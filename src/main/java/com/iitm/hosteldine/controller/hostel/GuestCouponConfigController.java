package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.GuestCouponConfigDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.GuestCouponConfigService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.hostel.GuestCouponConfigValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "${url.hostel.guest.coupon.config}")
@RequiredArgsConstructor
public class GuestCouponConfigController {

    private final CommonResponseUtil commonResponseUtil;
    private final GuestCouponConfigService guestCouponConfigService;
    private final GuestCouponConfigValidator guestCouponConfigValidator;

    @Value("${url.hostel.guest.coupon.config}")
    private String baseUrl;

    /**
     * Fetches all guest coupon configurations with pagination.
     */
    @GetMapping
    public String getGuestCouponConfigList(PaginationForm form, ModelMap map, HttpServletRequest request) {
        Page<GuestCouponConfigDto> couponConfigList = guestCouponConfigService.getGuestCouponConfigList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request, couponConfigList, form);
        return HTMLPage.GUEST_COUPON_CONFIG_LIST;
    }

    /**
     * Show Add/Edit Form with an existing configuration or a new one.
     */
    @GetMapping(value = {"/add/{id}", "/edit/{id}"})
    public String showAddOrEditForm(@PathVariable(value = "id", required = false) Long id, ModelMap model) {
        if (id != null && id > 0) {
            // Editing existing configuration
            GuestCouponConfigDto guestCouponConfigDto = null;
            try {
                guestCouponConfigDto = guestCouponConfigService.getGuestCouponConfigById(id);
            } catch (RecordNotExistsException e) {
                throw new RuntimeException(e);
            }
            model.addAttribute("guestCouponConfigDto", guestCouponConfigDto);
        } else {
            // Adding new configuration
            model.addAttribute("guestCouponConfigDto", new GuestCouponConfigDto());
        }
        return HTMLPage.GUEST_COUPON_CONFIG_MODAL; // Modal form for both Add/Edit
    }

    /**
     * Saves or updates a guest coupon configuration.
     */
    @PostMapping
    public String saveOrUpdateGuestCouponConfig(@Valid @ModelAttribute GuestCouponConfigDto guestCouponConfigDto,
                                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // field validation
        guestCouponConfigValidator.validate(guestCouponConfigDto, bindingResult);

        // If there are validation errors, return to the form with errors
        if (bindingResult.hasErrors()) {
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, guestCouponConfigDto);
            return "redirect:" + baseUrl; // Return to the form with error messages
        }

        // If validation passes, proceed with saving/updating the configuration
        String status = guestCouponConfigService.saveOrUpdateGuestCouponConfig(guestCouponConfigDto);
        String saveStatus = status.equalsIgnoreCase(Constants.SAVED) ? "message.guest.coupon.config.save" :
                "message.guest.coupon.config.update";
        commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttributes);

        // Redirect after successful save/update
        return "redirect:" + baseUrl;
    }

    /**
     * Deletes a guest coupon configuration by ID.
     */
    @DeleteMapping("/{id}")
    public @ResponseBody BaseResponse deleteGuestCouponConfigById(@PathVariable("id") Long id) {
        try {
            boolean updated = guestCouponConfigService.deactivateGuestCouponConfig(id);
            return CommonResponseUtil.generateDeleteResponseByStatus(updated);
        } catch (RecordNotExistsException e) {
            // Custom error response in case of an exception
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }
}
