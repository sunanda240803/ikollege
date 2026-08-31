package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.FoodCourtTransactionDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDropdownDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.form.common.FoodCourtPurchaseForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mess.FoodCourtDebitCreditService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.util.BeanDefinitionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "${url.food.court.purchase}")
@RequiredArgsConstructor
public class FoodCourtPurchaseController {

    private final FoodCourtDebitCreditService foodCourtDebitCreditService;
    private final CommonResponseUtil commonResponseUtil;
    private final  MessageSource messageSource;
    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterRepository messMasterRepository;
    private final SimsConfigDataService simsConfigDataService;

    @Value("${url.food.court.purchase}")
    private String baseUrl;

    @GetMapping
    public String getFoodCourtReport( PaginationForm form, ModelMap model ,HttpServletRequest request) {
        List<MessMasterControllerEntity> allByActiveFlagOrderByModifiedAtDesc = messMasterControllerRepository.findAllByActiveFlagOrderByIdDesc(ModelConstants.STATUS_ACTIVE);
        List<MessMasterControllerDropdownDto> messPeriods = allByActiveFlagOrderByModifiedAtDesc.stream()
                .filter(d -> d.getDiningFromDate() != null && d.getDiningToDate() != null)
                .map(d -> new MessMasterControllerDropdownDto(
                        DateUtility.formatDate(d.getDiningFromDate()) + " to " + DateUtility.formatDate(d.getDiningToDate()), d.getId()))
                .collect(Collectors.toList());


        String food_court = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FOOD_COURT);

        List<MessMasterEntity> foodCourtMessMasterList = messMasterRepository.findAllByActiveFlagAndDescriptionOrderByMessNameAsc(ModelConstants.STATUS_ACTIVE, food_court);
        List<MessMasterDto> messNames = foodCourtMessMasterList.stream().map(d -> new MessMasterDto(d.getId(), d.getMessName()))
                .collect(Collectors.toList());

        model.addAttribute("messPeriods", messPeriods);
        model.addAttribute("messNames", messNames);
        model.addAttribute("searchForm", new FoodCourtPurchaseForm());
        model.addAttribute("showTable", false);
        commonResponseUtil.updateCommonModelAttributes(model, request ,null , form);

        return HTMLPage.FOOD_COURT_PURCHASE;
    }

    @PostMapping
    public String getFoodCourtReport(@ModelAttribute("searchForm") FoodCourtPurchaseForm searchForm, PaginationForm form, ModelMap model, HttpServletRequest request) {

        List<MessMasterControllerEntity> allByActiveFlagOrderByModifiedAtDesc = messMasterControllerRepository.findAllByActiveFlagOrderByIdDesc(ModelConstants.STATUS_ACTIVE);
        List<MessMasterControllerDropdownDto> messPeriods = allByActiveFlagOrderByModifiedAtDesc.stream()
                .filter(d -> d.getDiningFromDate() != null && d.getDiningToDate() != null)
                .map(d -> new MessMasterControllerDropdownDto(
                        DateUtility.formatDate(d.getDiningFromDate()) + " to " + DateUtility.formatDate(d.getDiningToDate()), d.getId()))
                .collect(Collectors.toList());
        String food_court = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FOOD_COURT);


        List<MessMasterEntity> foodCourtMessMasterList = messMasterRepository.findAllByActiveFlagAndDescriptionOrderByMessNameAsc(ModelConstants.STATUS_ACTIVE, food_court);
        List<MessMasterDto> messNames = foodCourtMessMasterList.stream().map(d -> new MessMasterDto(d.getId(), d.getMessName()))
                .collect(Collectors.toList());

        model.addAttribute("messPeriods", messPeriods);
        model.addAttribute("messNames", messNames);
        model.addAttribute("searchForm", searchForm);
        Page<FoodCourtTransactionDto> foodCourtTransactions = foodCourtDebitCreditService.getFoodCourtTransactions(form, searchForm);

        /* This is to get the total credit and debit amount from all the record */
        PaginationForm paginationForm = new PaginationForm();
        BeanUtils.copyProperties(form, paginationForm);
        paginationForm.setPage(1);
        paginationForm.setSize(Integer.MAX_VALUE);
        Page<FoodCourtTransactionDto> foodCourtTransactionDtoList = foodCourtDebitCreditService.getFoodCourtTransactions(paginationForm, searchForm);

        double totalCredit = foodCourtTransactionDtoList.getContent().stream()
                .filter(t -> "c".equalsIgnoreCase(t.getDebitOrCredit()))
                .map(FoodCourtTransactionDto::getAmount)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalDebit = foodCourtTransactionDtoList.getContent().stream()
                .filter(t -> "d".equalsIgnoreCase(t.getDebitOrCredit()))
                .map(FoodCourtTransactionDto::getAmount)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
        model.addAttribute("totalCredit", totalCredit);
        model.addAttribute("totalDebit", totalDebit);

        model.addAttribute("showTable", true);

        commonResponseUtil.updateCommonModelAttributes(model, request,foodCourtTransactions,form);

        return HTMLPage.FOOD_COURT_PURCHASE;
    }

    @PostMapping("${url.dean.approval.excel.report.download}")
    public void downloadRequestReport(@RequestBody FoodCourtPurchaseForm searchForm, @RequestParam Map<String, String> allParams,@ModelAttribute PaginationForm pageForm, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        //Setting dynamic filter values
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
        pageForm.setPage(1);
        pageForm.setSize(Integer.MAX_VALUE);
        Page<FoodCourtTransactionDto> foodCourtTransactions = foodCourtDebitCreditService.getFoodCourtTransactions(pageForm, searchForm);

        try {
            Workbook workbook = foodCourtDebitCreditService.getFoodCourtTransactionsReport(foodCourtTransactions.getContent());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = messageSource.getMessage("message.mess.food.court.purchase.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
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
