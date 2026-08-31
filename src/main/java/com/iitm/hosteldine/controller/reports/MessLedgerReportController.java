package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.student.StudentChargesDto;
import com.iitm.hosteldine.dto.studentDashboard.LedgerReportDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.common.SelectForm;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.MessLedgerReportSelectForm;
import com.iitm.hosteldine.service.hostel.NocVacationPaymentService;
import com.iitm.hosteldine.service.mess.MessOpeningBalService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.ledger.report}")
public class MessLedgerReportController {
    private final CommonResponseUtil commonResponseUtil;
    private final NocVacationPaymentService nocVacationPaymentService;
    private final MessOpeningBalService messOpeningBalService;
    private final StudentDetailsInfoService studentDetailsInfoService;

    @Value("${url.mess.ledger.report}")
    private String baseUrl;

    @GetMapping
    public String getLedgerReport(ModelMap model, HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        MessLedgerReportSelectForm selectForm = flashInputMap != null && flashInputMap.get("selectForm") != null
                ? ((MessLedgerReportSelectForm) flashInputMap.get("selectForm"))
                : new MessLedgerReportSelectForm();

        Map<String, Map<String, List<LedgerReportDto>>> ledgerReport = flashInputMap != null && flashInputMap.get("ledgerReport") != null
                ? ((Map<String, Map<String, List<LedgerReportDto>>>) flashInputMap.get("ledgerReport"))
                : null;

        List<LedgerReportDto> ledgerReportList = flashInputMap != null && flashInputMap.get("ledgerReportList") != null
                ? ((List<LedgerReportDto>) flashInputMap.get("ledgerReportList"))
                : null;

        LedgerReportDto ledgerSummaryTotals = flashInputMap != null && flashInputMap.get("ledgerSummaryTotals") != null
                ? ((LedgerReportDto) flashInputMap.get("ledgerSummaryTotals"))
                : null;

        Double openingBal = flashInputMap != null && flashInputMap.get("openingBal") != null
                ? ((Double) flashInputMap.get("openingBal"))
                : 0.0;

        model.addAttribute("selectForm", selectForm);
        model.addAttribute("ledgerReport", ledgerReport);
        model.addAttribute("ledgerReportList", ledgerReportList);
        model.addAttribute("ledgerSummaryTotals", ledgerSummaryTotals);
        model.addAttribute("openingBal", openingBal);
        model.addAttribute("isReport", true);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return HTMLPage.NOC_VACATION_PAYMENT;
    }

    @PostMapping
    public String getLedgerReportList(@ModelAttribute MessLedgerReportSelectForm selectForm, RedirectAttributes redirectAttributes) {
        try {
            Map<String, Map<String, Object>> ledgerReport = nocVacationPaymentService.getLedgerReport(selectForm, true);
            List<LedgerReportDto> ledgerReportList = nocVacationPaymentService.getLedgerReportList(selectForm, false);
            LedgerReportDto ledgerSummaryTotals = nocVacationPaymentService.getTotalLedgerSummary(ledgerReport);
            Double openingBal = messOpeningBalService.getOpeningBal();
            Long lastClosingBalance = 0L;

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

            String studentId = selectForm.getStudentID().toUpperCase();
            String studentIds = String.join(", ", studentDetailsInfoService.getStudentIdsByPreviousId(studentId));
            StudentChargesDto studentChargesAndAmountDetails = nocVacationPaymentService.getStudentChargesAndAmountDetails(selectForm);
            studentChargesAndAmountDetails.setOpeningBalance(lastClosingBalance);

            redirectAttributes.addFlashAttribute("ledgerReport", ledgerReport);
            redirectAttributes.addFlashAttribute("ledgerReportList", ledgerReportList);
            redirectAttributes.addFlashAttribute("ledgerSummaryTotals", ledgerSummaryTotals);
            redirectAttributes.addFlashAttribute("selectForm", selectForm);
            redirectAttributes.addFlashAttribute("openingBal", openingBal);
            redirectAttributes.addFlashAttribute("studentIds", studentIds);
            redirectAttributes.addFlashAttribute("studentChargesDto", studentChargesAndAmountDetails);
        } catch (Exception e) {
            commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
        }
        return Constants.REDIRECT+baseUrl;
    }

    @GetMapping("${url.download}")
    public void downloadMessLedgerReportExcel(@RequestParam String allVal, HttpServletResponse response) throws Exception {
        List<String> split = Arrays.stream(allVal.split(ModelConstants.COMMA)).toList();
        Workbook workbook = nocVacationPaymentService.downloadMessLedgerReportExcel(split);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();
        response.setContentType(ExcelConstants.CONTENT_TYPE);
        response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_LEDGER_FILE_NAME);
        response.setContentLength(excelBytes.length);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(excelBytes);
            outputStream.flush();
        }
    }

    @PostMapping("${url.delete}")
    public @ResponseBody BaseResponse deleteReceipt(@RequestBody Map<String, String> request) {
        try {
            String voucherNo = request.get("voucherNo");
            String studentId = request.get("studentId");
            int slNo = Integer.parseInt(request.get("slNo"));

            return CommonResponseUtil.generateDeleteResponseByStatus(
                    nocVacationPaymentService.deleteLedgerEntry(voucherNo, studentId,slNo)
            );
        } catch (RecordNotExistsException e) {
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }

}
