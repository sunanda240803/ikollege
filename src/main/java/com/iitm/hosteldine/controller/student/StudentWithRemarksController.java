package com.iitm.hosteldine.controller.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.student.StudentWithRemarksService;
import com.iitm.hosteldine.util.HTMLPage;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.student.with.remarks}")
public class StudentWithRemarksController {

    private final CommonResponseUtil commonResponseUtil;
    private final StudentWithRemarksService studentWithRemarksService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final MessageSource messageSource;

    @Value("${url.student.with.remarks}")
    private String baseUrl;

    @GetMapping
    public String getStudentWithRemarks(@ModelAttribute StudentBlackListDetailDto studentBlackListDetailDto,PaginationForm form, ModelMap map, HttpServletRequest request) {
        Page<StudentBlackListDetailDto> studentBlackListDetailDtoPage = studentWithRemarksService.getStudentWithRemarksList(form);
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        commonResponseUtil.updateCommonModelAttributes(map, request,studentBlackListDetailDtoPage,form);
        map.addAttribute(StudentConstants.STUDENT_WITH_REMARK.getStudentConstant(),
                flashInputMap != null && flashInputMap.get(StudentConstants.STUDENT_WITH_REMARK.getStudentConstant()) != null
                        ? flashInputMap.get(StudentConstants.STUDENT_WITH_REMARK.getStudentConstant())
                        : new StudentBulkInfoDto());
        return HTMLPage.STUDENT_WITH_REMARKS_LIST;
    }

    @GetMapping(value = "${url.get}" + "${id}")
    public String getStudentWithRemarkById(@PathVariable("id") Long id, ModelMap model, HttpServletRequest request) {
        StudentBlackListDetailDto studentBlackListDetailDto = commonResponseUtil.handleModalFormError(
                request,
                model,
                StudentConstants.STUDENT_WITH_REMARK.getStudentConstant(),
                StudentBlackListDetailDto.class
        );

        StudentDetailsInfoDto existingStudent = null;
        List<String> previousStudent = null;
        StudentDetailsInfoDto studentDetailsInfoDto;
        String studentName = ModelConstants.EMPTY_STRING;

        if (Objects.nonNull(id) && id > 0) {
            studentBlackListDetailDto = studentWithRemarksService.getStudentBlackListById(id);
            studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(studentBlackListDetailDto.getStudentId());
            if (Objects.nonNull(studentDetailsInfoDto)) {
                String studentLastName = Objects.nonNull(studentDetailsInfoDto.getLastName()) ? ModelConstants.SPACE + studentDetailsInfoDto.getLastName() : ModelConstants.EMPTY_STRING;
                studentName = Objects.requireNonNull(studentDetailsInfoDto).getFirstName() + studentLastName;
            }
            existingStudent = studentDetailsInfoService.getStudentInfoDetails(studentBlackListDetailDto.getStudentId());
            previousStudent = studentDetailsInfoService.getStudentPreviousInfoDetails(Objects.requireNonNull(existingStudent).getPreviousId());
        }

        model.addAttribute(StudentConstants.STUDENT_WITH_REMARK.getStudentConstant(), studentBlackListDetailDto);
        model.addAttribute(StudentConstants.STUDENT_NAME.getStudentConstant(), studentName);
        return HTMLPage.STUDENT_WITH_REMARKS_MODAL;
    }


    @PostMapping(value = "${url.save}")
    public String saveOrUpdateStudentWithRemark(@Valid @ModelAttribute StudentBlackListDetailDto studentBlackListDetailDto, BindingResult bindingResult,
                                                RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult,studentBlackListDetailDto);
            return Constants.REDIRECT + baseUrl;
        }
        String status = studentWithRemarksService.saveOrStudentBlackList(studentBlackListDetailDto);
        String message=status.equalsIgnoreCase(Constants.SAVED) ? "message.student.blacklist.details.save" : "message.student.blacklist.details.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes,message);
        return Constants.REDIRECT + baseUrl;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteStudentBlackListById(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(studentWithRemarksService.deleteStudentBlackList(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }

    @PutMapping("${url.revoke.blacklist}" + "${id}")
    public @ResponseBody BaseResponse revokeStudentBlackListById(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(studentWithRemarksService.revokeStudentBlackList(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }

    @GetMapping(value = "${url.student.remark.template.download}")
    public void downloadStudentWithRemarkUploadTemplate(HttpServletResponse response) {
        try (Workbook workbook = studentWithRemarksService.downloadStudentRemarkUploadTemplate();
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(bos);
            byte[] excelBytes = bos.toByteArray();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=StudentWithRemarks.xlsx");
            response.setContentLength(excelBytes.length);
            outputStream.write(excelBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @PostMapping(value = "${url.student.remark.upload.save}")
    public String saveStudentWithRemarkBulkDetails(@Valid @ModelAttribute StudentBlackListDetailDto studentBlackListDetailDto,
                                                RedirectAttributes redirectAttributes) {
        if (studentBlackListDetailDto.getFile().isEmpty()) {
            redirectAttributes.addFlashAttribute(StudentConstants.EXCEL_ERROR_LIST.getStudentConstant(),
                    messageSource.getMessage("message.label.choose.file.to.upload", null, Locale.getDefault()));
            return Constants.REDIRECT + baseUrl;
        }
        StudentBlackListDetailDto studentBlacklistBulkUpload = studentWithRemarksService.saveStudentBlacklistBulkUpload(studentBlackListDetailDto.getFile());
        if (studentBlacklistBulkUpload.getErrorList() == null) {
            commonResponseUtil.updateSaveResponseByStatus(Constants.SAVED, redirectAttributes,"message.student.blacklist.details.save");
        } else {
            redirectAttributes.addFlashAttribute(StudentConstants.EXCEL_ERROR_LIST.getStudentConstant(), studentBlacklistBulkUpload.getErrorList());
        }
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "/{studentId}")
    public @ResponseBody ResponseEntity<Map<String, Boolean>> studentIdValidation(
            @PathVariable("studentId") String studentId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        var studentInfo = studentDetailsInfoService.getStudentInfoDetails(studentId.toUpperCase());
        boolean studentIDNotExists = studentInfo.getStudentId() == null;
        boolean hasPreviousId = !studentDetailsInfoRepository.checkStudentIdExistInPreviousId(studentId.toUpperCase()).isEmpty();
        boolean hasOverlappingDates = false;
        if (!studentIDNotExists) {
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                var duplicateEntry = studentWithRemarksService.getStudentDuplicateDateBlacklistInfo(
                        LocalDate.parse(fromDate),
                        LocalDate.parse(toDate),
                        studentId);
                hasOverlappingDates = duplicateEntry.isPresent();
            }
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put(StudentConstants.STUDENT_ID_NOT_EXIST.getStudentConstant(), studentIDNotExists);
        response.put(StudentConstants.HAS_PREV_ID.getStudentConstant(), hasPreviousId);
        response.put(StudentConstants.HAS_OVERLAPPING_DATES.getStudentConstant(), hasOverlappingDates);
        return ResponseEntity.ok(response);
    }

}
