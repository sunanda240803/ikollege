package com.iitm.hosteldine.controller.transactions;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.transactions.ReceiptEntryDto;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.transactions.ReceiptEntryService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.receipt.entry}")
@RequiredArgsConstructor
public class ReceiptEntryController {

    private final AccountHeadService accountHeadService;
    private final CommonResponseUtil commonResponseUtil;
    private final ReceiptEntryService receiptEntryService;

    @Value("${url.receipt.entry}")
    private String baseUrl;

    @GetMapping
    public String getReceiptEntryPage(ModelMap model, HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(model, request, null, null);
        model.addAttribute("bankList", accountHeadService.getBankList(List.of("B","C")));
        model.addAttribute("receiptEntryDto", ReceiptEntryDto.builder().build());
        return HTMLPage.RECEIPT_ENTRY;
    }

    @GetMapping(value = "${id}")
    public @ResponseBody String validateStudentId(@PathVariable("id") String studentId) {
        String status = receiptEntryService.validateStudentId(studentId);
        if (Objects.nonNull(status)) {
            return commonResponseUtil.getMessage(status);
        } else {
            return Strings.EMPTY;
        }
    }

    @PostMapping
    public String saveReceiptEntry(@ModelAttribute ReceiptEntryDto receiptEntryDto, BindingResult result, ModelMap model,
                                   HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception{
        receiptEntryService.validate(receiptEntryDto, result);
        if (result.hasErrors()) {
            model.addAttribute("bankList", accountHeadService.getBankList(List.of("B","C")));
            commonResponseUtil.updateCommonModelAttributes(model, request, null, null);
            return HTMLPage.RECEIPT_ENTRY;
        }
        String status = receiptEntryService.saveReceiptEntry(receiptEntryDto);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, "message.receipt.entry.save");
        return Constants.REDIRECT + baseUrl;

    }
}