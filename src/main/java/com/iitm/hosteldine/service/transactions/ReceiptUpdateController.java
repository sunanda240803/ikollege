package com.iitm.hosteldine.service.transactions;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessLedgerADto;
import com.iitm.hosteldine.dto.transactions.ReceiptEntryDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.receipt.update}")
@RequiredArgsConstructor
public class ReceiptUpdateController {

    private final AccountHeadService accountHeadService;
    private final ReceiptUpdateService receiptUpdateService;
    private final CommonResponseUtil commonResponseUtil;

    @Value("${url.receipt.update}")
    private String baseUrl;

    @GetMapping
    public String getReceiptUpdate(PaginationForm form, ModelMap model, HttpServletRequest request) {
        model.addAttribute("bankList", accountHeadService.getBankList(List.of("B")));
        Page<MessLedgerADto> receiptDetails = receiptUpdateService.getReceiptDetails(form);
        model.addAttribute("selectedBank", form.getSearch());
        model.addAttribute("receiptEntryDto", ReceiptEntryDto.builder().build());
        commonResponseUtil.updateCommonModelAttributes(model, request, receiptDetails, form);
        return HTMLPage.RECEIPT_UPDATE;
    }

    @PostMapping
    public String updateReceiptEntry(@ModelAttribute ReceiptEntryDto receiptEntryDto, RedirectAttributes redirectAttributes) throws Exception{
        try {
            String status = receiptUpdateService.updateReceiptDetails(receiptEntryDto);
            String message = Objects.nonNull(status) && Constants.SAVED.equalsIgnoreCase(status) ? "message.receipt.update.success"
                    : "message.receipt.update.failed";
            commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        } catch (RecordNotExistsException e) {
            commonResponseUtil.errorExceptionHandling(e);
        }
        return Constants.REDIRECT + baseUrl;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteReceipt(@PathVariable("id") String voucherNo) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(receiptUpdateService.deleteReceipt(voucherNo));
        } catch (RecordNotExistsException e) {
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }
}