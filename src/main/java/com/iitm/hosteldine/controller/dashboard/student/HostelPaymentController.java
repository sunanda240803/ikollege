package com.iitm.hosteldine.controller.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.HostelEnrollmentConfigurationDto;
import com.iitm.hosteldine.dto.student.StudentHostelPaymentDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.HostelPaymentService;
import com.iitm.hosteldine.service.hostel.HostelEnrollmentConfigService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "${url.hostel.payment}")
@RequiredArgsConstructor
public class HostelPaymentController {

    private final CommonResponseUtil commonResponseUtil;
    private final HostelPaymentService hostelPaymentService;
    private final HostelEnrollmentConfigService hostelEnrollmentConfigService;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.hostel.payment}")
    private String baseUrl;

    @GetMapping("${url.widget}")
    public String getHostelPaymentWidget(ModelMap model) {
        StudentHostelPaymentDto studentHostelPayments = hostelPaymentService.getRecentStudentHostelPayment();
        model.addAttribute("studentHostelPayments", studentHostelPayments);
        return HTMLPage.HOSTEL_PAYMENT_WIDGET;
    }

    @GetMapping
    public String getHostelPayments(HttpServletRequest request, ModelMap model) {
        List<StudentHostelPaymentDto> studentHostelPayments = hostelPaymentService.getStudentHostelPayments();
        commonResponseUtil.updateCommonModelAttributes(model, request, null, null);
        String ccwOfficeNo = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CCW_OFFICE_NO);
        model.addAttribute("officeNo", ccwOfficeNo);
        model.addAttribute("studentHostelPayments", studentHostelPayments);
        return HTMLPage.VIEW_HOSTEL_PAYMENTS;
    }

    @GetMapping(value = "${id}")
    public String getHostelPaymentById(ModelMap model,HttpServletRequest request) {
        StudentHostelPaymentDto hostelPaymentDto = commonResponseUtil.handleModalFormError(request,model,"hostelPaymentDto", StudentHostelPaymentDto.class);
        HostelEnrollmentConfigurationDto enrollmentConfigurationDto = hostelEnrollmentConfigService.getByActiveFlag();
        model.addAttribute("hostelPaymentDto", hostelPaymentDto);
        model.addAttribute("enrollment", enrollmentConfigurationDto);
        return HTMLPage.HOSTEL_PAYMENTS_MODAL;
    }

    @PostMapping
    public String saveHostelPayment(@ModelAttribute StudentHostelPaymentDto hostelPaymentDto, BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        hostelPaymentService.validateStudentHostelPaymentDto(hostelPaymentDto, bindingResult);
        if(bindingResult.hasErrors()) {
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult,hostelPaymentDto);
            return Constants.REDIRECT + baseUrl;
        }

        String status = hostelPaymentService.saveHostelPayment(hostelPaymentDto);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, "message.hostel.payment.success");
        return Constants.REDIRECT + baseUrl;
    }

    @DeleteMapping(value = "${id}")
    public @ResponseBody BaseResponse deleteHostelPayment(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(hostelPaymentService.deleteHostelPayment(id));
        } catch (RecordNotExistsException e) {
            // Create a custom error response in case of an exception
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus(Constants.FAILURE);
            return errorResponse;
        }
    }

    @GetMapping("${url.exist}" + "${id}")
    public @ResponseBody Boolean checkDuNumberExists(@PathVariable("id") String duNumber) {
        return hostelPaymentService.checkDuNumberExists(duNumber);
    }
}
