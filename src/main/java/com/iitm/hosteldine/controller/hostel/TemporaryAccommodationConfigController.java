package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.TemporaryAccommodationConfigDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.TemporaryAccommodationConfigService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
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
import java.util.Locale;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.temporary.accommodation.config}")
@RequiredArgsConstructor
public class TemporaryAccommodationConfigController {

    private final TemporaryAccommodationConfigService temporaryAccommodationConfigService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;

    @Value("${url.temporary.accommodation.config}")
    private String baseUrl;

    @GetMapping
    public String getTemporaryAccommodationConfig(PaginationForm form, ModelMap model, HttpServletRequest request) {
        Page<TemporaryAccommodationConfigDto> temporaryAccConfigList = temporaryAccommodationConfigService.getTemporaryAccConfigList(form);
        commonResponseUtil.updateCommonModelAttributes(model, request, temporaryAccConfigList, form);
        setExportButton(model, baseUrl.replace("/", ""));
        return HTMLPage.TEMPORARY_ACCOMMODATION_CONFIG_LIST;
    }

    @GetMapping(value = "${id}")
    public String getAddOrUpdateModal(@PathVariable("id") Long id, ModelMap model, HttpServletRequest request) {
        String formKey = "temporaryAccommodationConfigDto";
        TemporaryAccommodationConfigDto temporaryAccommodationConfigDto = commonResponseUtil.handleModalFormError(request, model,
                formKey, TemporaryAccommodationConfigDto.class);
        if (Objects.nonNull(id) && id > 0) {
            temporaryAccommodationConfigDto = temporaryAccommodationConfigService.getById(id);
        }
        model.addAttribute(formKey, temporaryAccommodationConfigDto);
        return HTMLPage.TEMPORARY_ACCOMMODATION_CONFIG_MODAL;
    }

    @PostMapping
    public String saveOrUpdateAccommodationConfig(@ModelAttribute TemporaryAccommodationConfigDto temporaryAccommodationConfigDto,
                                                  BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        temporaryAccommodationConfigService.validateForm(temporaryAccommodationConfigDto, bindingResult);
        if (bindingResult.hasErrors()) {
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, temporaryAccommodationConfigDto);
            return Constants.REDIRECT + baseUrl;
        }

        String status = temporaryAccommodationConfigService.saveOrUpdate(temporaryAccommodationConfigDto);
        String message = status.equalsIgnoreCase(Constants.SAVED) ? "message.temporary.accom.config.save" : "message.temporary.accom.config.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        return Constants.REDIRECT + baseUrl;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteAccomConfig(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(temporaryAccommodationConfigService.processDelete(id));
        } catch (RecordNotExistsException e) {
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }

    @GetMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestExcelReport(HttpServletResponse response) {
        try {
            Workbook workbook = temporaryAccommodationConfigService.generateTempAccomConfigListExcelReport();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = commonResponseUtil.getMessage("message.temp.accom.config.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage("message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
            response.setContentLength(excelBytes.length);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setExportButton(ModelMap map, String splitBaseUrl) {
        HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
        if (headerForm != null) {
            headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK, commonResponseUtil.getMessage("message.excel.report"),
                    ModelConstants.FA_FILE_EXCEL);
            map.addAttribute("excelUrl", splitBaseUrl + commonResponseUtil.getMessage("url.dean.approval.excel.report.download"));
        }
    }
}