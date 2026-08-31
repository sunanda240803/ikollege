package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.dto.dean.FacultyAccommodationRequestDto;
import com.iitm.hosteldine.form.common.FacultyAccommodatiomRequstForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.FacultyAccommodationRequestService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping(value = "${url.hostel.faculty.accommodation.request}")
@RequiredArgsConstructor
public class FacultyAccommodationRequestController {

    private final FacultyAccommodationRequestService facultyAccommodationRequestService;
    private final CommonResponseUtil commonResponseUtil;
    private final  MessageSource messageSource;
    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterRepository messMasterRepository;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.hostel.faculty.accommodation.request}")
    private String baseUrl;

    @GetMapping
    public String getFoodCourtReport( PaginationForm form, ModelMap model ,HttpServletRequest request) {

        model.addAttribute("searchForm", new FacultyAccommodatiomRequstForm());
        model.addAttribute("showTable", false);
        commonResponseUtil.updateCommonModelAttributes(model, request ,null , form);

        return HTMLPage.FACULTY_ACCOMMODATION_REQUEST;
    }

    @GetMapping("${url.get.list}")
    public String getFoodCourtReport(@ModelAttribute("searchForm") FacultyAccommodatiomRequstForm searchForm, PaginationForm form, ModelMap model, HttpServletRequest request) {

        model.addAttribute("searchForm", searchForm);
        Page<FacultyAccommodationRequestDto> facultyAccommodationLis = facultyAccommodationRequestService.getFacultyAccommodationList(form, searchForm);
        model.addAttribute("showTable", true);

        commonResponseUtil.updateCommonModelAttributes(model, request,facultyAccommodationLis,form);

        return HTMLPage.FACULTY_ACCOMMODATION_REQUEST;
    }

    @PostMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestReport(@RequestBody FacultyAccommodatiomRequstForm searchForm, @RequestParam Map<String, String> allParams,@ModelAttribute PaginationForm form, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        //Setting dynamic filter values
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
        form.setPage(1);
        form.setSize(Integer.MAX_VALUE);
        Page<FacultyAccommodationRequestDto> facultyAccommodationLis = facultyAccommodationRequestService.getFacultyAccommodationList(form, searchForm);

        try {
            Workbook workbook = facultyAccommodationRequestService.getFacultyAccommodationReport(facultyAccommodationLis.getContent());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = messageSource.getMessage("message.hostel.faculty.accommodation.report.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage(
                    "message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
            response.setContentLength(excelBytes.length);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
