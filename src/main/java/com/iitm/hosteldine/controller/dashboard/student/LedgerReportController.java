package com.iitm.hosteldine.controller.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.studentDashboard.LedgerReportDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessPriorityRegistrationDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.common.SelectForm;
import com.iitm.hosteldine.service.dashboard.LedgerReportService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessOpeningBalService;
import com.iitm.hosteldine.service.student.MessToCardTransferControllerService;
import com.iitm.hosteldine.service.studentDashboard.StudentMessPriorityRegistrationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${url.ledger.report}")
@RequiredArgsConstructor
public class LedgerReportController {

    private final LedgerReportService ledgerReportService;
    private final MessOpeningBalService messOpeningBalService;
    private final MessToCardTransferControllerService messToCardTransferControllerService;
    private final StudentMessPriorityRegistrationService studentMessPriorityRegistrationService;
    private final MessMasterCommonService messMasterCommonService;

    @Value("${url.ledger.report}")
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
        SelectForm selectForm = flashInputMap != null && flashInputMap.get("selectForm") != null
                ? ((SelectForm) flashInputMap.get("selectForm"))
                : new SelectForm();

        Map<String, Map<String, List<LedgerReportDto>>> ledgerReport = flashInputMap != null && flashInputMap.get("ledgerReport") != null
                ? ((Map<String, Map<String, List<LedgerReportDto>>>) flashInputMap.get("ledgerReport"))
                : null;

        Double openingBal = flashInputMap != null && flashInputMap.get("openingBal") != null
                ? ((Double) flashInputMap.get("openingBal"))
                : 0.0;

        model.addAttribute("selectForm", selectForm);
        model.addAttribute("ledgerReport", ledgerReport);
        model.addAttribute("openingBal", openingBal);
        return HTMLPage.LEDGER_REPORT;
    }

    @PostMapping
    public String getLedgerReportList(@ModelAttribute SelectForm selectForm, RedirectAttributes redirectAttributes) {
        Map<String, Map<String, Object>> ledgerReport = ledgerReportService.getLedgerReport(selectForm);
        Double openingBal = messOpeningBalService.getOpeningBal();
        redirectAttributes.addFlashAttribute("ledgerReport", ledgerReport);
        redirectAttributes.addFlashAttribute("selectForm", selectForm);
        redirectAttributes.addFlashAttribute("openingBal", openingBal);
        return Constants.REDIRECT+baseUrl;
    }

    @GetMapping(value = "${url.food.court.report}")
    public String getFoodCourtReport(ModelMap model) {
        List<LedgerReportDto> foodCourtReportList = ledgerReportService.getFoodCourtReport();

        // get mess priority registration list
        List<StudentMessPriorityRegistrationDto> messRegistrationList = studentMessPriorityRegistrationService.getMessRegistrationList(null);
        for (StudentMessPriorityRegistrationDto dto : messRegistrationList) {
            if (dto.getMessList() == null) {
                dto.setMessList(new ArrayList<>());
            }
        }
        if(messRegistrationList.getFirst() != null) {
            model.addAttribute("currentMessName", messRegistrationList.getFirst().getMessName());
        }else {
            model.addAttribute("currentMessName", "-");
        }

        // Mess master controller details
        List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
        String dinningFromDate = "", dinningToDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
        if(messControllerList != null ) {
            dinningFromDate = messControllerList.get(0).getDiningFromDate().format(formatter);
            dinningToDate = messControllerList.get(0).getDiningToDate().format(formatter);
        }
        model.addAttribute("dinningFromDate", dinningFromDate);
        model.addAttribute("dinningToDate", dinningToDate);

        model.addAttribute("foodCourtReportList", foodCourtReportList);
        return HTMLPage.FOOD_COURT_REPORT;
    }
}
