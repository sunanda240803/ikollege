package com.iitm.hosteldine.controller.collegeInfo;

import java.util.Map;
import java.util.Objects;

import com.iitm.hosteldine.dto.mailQueue.MailQueueDetailsDto;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.collegeInfo.FaqDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.collegeInfo.FaqService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.mail.management}")
@RequiredArgsConstructor
public class MailManagementController {
    private final CommonResponseUtil commonResponseUtil;
    private final MailQueueService mailQueueService;

    @GetMapping
    public String getMailList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
                             HttpServletRequest request) {
        Page<MailQueueDetailsDto> mailList = null;
        commonResponseUtil.getAdditionalParams(allParams, form);
        if (form.getAdditionalParam().get("fromDate") == null) {
            form.getAdditionalParam().put("fromDate", "");
            form.getAdditionalParam().put("toDate", "");
            form.getAdditionalParam().put("status", "");
        }
        mailList = mailQueueService.getList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request, mailList, form);
        return HTMLPage.MAIL_LIST;
    }

    @GetMapping(value = "${id}")
    public String getMailById(@PathVariable("id") Long id, ModelMap model, HttpServletRequest request) {
        String formKey = "mailQueueDto";
        MailQueueDetailsDto mailQueueDetailsDto = commonResponseUtil.handleModalFormError(request, model, formKey, MailQueueDetailsDto.class);
        if (Objects.nonNull(id) && id > 0) {
            mailQueueDetailsDto = mailQueueService.getById(id);
        }
        model.addAttribute(formKey, mailQueueDetailsDto);
        return HTMLPage.MAIL_VIEW_MODAL;
    }
}
