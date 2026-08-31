package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dashboard.student.HostelNightPaymentTransactionDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.form.hostel.HostelNightCouponForm;
import com.iitm.hosteldine.repository.dashboard.student.HostelNightPaymentTransactionRepository;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.HostelNightPaymentTransactionService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(value = "${url.hostel.night.coupon}")
@RequiredArgsConstructor
public class HostelNightCouponController {

    private final SimsConfigDataService simsConfigDataService;
    private final HostelMasterService hostelMasterService;
    private final ShowStudentDetailRepository showStudentDetailRepository;
    private final HostelNightPaymentTransactionRepository hostelNightPaymentTransactionRepository;
    private final HostelNightPaymentTransactionService hostelNightPaymentTransactionService;
    private final CommonResponseUtil commonResponseUtil;

    @Value("${url.hostel.night.coupon}")
    private String baseUrl;

    @GetMapping("${url.widget}")
    String getHostelNightCouponWidget(ModelMap model) {
        String status = Optional.ofNullable(hostelNightPaymentTransactionService.getByDateBetween())
                .map(HostelNightPaymentTransactionDto::getPaymentStatus)
                .orElse(Constants.HYPHEN);
        model.put("status", status);
        return HTMLPage.HOSTEL_NIGHT_COUPON_WIDGET;
    }

    @GetMapping
    String getHostelNightCoupon(ModelMap model) {
        addModelAttributes(model);
        model.addAttribute("hostelNightCouponForm", HostelNightCouponForm.builder().build());
        return HTMLPage.HOSTEL_NIGHT_COUPON_FORM;
    }

    @PostMapping
    String saveHostelNightCoupon(@ModelAttribute HostelNightCouponForm hostelNightCouponForm, BindingResult bindingResult,
                                 HttpServletRequest request, ModelMap model, RedirectAttributes redirectAttributes) {
        hostelNightPaymentTransactionService.validateNightCouponForm(hostelNightCouponForm, bindingResult);
        if (bindingResult.hasErrors()) {
            addModelAttributes(model);
            return HTMLPage.HOSTEL_NIGHT_COUPON_FORM;
        }
        HostelNightPaymentTransactionDto hostelNightPaymentTransactionDto = hostelNightPaymentTransactionService.processHostelNightCoupon(hostelNightCouponForm, request);
        String saveStatus;
        if(Objects.nonNull(hostelNightPaymentTransactionDto)){
            saveStatus = Constants.SAVED;
        }
        else{
            saveStatus = null;
        }

        if (Objects.isNull(hostelNightCouponForm.getIsLedger()) || !hostelNightCouponForm.getIsLedger()) {
            try {
                PaymentGatewayCcavenueDto pgDto = hostelNightPaymentTransactionService.initiateTransaction(hostelNightPaymentTransactionDto,request);
                // Pass encrypted data and access code to the Thymeleaf template
                model.addAttribute("pgDto", pgDto);
                return HTMLPage.CCAVENUE_HTML;
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttributes, "message.night.coupon.purchase");
        return Constants.REDIRECT + baseUrl;
    }

    @PostMapping("${url.payment.response}")
    public String savePaymentResponse(@RequestParam(value = "responseType", required = false) String responseType, ModelMap model, HttpServletRequest request) throws Exception {

        System.out.println("responseType------"+responseType);
        PaymentGatewayCcavenueDto pgDto = hostelNightPaymentTransactionService.savePaymentResponse(responseType,request);

        if(responseType!=null & responseType.equals(Constants.CANCEL)) {
            return Constants.REDIRECT + baseUrl;
        }

        model.addAttribute("paymentGatewayDto", pgDto);
        model.addAttribute("backToList", baseUrl);
        return HTMLPage.CCAVENUE_RESPONSE_HTML;
    }

    private void addModelAttributes(ModelMap model) {
        String maxCoupon = simsConfigDataService.getSimConfigValue(SimsConfigDataService.HOSTEL_NIGHT_MAX_COUPON);
        HostelMasterDto hostelDetailsById = hostelMasterService.getHostelDetailsById(SecurityCtxUtil.hostelId());
        Double ledgerBalance = Objects.nonNull(showStudentDetailRepository.checkStudentBalance(SecurityCtxUtil.userId())) ?
                showStudentDetailRepository.checkStudentBalance(SecurityCtxUtil.userId()) : 0.0;
        Integer totalNumberOfCoupons = hostelNightPaymentTransactionRepository.getTotalNumberOfCoupons(SecurityCtxUtil.userId(),
                        WorkflowStatus.SUCCESS.getStatus(), ModelConstants.STATUS_ACTIVE)
                .orElse(0);
        List<HostelNightPaymentTransactionDto> hostelNightPaymentTransactions = hostelNightPaymentTransactionService.getHostelNightPaymentTransactions();
        model.addAttribute("hostel", hostelDetailsById);
        model.addAttribute("ledgerBalance", ledgerBalance);
        model.addAttribute("maxCoupon", maxCoupon);
        model.addAttribute("totalNumberOfCoupons", totalNumberOfCoupons);
        model.addAttribute("hostelNightPaymentTransactions", hostelNightPaymentTransactions);
    }

}