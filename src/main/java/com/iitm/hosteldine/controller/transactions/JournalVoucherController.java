package com.iitm.hosteldine.controller.transactions;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.reports.JournalVoucherService;
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
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.journal.voucher}")
@RequiredArgsConstructor
public class JournalVoucherController {

    private final CommonResponseUtil commonResponseUtil;
    private final JournalVoucherService journalVoucherService;
    private final AccountHeadService accountHeadService;

    @Value("${url.journal.voucher}")
    private String baseUrl;


    @GetMapping
    public String getVoucher(HttpServletRequest request, ModelMap model) {
        commonResponseUtil.updateCommonModelAttributes(model,request,null,null);
        return HTMLPage.JOURNAL_VOUCHER_LIST;
    }

    @GetMapping("${id}")
    public String getByVoucherNo(@PathVariable("id") String voucherNo, ModelMap model, HttpServletRequest request) {
        TransactionDto byVoucherNo = journalVoucherService.getByVoucherNo(voucherNo);
        List<AccountHeadDto> bankList = accountHeadService.getBankList(List.of("G", "D", "R"));
        commonResponseUtil.updateCommonModelAttributes(model,request,null,null);
        model.addAttribute("transactionDto",byVoucherNo);
        model.addAttribute("accountHeadList",bankList);
        commonResponseUtil.updateHeaderForm(request,model,null,false);
        return HTMLPage.ADD_JOURNAL_VOUCHER;
    }

    @PostMapping
    public String saveOrUpdateJournalVoucher(@ModelAttribute TransactionDto transactionDto, BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes) {
        String status = journalVoucherService.saveOrUpdateJournalVoucher(transactionDto);
        String message;
        if(Objects.nonNull(status)){
            if(Constants.SAVED.equalsIgnoreCase(status)){
                message = "message.journal.voucher.save";
            }
            else{
                message = "message.journal.voucher.update";
            }
        }
        else{
            message = "message.failed.save.update.journal";
        }
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        return Constants.REDIRECT+baseUrl;
    }

    @DeleteMapping(value = "${url.cancel}"+"${id}")
    public @ResponseBody BaseResponse cancelJournalVoucher(@PathVariable("id") String voucherNo, RedirectAttributes redirectAttributes) {
        String status = journalVoucherService.cancelJournalVoucher(voucherNo);
        String message;
        String code;
        if(Constants.SAVED.equalsIgnoreCase(status)){
            message = commonResponseUtil.getMessage("message.journal.voucher.cancel");
            code = Constants.SUCCESS;
        }
        else{
            message = commonResponseUtil.getMessage("message.journal.voucher.cancel.failed");
            code = Constants.FAILURE;
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(message);
        baseResponse.setStatus(code);
        return baseResponse;
    }
}