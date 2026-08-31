package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.dashboard.student.WorkflowMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.student.WorkflowMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.hostel.WorkflowMasterValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.workflow.master}")
public class WorkflowMasterController {
    private final WorkflowMasterService workflowMasterService;
    private final CommonResponseUtil commonResponseUtil;
    private final SimsConfigDataService simsConfigDataService;
    private final WorkflowMasterValidator workflowMasterValidator;

    @Value("${url.workflow.master}")
    private String baseUrl;

    @GetMapping
    public String getWorkflowMasterList(PaginationForm form, ModelMap map, HttpServletRequest request) {
        Page<WorkflowMasterDto> scholarsStayExtensionList = workflowMasterService.getWorkflowMasterList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request,scholarsStayExtensionList,form);
        return HTMLPage.WORKFLOW_MASTER_LIST;
    }

    @GetMapping(value = "${url.get}" + "${id}")
    public String getWorkflowMasterById(@PathVariable("id") Long id, ModelMap model, HttpServletRequest request) {
        WorkflowMasterDto workflowMasterDto = commonResponseUtil.handleModalFormError(
                request,
                model,
                HostelConstants.WORKFLOW_MASTER_FORM_KEY.getConstants(),
                WorkflowMasterDto.class);
        if (Objects.nonNull(id) && id > 0) {
            workflowMasterDto = workflowMasterService.getWorkflowMasterById(id);
        }
        model.addAttribute(HostelConstants.WORKFLOW_MASTER_CATEGORY.getConstants(), simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.WORKFLOW_MASTER_CATEGORY));
        model.addAttribute(HostelConstants.WORKFLOW_MASTER_AUTHORITY_TYPE.getConstants(), simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.WORKFLOW_MASTER_AUTHORITY_TYPE));
        model.addAttribute(HostelConstants.WORKFLOW_MASTER_FORM_KEY.getConstants(), workflowMasterDto);
        return HTMLPage.WORKFLOW_MASTER_LIST_MODEL;
    }

    @PostMapping(value = "${url.save}")
    public String saveOrUpdateWorkflowMaster(@ModelAttribute WorkflowMasterDto workflowMasterDto, BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes) {
        workflowMasterValidator.validate(workflowMasterDto, bindingResult);
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, workflowMasterDto);
            return Constants.REDIRECT + baseUrl;
        }
        String status = workflowMasterService.saveOrUpdateWorkflowMaster(workflowMasterDto);
        String message = status.equalsIgnoreCase(Constants.SAVED) ? "message.workflow.master.save" : "message.workflow.master.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes,message);
        return Constants.REDIRECT + baseUrl;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteWorkflowMasterById(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(workflowMasterService.deleteWorkflowMaster(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }
}
