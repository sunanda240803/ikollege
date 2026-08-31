package com.iitm.hosteldine.controller.student;

import com.iitm.hosteldine.dto.hostel.HostelFloorMasterDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.service.StudentBulkUploadService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.student.bulk.upload}")
public class StudentBulkUploadController {
    private final StudentBulkUploadService studentBulkUploadService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;
    @Value("${url.student.bulk.upload}")
    private String studentBulkUploadUrl;

    @GetMapping
    public String studentBulkUpload(@ModelAttribute HostelFloorMasterDto hostelFloorMasterDto, ModelMap map,
                                    HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        map.addAttribute("studentBulkUploadDto",
                flashInputMap != null && flashInputMap.get("studentBulkUploadDto") != null
                        ? ((StudentBulkInfoDto) flashInputMap.get("studentBulkUploadDto"))
                        : new StudentBulkInfoDto());
        commonResponseUtil.updateCommonModelAttributes(map, request);

        String logTag = (String) session.getAttribute(commonResponseUtil.getMessage("upload.log.tag"));
        // If no upload in progress, create a new idle tag
        if (logTag == null) {
            logTag = "studentAdd-" + UUID.randomUUID();
            session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), logTag);
        }
        map.put("logTag", logTag);
        return HTMLPage.STUDENT_BULK_UPLOAD;
    }
    
    @GetMapping(value = "${url.download.student.bulk.upload.template}")
    public void downloadStudentBulkUploadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Workbook workbook = studentBulkUploadService.downloadStudentBulkUploadTemplate();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        // Convert the ByteArrayOutputStream to a byte array
        byte[] excelBytes = bos.toByteArray();

        // Set the content type and headers for the response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=StudentBulkUploadTemplate.xlsx");
        response.setContentLength(excelBytes.length);

        // Write the byte array to the response output stream
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(excelBytes);
            outputStream.flush();
        }

    }

    @PostMapping(value = "${url.student.bulk.upload.save}")
    public String saveStudentBulkUpload(@ModelAttribute StudentBulkInfoDto studentBulkUploadDto, HttpServletRequest request,
                                        HttpServletResponse response, RedirectAttributes redirectAttrs,HttpSession session) throws IOException {
        String message = null, status = null;
        BaseResponse baseResponse = new BaseResponse();
        String s = "redirect:" + studentBulkUploadUrl;
        if (studentBulkUploadDto.getFile().isEmpty()) {
            redirectAttrs.addFlashAttribute("excelErrorList", "Please choose file to upload.");
            return "redirect:" + studentBulkUploadUrl;
        }

        String tag = "studentAdd-" + UUID.randomUUID();
        session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), tag);
        studentBulkUploadDto.setLogTag(tag);

        byte[] fileBytes = studentBulkUploadDto.getFile().getBytes();
        studentBulkUploadDto.setFileBytes(fileBytes);
        studentBulkUploadDto = studentBulkUploadService.startBulkUpload(studentBulkUploadDto);
        commonResponseUtil.updateSaveResponseByStatus("message", redirectAttrs,"message.upload.process.started");
        return s ;

//        StudentBulkInfoDto studentBulkInfoDto = studentBulkUploadService.saveStudentBulkUpload(studentBulkUploadDto.getFile());
//        if (!studentBulkInfoDto.getErrorList().isEmpty()) {
//            redirectAttrs.addFlashAttribute("excelErrorList", studentBulkInfoDto.getErrorList());
//            return s ;
//        } else {
//            message = messageSource.getMessage("response.student.bulk.upload.success", null, Locale.getDefault());
//            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
//            redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status));
//            return s ;
//        }
    }

    @GetMapping(value = "${url.download.all.students.details.view.report}")
    public void downloadAllStudentsDetailsViewReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = studentBulkUploadService.downloadAllStudentDetailsReport();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=All_Students_Details_Report.xlsx");
        response.setContentLength(excelBytes.length);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(excelBytes);
            outputStream.flush();
        }
    }

    @GetMapping(value = "${url.student.add.new}")
    public String showAddForm(ModelMap model) {
        model.addAttribute("studentBulkInfoDto", new StudentBulkInfoDto());
        model.addAttribute("courseHeadList",studentBulkUploadService.getCourseHeaders());
        return HTMLPage.STUDENT_BULK_UPLOAD_ADD_NEW; // Modal form for both Add/Edit
    }

    @PostMapping(value = "${url.student.add.new}")
    public @ResponseBody StudentBulkInfoDto saveStudent(@ModelAttribute StudentBulkInfoDto studentBulkUploadDto,
                                                        HttpServletRequest request, HttpServletResponse response,
                                                        RedirectAttributes redirectAttrs) throws IOException {

        return studentBulkUploadService.validateStudentAndSave(studentBulkUploadDto);
    }

}
