package com.iitm.hosteldine.controller.dashboard.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.HostelEnrollmentDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailDto;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanHostelEnrollmentService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.mess.StudentMessAllocationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.dean.hostel.enrollment}")
@RequiredArgsConstructor
public class DeanHostelEnrollmentController {

    private final DeanHostelEnrollmentService deanHostelEnrollmentService;
    private final HostelMasterService hostelMasterService;
    private final MessageSource messageSource;
    private final StudentMessAllocationService studentMessAllocationService;
    private final CommonService commonService;

    @Value("${url.dean.hostel.enrollment}")
    private String baseUrl;

    @Value("${url.late.fee.enrollment}")
    private String enrollmentUrl;

    private final CommonResponseUtil commonResponseUtil;
    private final DeanDashboardService deanDashboardService;

    @GetMapping
    public String getHostelEnrollment(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) {
        DeanApprovalDto deanApprovalDto = deanDashboardService.getDeanMenuList();
        map.addAttribute("deanApprovalDto", deanApprovalDto);
        DeanApprovalDto columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));
        map.addAttribute("columnDto", columnList);

        if (!form.isSearchFilter()) {
            List<String> filterList = List.of("validationStatus", "studentName", "studentId", "hostelName");
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }
        commonResponseUtil.getAdditionalParams(allParams, form);

        map.addAttribute("validationStatusList", "Validating,Approved,Rejected".split(","));
        Page<HostelEnrollmentDto> hostelEnrollmentList = deanHostelEnrollmentService.getHostelEnrollmentList(form, baseUrl.replace("/", ""));
        commonResponseUtil.updateCommonModelAttributes(map, request, hostelEnrollmentList, form);
        map.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(),commonService.getHostelListForUser());

        map.addAttribute("currentMp", studentMessAllocationService.checkCurrentMessPeriod());
        map.addAttribute("nextMp", studentMessAllocationService.checkNextMessPeriod());
        map.addAttribute("needBulkUpload", RoleEnum.DEAN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()));
        /*HeaderForm headerForm = (HeaderForm) model.getAttribute(ModelConstants.HEADER_FORM);
        if (headerForm != null) {
            headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
                    messageSource.getMessage("message.button.download.pdf", null, Locale.getDefault()),
                    ModelConstants.FA_FA_DOWNLOAD);
        }*/
        return HTMLPage.HOSTEL_ENROLLMENT;
    }

    @GetMapping(value = "${url.download.student.bulk.upload.template}")
    public void downloadStudentBulkUploadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Workbook workbook = deanHostelEnrollmentService.downloadStudentBulkUploadTemplate();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        // Convert the ByteArrayOutputStream to a byte array
        byte[] excelBytes = bos.toByteArray();

        // Set the content type and headers for the response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BulkApproveTemplate.xlsx");
        response.setContentLength(excelBytes.length);

        // Write the byte array to the response output stream
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(excelBytes);
            outputStream.flush();
        }

    }

    @GetMapping(value = "${url.late.fee.enrollment}")
    public String getLateFeeEnrollmentList(PaginationForm form, ModelMap map, HttpServletRequest request) {
        Page<StudentHostelPaymentLateFeeDetailDto> lateFeeEnrollmentList = deanHostelEnrollmentService.getLateFeeEnrollmentList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request, lateFeeEnrollmentList, form);
        return HTMLPage.LATE_FEE_ENROLLMENT;
    }

    @GetMapping(value = "${url.late.fee.enrollment}" + "${id}")
    public String addLateFee(ModelMap model, HttpServletRequest request) {
        StudentHostelPaymentLateFeeDetailDto studentHostelPaymentLateFeeDetailDto = commonResponseUtil.
                handleModalFormError(request, model, "studentHostelPaymentLateFeeDetailDto", StudentHostelPaymentLateFeeDetailDto.class);
        model.addAttribute("studentHostelPaymentLateFeeDetailDto", studentHostelPaymentLateFeeDetailDto);
        List<HostelMasterDto> hostelListByUser = hostelMasterService.getHostelListByUser();
        model.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(), hostelListByUser);
        return HTMLPage.ADD_LATE_FEE_MODAL;
    }

    @PostMapping(value = "${url.late.fee.enrollment}")
    public String saveLateFee(@Valid @ModelAttribute StudentHostelPaymentLateFeeDetailDto studentHostelPaymentLateFeeDetailDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        deanHostelEnrollmentService.validateLateFeeEnrollment(studentHostelPaymentLateFeeDetailDto, bindingResult);
        if (bindingResult.hasErrors()) {
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, studentHostelPaymentLateFeeDetailDto);
            return Constants.REDIRECT + baseUrl + enrollmentUrl;
        }
        String status = deanHostelEnrollmentService.saveLateFeeEnrollment(studentHostelPaymentLateFeeDetailDto);
        String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ? "message.late.fee.save" : "message.late.fee.fail";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        return Constants.REDIRECT + baseUrl + enrollmentUrl;
    }

    @GetMapping(value = "${url.dean.approve}" + "${id}")
    public String approveHostelEnrollment(@PathVariable("id") String requestKey, @RequestParam("messPeriod") String messPeriod,
                                          RedirectAttributes redirectAttributes) throws Exception {
        Boolean validKey = Utility.checkRequestType(requestKey, 3, 2);
        if (validKey) {
            String studentId = MCrypt.getInstance().decryptToString(requestKey).split(Constants.BACKTICK)[0];
            String status = deanHostelEnrollmentService.approveOrRejectHostelEnrollment(List.of(studentId),
                    WorkflowStatus.APPROVED.getStatus(), null, null);
            String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ?
                    "message.label.approved.successfully" : "message.label.approved.failure";
            commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        } else {
            return "Invalid";
        }
        return null;
    }

    @GetMapping(value = "${url.dean.reject}" + "${id}")
    public String rejectHostelEnrollment(@PathVariable("id") String requestKey, @RequestParam("reason") String reason,
                                         RedirectAttributes redirectAttributes) throws Exception {
        Boolean validKey = Utility.checkRequestType(requestKey, 3, 2);
        if (validKey) {
            String studentId = MCrypt.getInstance().decryptToString(requestKey).split(Constants.BACKTICK)[0];
            String status = deanHostelEnrollmentService.approveOrRejectHostelEnrollment(List.of(studentId),
                    WorkflowStatus.REJECTED.getStatus(), reason,null);
            String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ?
                    "message.label.rejected.successfully" : "message.label.rejected.failure";
            commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        } else {
            return "Invalid";
        }
        return null;
    }

    @PostMapping(value = "${url.student.bulk.upload.save}")
    public String saveStudentBulkUpload(@ModelAttribute StudentBulkInfoDto studentBulkUploadDto, RedirectAttributes redirectAttrs) throws IOException {
        String message;
        String status;
        String redirect = Constants.REDIRECT + baseUrl;
        if (studentBulkUploadDto.getFile().isEmpty()) {
            redirectAttrs.addFlashAttribute("excelErrorList", "Please choose file to upload.");
            return redirect;
        }

        StudentBulkInfoDto studentBulkInfoDto = deanHostelEnrollmentService.approveStudentBulkUploads(studentBulkUploadDto.getFile());

        if (!studentBulkInfoDto.getErrorList().isEmpty()) {
            redirectAttrs.addFlashAttribute("excelErrorList", studentBulkInfoDto.getErrorList());
        } else if(!studentBulkInfoDto.getStudentIds().isEmpty()){
            String approvedStatus = deanHostelEnrollmentService.approveOrRejectHostelEnrollment(studentBulkInfoDto.getStudentIds(),
                    WorkflowStatus.APPROVED.getStatus(), null,studentBulkInfoDto.getMessPeriod());
            message = messageSource.getMessage("response.student.bulk.approve.success", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status));
        }
        return redirect;
    }
}