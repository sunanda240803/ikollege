package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.dto.hostel.UploadReceiptsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.hostel.UploadReceiptsRecord;
import com.iitm.hosteldine.service.hostel.UploadReceiptsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.hostel.UploadReceiptsValidator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "${url.upload.receipts}")
@RequiredArgsConstructor
public class UploadReceiptsController {
    private final CommonResponseUtil commonResponseUtil;
    private final UploadReceiptsService uploadReceiptsService;
    private final AccountHeadService accountHeadService;
	private final UploadReceiptsValidator validator;
	private final MessageSource messageSource;

    @Value("${url.upload.receipts}")
    private String baseUrl;

    @GetMapping
    public String getUploadReceiptsList(@RequestParam Map<String, String> allParams, ModelMap map, HttpServletRequest request,
                                        PaginationForm form,HttpSession session) {
        commonResponseUtil.getAdditionalParams(allParams, form);
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        UploadReceiptsDto uploadReceiptsDto = flashInputMap != null && flashInputMap.get("uploadReceiptsDto") != null
                ? (UploadReceiptsDto) flashInputMap.get("uploadReceiptsDto")
                : UploadReceiptsDto.builder().build();
        map.addAttribute("uploadReceiptsDto", uploadReceiptsDto);
        map.addAttribute("currentTotalAmount", uploadReceiptsService.getCurrentTotalAmount());
		getUploadReceiptsList(map, request, form);

        String logTag = (String) session.getAttribute(commonResponseUtil.getMessage("upload.log.tag"));
        // If no upload in progress, create a new idle tag
        if (logTag == null) {
            logTag = "uploadReceipt-" + UUID.randomUUID();
            session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), logTag);
        }
        map.put("logTag", logTag);

        return HTMLPage.UPLOAD_RECEIPTS;
    }

	private void getUploadReceiptsList(ModelMap map, HttpServletRequest request, PaginationForm form) {
		Page<UploadReceiptsRecord> uploadReceiptsRecords = uploadReceiptsService.getUploadReceipts(form);
        map.addAttribute("accHeadList", accountHeadService.getAccountHeadListByFinYear());
        map.addAttribute("bankList", accountHeadService.getBankList(List.of("B","C")));
        map.addAttribute("fromDate", form.getAdditionalParam().get("fromDate"));
        map.addAttribute("toDate", form.getAdditionalParam().get("toDate"));
        map.addAttribute("bookType", form.getAdditionalParam().get("bookType"));
        map.addAttribute("receiptTypeList", form.getAdditionalParam().get("receiptTypeList"));
		commonResponseUtil.updateCommonModelAttributes(map, request, uploadReceiptsRecords, form);
	}
    
    @GetMapping(value = "${url.template.download}")
    public void downloadExcelTemplate(@RequestParam String receiptType, HttpServletResponse response) {
        try (Workbook workbook = uploadReceiptsService.downloadExcelTemplate(receiptType);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(bos);
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(ExcelConstants.CONTENT_TYPE);
            response.setHeader(ExcelConstants.CONTENT_DISPOSITION, getFileName(receiptType));
            response.setContentLength(excelBytes.length);
            outputStream.write(excelBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.getMessage();
        }
    }
    private String getFileName(String receiptType) {
        return switch (receiptType) {
            case "fee" -> ExcelConstants.IFPP_RECEIPT;
            case "loan" -> ExcelConstants.LOAN_RECEIPT;
            case "subsidy" -> ExcelConstants.SUBSIDY_RECEIPT;
            case "scholarship" -> ExcelConstants.SCHOLARSHIP;
            case "mess" -> ExcelConstants.MESS_BILLING;
            case "rebate" -> ExcelConstants.MESS_REBATE;
            case "dayScholar" -> ExcelConstants.DAYS_SCHOLAR;
            default -> throw new IllegalStateException("Unexpected value: " + receiptType);
        };
    }

	@PostMapping(value = "${url.upload}")
	public String saveUploadReceiptsBulkUpload(@ModelAttribute UploadReceiptsDto uploadReceiptsDto,
                                               BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response,
                                               RedirectAttributes redirectAttrs, ModelMap map, PaginationForm form, HttpSession session) throws Exception {
		validator.validate(uploadReceiptsDto, bindingResult);
		if (bindingResult.hasErrors()) {
			getUploadReceiptsList(map, request, form);
			return HTMLPage.UPLOAD_RECEIPTS;
		}

        if (uploadReceiptsDto.getFile().isEmpty()) {
            redirectAttrs.addFlashAttribute(StudentConstants.EXCEL_ERROR_LIST.getStudentConstant(),
                    commonResponseUtil.getMessage("message.label.choose.file.to.upload"));
            return Constants.REDIRECT + baseUrl;
        }
        commonResponseUtil.updateCommonModelAttributes(map, request);
        //uploadReceiptsDto = uploadReceiptsService.saveUploadReceiptsBulkUpload(uploadReceiptsDto);
//        if (uploadReceiptsDto.getErrorList() == null|| uploadReceiptsDto.getErrorList().isEmpty()) {
//            Object[] args = { uploadReceiptsDto.getStudentCount(), uploadReceiptsDto.getAmount() };
//            commonResponseUtil.updateSaveResponseByStatus(Constants.SAVED, redirectAttrs,"message.upload.receipt.details.save");
//        } else {
//            redirectAttrs.addFlashAttribute("uploadReceiptsDto", uploadReceiptsDto);
//        }

        // 🔥 START ASYNC PROCESS (returns immediately)
        String tag = "uploadReceipt-" + UUID.randomUUID();
        session.setAttribute(commonResponseUtil.getMessage("upload.log.tag"), tag);
        uploadReceiptsDto.setLogTag(tag);

        byte[] fileBytes = uploadReceiptsDto.getFile().getBytes();
        uploadReceiptsDto.setFileBytes(fileBytes);
        uploadReceiptsDto = uploadReceiptsService.startBulkUpload(uploadReceiptsDto);
        redirectAttrs.addFlashAttribute("uploadReceiptsDto", uploadReceiptsDto);
        commonResponseUtil.updateSaveResponseByStatus("message", redirectAttrs,"message.upload.process.started");

        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping("${url.excel.report.download}")
    public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        commonResponseUtil.getAdditionalParams(allParams, form);
        form.setPage(1);
        form.setSize(Integer.MAX_VALUE);
        List<UploadReceiptsRecord> uploadReceiptsRecords = uploadReceiptsService.getUploadReceipts(form).getContent();
        try {
            Workbook workbook = uploadReceiptsService.getUploadReceiptReport(uploadReceiptsRecords);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = messageSource.getMessage("message.upload.receipt.file.name", null, Locale.getDefault()) + "" + FileUploadConstants.XLSX_EXTENSION;
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage("message.attachment.filename", new Object[] { fileName }, Locale.getDefault()));
            response.setContentLength(excelBytes.length);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @InitBinder("uploadReceiptsDto")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("rowNumber", "columnIndexMap");
    }
}
