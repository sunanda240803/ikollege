package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.MailTemplateService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping(value = "${url.mail.template}")
@RequiredArgsConstructor
public class MailTemplateController {

    private final MailTemplateService mailTemplateService;
    private final CommonResponseUtil commonResponseUtil;

    @Value("${url.mail.template}")
    private String baseUrl;

    @GetMapping
    public String getMailTemplateList(HttpServletRequest request, PaginationForm paginationForm, ModelMap model) {
        Page<MailTemplateDto> mailTemplates = mailTemplateService.getMailTemplates(paginationForm);
        commonResponseUtil.updateCommonModelAttributes(model, request, mailTemplates, paginationForm);
        return HTMLPage.MAIL_TEMPLATE;
    }

    @GetMapping(value = "${id}")
    public String getMailTemplateByType(@PathVariable("id") String type, ModelMap model, HttpServletRequest request) {
        MailTemplateDto mailTemplateByType = mailTemplateService.getMailTemplateByType(type);
        model.addAttribute("mailTemplateDto", mailTemplateByType);
        String menuHeading = type.equals("0") ? commonResponseUtil.getMessage("message.label.add.mail.template") :
                commonResponseUtil.getMessage("message.label.edit.mail.template");
        commonResponseUtil.updateHeaderForm(request, model, menuHeading, false);
        return HTMLPage.ADD_EDIT_MAIL_TEMPLATE;
    }

    @PostMapping
    public String saveOrUpdateMailTemplate(@ModelAttribute MailTemplateDto mailTemplateDto, BindingResult bindingResult,
                                           ModelMap model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        mailTemplateService.validateMailTemplate(mailTemplateDto,bindingResult);
        if(bindingResult.hasErrors()) {
            String menuHeading = Objects.nonNull(mailTemplateDto.getActiveFlag()) && !mailTemplateDto.getActiveFlag().isEmpty() ?
                    commonResponseUtil.getMessage("message.label.edit.mail.template") :
                    commonResponseUtil.getMessage("message.label.add.mail.template");
            commonResponseUtil.updateHeaderForm(request, model, menuHeading, false);
            return HTMLPage.ADD_EDIT_MAIL_TEMPLATE;
        }
        String status = mailTemplateService.saveOrUpdateMailTemplate(mailTemplateDto);
        String message = status.equals(Constants.SAVED) ? "message.mail.template.save":"message.mail.template.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes,message);
        return Constants.REDIRECT+baseUrl;
    }

    @PostMapping(value = "${url.validate.mail.type}")
    public ResponseEntity<Boolean> existsByMailType(@RequestParam("mailType") String mailType) {
        boolean b = mailTemplateService.existsByMailType(mailType);
        return ResponseEntity.ok(b);
    }
}