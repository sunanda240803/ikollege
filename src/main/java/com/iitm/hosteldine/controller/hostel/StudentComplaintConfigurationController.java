package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.hostel.StudentComplaintConfigurationDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.student.StudentComplaintConfigurationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping(value = "${url.student.complaint.config}")
public class StudentComplaintConfigurationController {
    private final StudentComplaintConfigurationService studentComplaintConfigurationService;
    private final CommonResponseUtil commonResponseUtil;


    @Value("${url.student.complaint.config}")
    private String baseUrl;

    @GetMapping
    public String getStudentComplaintConfig(PaginationForm form, ModelMap map, HttpServletRequest request) {
        Page<StudentComplaintConfigurationDto> floorList = studentComplaintConfigurationService.getStudentComplaintConfigList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request,floorList,form);
        return HTMLPage.STUDENT_COMPLAINT_CONFIG_LIST;
    }

    @GetMapping(value = "${url.get}" + "${id}")
    public String getStudentComplaintConfigById(@PathVariable("id") Long id, ModelMap model,HttpServletRequest request) {
        StudentComplaintConfigurationDto studentComplaintConfigurationDto = commonResponseUtil.handleModalFormError(
                request,
                model,
                HostelConstants.STUDENT_COMPLAINT_CONFIG_FORM_KEY.getConstants(),
                StudentComplaintConfigurationDto.class);
        if (Objects.nonNull(id) && id > 0) {
            studentComplaintConfigurationDto = studentComplaintConfigurationService.getStudentComplaintConfigById(id);
        }
        model.addAttribute(HostelConstants.STUDENT_COMPLAINT_CONFIG_FORM_KEY.getConstants(), studentComplaintConfigurationDto);
        return HTMLPage.STUDENT_COMPLAINT_CONFIG_MODEL;
    }

    @PostMapping(value = "${url.save}")
    public String saveOrUpdateStudentComplaintConfig(@Valid @ModelAttribute StudentComplaintConfigurationDto studentComplaintConfigurationDto, BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, studentComplaintConfigurationDto);
            return "redirect:" + baseUrl;
        }
        String status = studentComplaintConfigurationService.saveOrUpdateStudentComplaintConfig(studentComplaintConfigurationDto);
        String message=status.equalsIgnoreCase(Constants.SAVED) ? "message.student.complaint.config.save" : "message.student.complaint.config.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        return "redirect:" + baseUrl;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteEventMasterById(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(studentComplaintConfigurationService.deleteStudentComplaintConfig(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }

    @GetMapping("${id}")
    public ResponseEntity<StudentComplaintConfigurationDto> getStudentComplaintConfigById(@PathVariable("id") Long id) {
        StudentComplaintConfigurationDto studentComplaintConfigurationDto = studentComplaintConfigurationService.getStudentComplaintConfigById(id);
        if (studentComplaintConfigurationDto != null) {
            return ResponseEntity.ok(studentComplaintConfigurationDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
