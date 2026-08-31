package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mess.BulkMaiService;
import com.iitm.hosteldine.service.mess.MessPeriodConfigService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping(value = "${url.mess.period.config}")
@RequiredArgsConstructor
public class MessPeriodConfigController {

    private final MessPeriodConfigService messPeriodConfigService;
    private final CommonResponseUtil commonResponseUtil;
    private final SimsConfigDataService simsConfigDataService;
    private final BulkMaiService bulkMailService;

    @Value("${url.mess.period.config}")
    private String baseUrl;

    @GetMapping
    public String getMessPeriodConfigList(PaginationForm form, HttpServletRequest request, ModelMap model) {
        Page<MessMasterControllerDto> allMessMasterController = messPeriodConfigService.getAllMessPeriodConfig(form);
        commonResponseUtil.updateCommonModelAttributes(model, request, allMessMasterController, form);
        return HTMLPage.MESS_PERIOD_CONFIG;
    }

    @GetMapping(value = "${id}")
    public String getMessPeriodConfigById(@PathVariable("id") Long id, ModelMap model, HttpServletRequest request, HttpSession session) {
        MessMasterControllerDto messMasterControllerDto = messPeriodConfigService.getMessPeriodConfigById(id);
        ArrayList<String> monthList = simsConfigDataService.getSimConfigValueArrayList("MONTH_LIST");
        model.addAttribute("messMasterControllerDto", messMasterControllerDto);
        model.addAttribute("monthList", monthList);
        updateHeaderForm(id, request, model);
        commonResponseUtil.updateCommonModelAttributes(model, request);

        String logTag = (String) session.getAttribute(commonResponseUtil.getMessage("upload.log.tag"));
        // If no upload in progress, create a new idle tag
        if (logTag == null) {
            logTag = "bulkEmail-" + UUID.randomUUID();
            session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), logTag);
        }
        model.put("logTag", logTag);
        return HTMLPage.ADD_MESS_PERIOD_CONFIG;
    }

    @PostMapping
    public String saveOrUpdateMessPeriodConfig(@ModelAttribute MessMasterControllerDto messMasterControllerDto, BindingResult result, HttpServletRequest request, ModelMap model, RedirectAttributes redirectAttributes) {
        messPeriodConfigService.validateMessPeriodConfig(messMasterControllerDto, result);
        if (result.hasErrors()) {
            updateHeaderForm(messMasterControllerDto.getId(), request, model);
            return HTMLPage.ADD_MESS_PERIOD_CONFIG;
        }
        String status = messPeriodConfigService.saveOrUpdateMessPeriodConfig(messMasterControllerDto);
        String message = status.equals(Constants.SAVED) ? "message.mess.period.config.save" : "message.mess.period.config.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "${url.mess.mail}" + "${id}")
    public String getBulkMailTemplate(@PathVariable("id") Long id, ModelMap model) {
        MessMasterControllerDto dto = messPeriodConfigService.getMessPeriodConfigById(id);
        model.addAttribute("messMasterControllerDto", dto);
        return HTMLPage.STUDENT_MESS_BULK_MAIL;
    }

    @PostMapping(value = "${url.mess.mail}")
    public String sendStudentMessBulkMail(@ModelAttribute MessMasterControllerDto messMasterControllerDto,
                                          RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            String tag = "bulkEmail-" + UUID.randomUUID();
            session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), tag);
            messMasterControllerDto.setLogTag(tag);
            messMasterControllerDto = bulkMailService.startBulkMail(messMasterControllerDto);
            commonResponseUtil.updateSaveResponseByStatus("message", redirectAttributes,"message.process.started");
            //boolean status = messPeriodConfigService.sendStudentMessBulkMail(messMasterControllerDto);
           // commonResponseUtil.updateSaveResponseByStatus(String.valueOf(status), redirectAttributes, "response.mail.success");
        } catch (Exception e) {
            commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
        }
        return Constants.REDIRECT + baseUrl + "/" + messMasterControllerDto.getId();
    }

    private void updateHeaderForm(Long id, HttpServletRequest request, ModelMap model) {
        String menuHeading = id == null || id == 0 ? commonResponseUtil.getMessage("message.label.add.mess.period.config")
                : commonResponseUtil.getMessage("message.label.edit.mess.period.config");
        commonResponseUtil.updateHeaderForm(request, model, menuHeading, false);
    }
}
