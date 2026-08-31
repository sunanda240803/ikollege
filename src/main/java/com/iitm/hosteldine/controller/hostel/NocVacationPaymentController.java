package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.StudentChargesDto;
import com.iitm.hosteldine.dto.studentDashboard.LedgerReportDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.common.SelectForm;
import com.iitm.hosteldine.form.common.SettlementFormDTO;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.MessLedgerReportSelectForm;
import com.iitm.hosteldine.service.hostel.NocVacationPaymentService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessOpeningBalService;
import com.iitm.hosteldine.service.student.MessToCardTransferControllerService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessPriorityRegistrationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "${url.noc.vacation.payment.report}")
@RequiredArgsConstructor
public class NocVacationPaymentController {

    private final NocVacationPaymentService nocVacationPaymentService;
    private final MessOpeningBalService messOpeningBalService;
    private final MessToCardTransferControllerService messToCardTransferControllerService;
    private final StudentMessPriorityRegistrationService studentMessPriorityRegistrationService;
    private final MessMasterCommonService messMasterCommonService;
    private final CommonResponseUtil commonResponseUtil;
    private final  MessageSource messageSource;
    private final StudentDetailsInfoService studentDetailsInfoService;

    @Value("${url.noc.vacation.payment.report}")
    private String baseUrl;

    @GetMapping("${url.widget}")
    public String getLedgerReportWidget(ModelMap model) {
        boolean messToCardTransferActive = messToCardTransferControllerService.isMessToCardTransferActive();
        model.addAttribute("messToCardTransferActive", messToCardTransferActive);
        return HTMLPage.LEDGER_REPORT_WIDGET;
    }

    @GetMapping
    public String getLedgerReport(ModelMap model,HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        MessLedgerReportSelectForm selectForm = flashInputMap != null && flashInputMap.get("selectForm") != null
                ? ((MessLedgerReportSelectForm) flashInputMap.get("selectForm"))
                : new MessLedgerReportSelectForm();

        Map<String, Map<String, List<LedgerReportDto>>> ledgerReport = flashInputMap != null && flashInputMap.get("ledgerReport") != null
                ? ((Map<String, Map<String, List<LedgerReportDto>>>) flashInputMap.get("ledgerReport"))
                : null;

        LedgerReportDto ledgerSummaryTotals = flashInputMap != null && flashInputMap.get("ledgerSummaryTotals") != null
                ? ((LedgerReportDto) flashInputMap.get("ledgerSummaryTotals"))
                : null;

        Double openingBal = flashInputMap != null && flashInputMap.get("openingBal") != null
                ? ((Double) flashInputMap.get("openingBal"))
                : 0.0;

        model.addAttribute("selectForm", selectForm);
        model.addAttribute("ledgerReport", ledgerReport);
        model.addAttribute("openingBal", openingBal);
        model.addAttribute("ledgerSummaryTotals", ledgerSummaryTotals);
        model.addAttribute("isReport", false);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return HTMLPage.NOC_VACATION_PAYMENT;
    }

    @PostMapping
    public String getLedgerReportList(@ModelAttribute MessLedgerReportSelectForm selectForm, RedirectAttributes redirectAttributes) {
        Map<String, Map<String, Object>> ledgerReport = nocVacationPaymentService.getLedgerReport(selectForm, false);
        Double openingBal = messOpeningBalService.getOpeningBal();
        redirectAttributes.addFlashAttribute("ledgerReport", ledgerReport);
        redirectAttributes.addFlashAttribute("selectForm", selectForm);
        redirectAttributes.addFlashAttribute("ledgerSummaryTotals", nocVacationPaymentService.getTotalLedgerSummary(ledgerReport));
        redirectAttributes.addFlashAttribute("openingBal", openingBal);
        Long lastClosingBalance = 0L;

        try {
            // Get last value
            if (!ledgerReport.isEmpty()) {
                Map<String, Object> res = null;
                Iterator<Map.Entry<String, Map<String, Object>>> iterator = ledgerReport.entrySet().iterator();
                while (iterator.hasNext()) {
                    res = iterator.next().getValue(); // Updates until last element
                }
                if(!res.isEmpty()) {
                    String lastValue = (String) res.get("totalClosingBalance");
                    if(lastValue  != null) {
                        lastValue = lastValue.replaceAll(ModelConstants.decimalNumericPattern, ModelConstants.EMPTY_STRING);
                        lastClosingBalance = (long) Double.parseDouble(lastValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StudentChargesDto studentChargesAndAmountDetails = nocVacationPaymentService.getStudentChargesAndAmountDetails(selectForm);
        String studentId = selectForm.getStudentID().toUpperCase();
        String studentIds = String.join(", ", studentDetailsInfoService.getStudentIdsByPreviousId(studentId));
        studentChargesAndAmountDetails.setOpeningBalance(lastClosingBalance);
        redirectAttributes.addFlashAttribute("studentChargesDto", studentChargesAndAmountDetails);
        redirectAttributes.addFlashAttribute("studentIds", studentIds);
        return Constants.REDIRECT+baseUrl;
    }


    @GetMapping("${url.noc.vacation.payment.report.transaction}" + "/{id}")
    public @ResponseBody BaseResponse nocVacationTransaction(@PathVariable String id,
                                                             @RequestParam String amount,
                                                             @RequestParam String transactionType,
                                                             @RequestParam String eventId) throws Exception {
        try {
            boolean status = nocVacationPaymentService.claimPenaltyCharge(id,amount,transactionType,eventId);
            return CommonResponseUtil.generateDeleteResponseByStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }


    @GetMapping("${url.noc.vacation.payment.report.settlement}" + "/{id}")
    public String showSettlementScreen(@PathVariable String id, @RequestParam String amount, ModelMap model) {
        if (id != null) {
            model.addAttribute("settlementFormDTO", nocVacationPaymentService.getSettlementId(id,amount));
        } else {
            model.addAttribute("settlementFormDTO", new SettlementFormDTO());
        }
        return HTMLPage.SETTLEMENT_MODAL;
    }

    @PostMapping("${url.noc.vacation.payment.report.settlement}")
    public @ResponseBody BaseResponse saveSettlement(@Valid @ModelAttribute SettlementFormDTO settlementFormDTO,
                                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        try {
            boolean isSaved = nocVacationPaymentService.saveSettlement(settlementFormDTO);
            BaseResponse baseResponse = new BaseResponse();
            String message, status;
            if (isSaved) {
                message ="response.noc.settlement.success";
                status = "response.status.success";
            } else {
                message = "response.noc.settlement.failed";
                status = "response.status.failure";
            }
            baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
            baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
            return baseResponse;
        } catch (Exception e) {
            e.printStackTrace();
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }

    @PostMapping("${url.noc.undo.settlement}")
    public @ResponseBody BaseResponse undoSettlment(@RequestBody Map<String, String> request) {
        try {
            String studentId = request.get("studentId");
            boolean isSaved = nocVacationPaymentService.undoSettlement(studentId);
            BaseResponse baseResponse = new BaseResponse();
            String message, status;
            if (isSaved) {
                message ="response.noc.undo.settlement.success";
                status = "response.status.success";
            } else {
                message = "response.noc.undo.settlement.failed";
                status = "response.status.failure";
            }
            baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
            baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
            return baseResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }
}
