package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.transactions.EstablishmentDebitDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.EstablishmentDebitService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.establishment.debit}")
public class EstablishmentDebitController {

    private final CommonResponseUtil commonResponseUtil;
    private final HostelMasterService hostelMasterService;
    private final EstablishmentDebitService establishmentDebitService;

    @Value("${url.establishment.debit}")
    private String baseUrl;

    @GetMapping
    public String establishmentDebit(@RequestParam Map<String, String> allParams, ModelMap map, HttpServletRequest request, PaginationForm form) {
        Page<EstablishmentDebitDto> establishmentDebitDtos = null;
        commonResponseUtil.getAdditionalParams(allParams, form);
        if (form.getAdditionalParam().get("hostelId") != null) {
            if (form.getAdditionalParam().get("amount").toString().isEmpty()){
                form.getAdditionalParam().put("amount", null);
            }
            establishmentDebitDtos = establishmentDebitService.getEstablishmentDebitList(form);
        } else {
            form.getAdditionalParam().put("date", "");
            form.getAdditionalParam().put("hostelId", 0L);
            form.getAdditionalParam().put("description", "");
            form.getAdditionalParam().put("amount", "");
            form.getAdditionalParam().put("selectOptions", "");
        }
        map.addAttribute("hostelList", hostelMasterService.getHostelList());
        map.addAttribute("selectOptions", form.getAdditionalParam().get("fixed"));
        map.addAttribute("description", form.getAdditionalParam().get("description"));
        map.addAttribute("amount", form.getAdditionalParam().get("amount"));
        map.addAttribute("date", form.getAdditionalParam().get("date"));
        commonResponseUtil.updateCommonModelAttributes(map, request, establishmentDebitDtos, form);
        return HTMLPage.ESTABLISHMENT_DEBITS;
    }

    @PostMapping(value = "${url.save.establishment.list}")
    public ResponseEntity<String> saveEstablishmentList(@RequestBody EstablishmentDebitDto establishmentDebitDto) {
        return ResponseEntity.ok(establishmentDebitService.saveEstablishmentDebitList(PageRequest.of(0, Integer.MAX_VALUE), establishmentDebitDto));
    }

    @GetMapping(value = "${url.template.download}")
    public void downloadExcelTemplate(HttpServletResponse response) {
        try (Workbook workbook = establishmentDebitService.downloadExcelTemplate();
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(bos);
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(ExcelConstants.CONTENT_TYPE);
            response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.ESTABLISHMENT_DEBIT_FILENAME);
            response.setContentLength(excelBytes.length);
            outputStream.write(excelBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @PostMapping(value = "${url.establishment.upload.save}")
    public ResponseEntity<EstablishmentDebitDto> saveEstablishmentDebitBulkUpload(
            @ModelAttribute EstablishmentDebitDto establishmentDebitDto) {

        EstablishmentDebitDto responseDto = EstablishmentDebitDto.builder().build();
        ArrayList<String> list = new ArrayList<>();

        if (establishmentDebitDto.getFile() == null || establishmentDebitDto.getFile().isEmpty()) {
            list.add(commonResponseUtil.getMessage("message.label.choose.file.to.upload"));
        responseDto.setErrorList(list);
            return ResponseEntity.ok(responseDto);
        }
        EstablishmentDebitDto processedDto = establishmentDebitService.saveEstablishmentDebitUpload(establishmentDebitDto);

        if (processedDto.getErrorList() != null && !processedDto.getErrorList().isEmpty()) {
            responseDto.setErrorList(processedDto.getErrorList());
        }
        return ResponseEntity.ok(responseDto);
    }
}
