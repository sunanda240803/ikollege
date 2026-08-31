package com.iitm.hosteldine.controller.biometric;


import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.dto.student.StudentComplaintDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.biometric.BiometricService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Controller
@RequestMapping(value = URLConstant.FR_ADMIN)
public class FrAdminController {
    private BiometricService biometricService;
    private CommonController commonController;
    private CommonResponseUtil commonResponseUtil;
    private MessageSource messageSource;
    private MessMasterService messMasterService;

    @GetMapping
    public String index(ModelMap model, HttpServletRequest request) {
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/frAdmin";
    }

    @GetMapping(value = "${url.get.report}")
    String getReportScreen(HttpServletRequest request, ModelMap model) {
        model.addAttribute("messMasterList", messMasterService.getMessMasterList());
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return HTMLPage.DEVICE_FR_LOG_REPORT;
    }

    @GetMapping(value = "${url.excel.download}")
    ResponseEntity<byte[]> downloadComplaintDetails(@RequestParam(required = false) String messId, @RequestParam(required = false) String studentId, @RequestParam String fromDate, @RequestParam String toDate, PaginationForm form, HttpServletResponse response) {
        try {
            form.setSize(Integer.MAX_VALUE);
            Long mId = Objects.nonNull(messId) && !messId.isEmpty() ? Long.parseLong(messId) : null;
            String stdId = Objects.nonNull(studentId) && !studentId.isEmpty() ? studentId : null;
            Workbook workbook = biometricService.generateDeviceFRLogsReport(stdId, mId, LocalDate.parse(fromDate), LocalDate.parse(toDate), "message.device.fr.log.report");
            String fileName  = commonResponseUtil.getMessage("message.device.fr.log.report.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage("message.device.fr.log.report.filename", new Object[]{fileName}, Locale.getDefault()));
            response.setContentLength(excelBytes.length);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setBiometricService(BiometricService biometricService) {
        this.biometricService = biometricService;
    }

    @Autowired public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    @Autowired
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setMessMasterService(MessMasterService messMasterService) {
        this.messMasterService = messMasterService;
    }
}
