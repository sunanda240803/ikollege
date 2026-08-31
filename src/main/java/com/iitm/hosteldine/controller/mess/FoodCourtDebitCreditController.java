package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.mess.FoodCourtLedgerDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDropdownDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.mess.FoodCourtDebitCreditService;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.food.court.credit.debit}")
public class FoodCourtDebitCreditController {

    private final CommonResponseUtil commonResponseUtil;
    private final FoodCourtDebitCreditService foodCourtDebitCreditService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final MessageSource messageSource;
    private final MessMasterCommonService messMasterCommonService;
    private final MessMasterService messMasterService;
    private final MessAllottedListService messAllottedListService;

    @Value("${url.food.court.credit.debit}")
    private String baseUrl;

    @GetMapping
    public String getFoodCourtDebitCreditList(@RequestParam(required = false) Long id, ModelMap map, HttpServletRequest request, PaginationForm form) {
        long selectedPeriodId = (id != null) ? id : 0L;
        List<FoodCourtLedgerDto> foodCourtList = foodCourtDebitCreditService.getDebitCreditList(selectedPeriodId);
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        List<MessMasterControllerDto> messPeriodList = messAllottedListService.getMessPeriodList();
        List<MessMasterControllerDropdownDto> messPeriods = messPeriodList.stream()
                .filter(d -> d.getDiningFromDate() != null && d.getDiningToDate() != null)
                .map(d -> new MessMasterControllerDropdownDto(
                        DateUtility.formatDate(d.getDiningFromDate()) + " to " + DateUtility.formatDate(d.getDiningToDate()), d.getId()))
                .toList();
        commonResponseUtil.updateCommonModelAttributes2(map, request, foodCourtList, form);
        map.addAttribute("foodCourtLedgerDto",
                flashInputMap != null && flashInputMap.get("foodCourtLedgerDto") != null
                        ? flashInputMap.get("foodCourtLedgerDto")
                        : new StudentBulkInfoDto());
        map.addAttribute("messPeriods", messPeriods);
        map.addAttribute("id", id);
        map.addAttribute("USE_DATATABLES", true);
        return HTMLPage.FOOD_COURT_CREDIT_DEBIT;
    }

    @PostMapping(value = "${url.save.food.court.list}")
    public ResponseEntity<String> saveFoodCourtList(@RequestParam(required = false) Long messPeriod) {
        String result = foodCourtDebitCreditService.saveFoodCourtList(messPeriod);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "${url.get.form}")
    public String getTopUpAmountForm(ModelMap map, HttpServletRequest request){
        FoodCourtLedgerDto foodCourtLedgerDto = commonResponseUtil.handleModalFormError(request , map, "foodCourtLedgerDto", FoodCourtLedgerDto.class);
        map.addAttribute("foodCourtLedgerDto", foodCourtLedgerDto);
        map.addAttribute("currentMessPeriod", getCurrentMessPeriod());
        return HTMLPage.TOP_UP_AMOUNT_MODAL;
    }

    @GetMapping(value = "${url.exist}")
    public ResponseEntity<StudentDetailsInfoDto> checkStudentExist(@RequestParam String studentId) {
        StudentDetailsInfoDto studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(studentId.toUpperCase());
        FoodCourtLedgerDto foodCourtLedgerDto = foodCourtDebitCreditService.getStudentLedgerDetails(studentId.toUpperCase());
        studentDetailsInfoDto.setFoodCourtLedgerDto(foodCourtLedgerDto);
        return ResponseEntity.ok(studentDetailsInfoDto);
    }

    @PostMapping(value = "${url.save}")
    public String saveFoodCourtTopUpAmount(@Valid @ModelAttribute FoodCourtLedgerDto foodCourtLedgerDto, BindingResult bindingResult,
                                                RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult,foodCourtLedgerDto);
            return Constants.REDIRECT + baseUrl;
        }
        String status = foodCourtDebitCreditService.saveFoodCourtTopUpAmount(foodCourtLedgerDto);
        commonResponseUtil.updateResponse(status.equals(Constants.UPDATED) ? status : null, redirectAttributes, "message.label.food.court.top.up.update", "message.validation.error.something.went.wrong");
        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "${url.template.download}")
    public void downloadExcelTemplate(HttpServletResponse response) {
        try (Workbook workbook = foodCourtDebitCreditService.downloadExcelTemplate();
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(bos);
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(ExcelConstants.CONTENT_TYPE);
            response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.FOOD_COURT_DEBIT_FILENAME);
            response.setContentLength(excelBytes.length);
            outputStream.write(excelBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @PostMapping(value = "${url.upload}")
    public String saveFoodCourtDebitBulkUpload(@Valid @ModelAttribute FoodCourtLedgerDto foodCourtLedgerDto,
                                               RedirectAttributes redirectAttributes) {
        if (foodCourtLedgerDto.getFile().isEmpty()) {
            redirectAttributes.addFlashAttribute(StudentConstants.EXCEL_ERROR_LIST.getStudentConstant(),
                    messageSource.getMessage("message.label.choose.file.to.upload", null, Locale.getDefault()));
            return Constants.REDIRECT + baseUrl;
        }
        FoodCourtLedgerDto foodCourtBulkDetails = foodCourtDebitCreditService.saveFoodCourtBulkDetails(foodCourtLedgerDto.getFile());
        if (foodCourtBulkDetails.getErrorList() == null) {
            commonResponseUtil.updateSaveResponseByStatus(Constants.SAVED, redirectAttributes,"message.label.food.court.details.save");
        } else {
            redirectAttributes.addFlashAttribute(StudentConstants.EXCEL_ERROR_LIST.getStudentConstant(), foodCourtBulkDetails.getErrorList());
        }
        return Constants.REDIRECT + baseUrl;
    }

    private MessMasterControllerDto getCurrentMessPeriod() {
        return messMasterCommonService.getCurrentMessPeriod();
    }
}
