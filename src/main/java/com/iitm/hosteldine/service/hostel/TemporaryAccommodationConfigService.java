package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.TemporaryAccommodationConfigDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.TemporaryAccommodationConfigMapper;
import com.iitm.hosteldine.model.hostel.TemporaryAccommodationConfigEntity;
import com.iitm.hosteldine.repository.hostel.TemporaryAccommodationConfigRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemporaryAccommodationConfigService {

    private final TemporaryAccommodationConfigRepository temporaryAccommodationConfigRepository;
    private final CustomValidators customValidators;
    private final CommonResponseUtil commonResponseUtil;
    private final Utility utility;

    public Page<TemporaryAccommodationConfigDto> getTemporaryAccConfigList(PaginationForm form){
        var pageable = PageRequest.of(form.getPage()-1, form.getSize(), Sort.by("modifiedAt").descending());
        return temporaryAccommodationConfigRepository.getAllByFilter(ModelConstants.STATUS_ACTIVE, form.getSearch(), pageable)
                .map(TemporaryAccommodationConfigMapper.INSTANCE::toDto);
    }

    public TemporaryAccommodationConfigDto getById(Long id){
        return temporaryAccommodationConfigRepository.findByIdAndActiveFlag(id,ModelConstants.STATUS_ACTIVE)
                .map(TemporaryAccommodationConfigMapper.INSTANCE::toDto)
                .orElse(TemporaryAccommodationConfigDto.builder().build());
    }

    public void validateForm(TemporaryAccommodationConfigDto dto, BindingResult result){
        if (customValidators.isNullOrEmpty(dto.getCategoryName())) {
            customValidators.rejectField(result, "categoryName", "message.validation.category.name.required");
        }

        if(customValidators.isNullOrEmpty(dto.getDescription())) {
            customValidators.rejectField(result, "description", "message.validation.description.required");
        }

        if(customValidators.isNullOrEmpty(dto.getAccStayAmnt()) || dto.getAccStayAmnt() <= 0){
            customValidators.rejectField(result, "accStayAmnt", "message.label.hostel.rent.required");
        }

        if(customValidators.isNullOrEmpty(dto.getBreakfastCoupon()) || dto.getBreakfastCoupon() <= 0){
            customValidators.rejectField(result, "breakfastCoupon", "message.label.breakfast.amount.required");
        }

        if(customValidators.isNullOrEmpty(dto.getLunchCoupon()) || dto.getLunchCoupon() <= 0){
            customValidators.rejectField(result, "lunchCoupon", "message.label.lunch.amount.required");
        }

        if(customValidators.isNullOrEmpty(dto.getDinnerCoupon()) || dto.getDinnerCoupon() <= 0){
            customValidators.rejectField(result, "dinnerCoupon", "message.label.dinner.amount.required");
        }

        if(customValidators.isNullOrEmpty(dto.getEffectiveDate())){
            customValidators.rejectField(result, "effectiveDate", "message.label.effective.date.required");
        }

        if(customValidators.isNullOrEmpty(dto.getRebateCharges()) || dto.getRebateCharges() <= 0){
            customValidators.rejectField(result, "rebateCharges", "message.label.rebate.charges.required");
        }
    }

    public String saveOrUpdate(TemporaryAccommodationConfigDto dto){
        return temporaryAccommodationConfigRepository.findByIdAndActiveFlag(dto.getId(), ModelConstants.STATUS_ACTIVE)
                .map(e -> updateAccomConfig(e, dto))
                .orElseGet(() ->saveAccomConfig(dto));
    }

    public String updateAccomConfig(TemporaryAccommodationConfigEntity entity, TemporaryAccommodationConfigDto dto){
        TemporaryAccommodationConfigMapper.INSTANCE.updateEntity(entity,dto);
        entity.onUpdate();
        temporaryAccommodationConfigRepository.saveAndFlush(entity);
        return Constants.UPDATED;
    }

    public String saveAccomConfig(TemporaryAccommodationConfigDto dto){
        TemporaryAccommodationConfigEntity entity = TemporaryAccommodationConfigMapper.INSTANCE.toEntity(dto);
        entity.onCreate();
        temporaryAccommodationConfigRepository.saveAndFlush(entity);
        return Constants.SAVED;
    }

    public boolean processDelete(Long id) throws RecordNotExistsException {
        return temporaryAccommodationConfigRepository.findByIdAndActiveFlag(id,ModelConstants.STATUS_ACTIVE)
                .map(this::deleteAccom)
                .orElseThrow(() -> new RecordNotExistsException(commonResponseUtil.getMessage("validation.error.id.not.found")));
    }

    public boolean deleteAccom(TemporaryAccommodationConfigEntity entity){
        entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        entity.onUpdate();
        temporaryAccommodationConfigRepository.save(entity);
        return true;
    }

    public Workbook generateTempAccomConfigListExcelReport() throws Exception {

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<TemporaryAccommodationConfigDto> accomCategoryConfigList = temporaryAccommodationConfigRepository.getAllByFilter(ModelConstants.STATUS_ACTIVE, null, pageable)
                .map(TemporaryAccommodationConfigMapper.INSTANCE::toDto)
                .getContent()
                .stream().sorted(Comparator.comparing(TemporaryAccommodationConfigDto::getEffectiveDate))
                .toList();

        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.TEMP_ACCOM_CONFIG_ISSUED_HEADER;

        try {

            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.temp.accom.config.report"));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);
            headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            // Create report header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage("message.temp.accom.config.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();
            excelUtility.createCell(rowheadthird, 0, commonResponseUtil.getMessage("message.label.report.date") + dateFormat.format(date), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 7));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(4);

            // Add headers to the sheet
            for (String p : headerList) {
                excelUtility.createCell(rowhead, colCount, p, headerStyle);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowCount = 4;
            if (CollectionUtils.isNotEmpty(accomCategoryConfigList)) {
                for (TemporaryAccommodationConfigDto dto : accomCategoryConfigList) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, dto.getCategoryName(), dataStyle);
                    excelUtility.createCell(row, 1, dto.getDescription(), dataStyle);
                    excelUtility.createCell(row, 2, utility.formatCommaSeperatedCurrency(Double.parseDouble(dto.getAccStayAmnt().toString())),
                            dataStyle);
                    excelUtility.createCell(row, 3, utility.formatCommaSeperatedCurrency(Double.parseDouble(dto.getBreakfastCoupon().toString())), dataStyle);
                    excelUtility.createCell(row, 4, utility.formatCommaSeperatedCurrency(Double.parseDouble(dto.getLunchCoupon().toString())), dataStyle);
                    excelUtility.createCell(row, 5, utility.formatCommaSeperatedCurrency(Double.parseDouble(dto.getDinnerCoupon().toString())), dataStyle);
                    excelUtility.createCell(row, 6, utility.dateFormatter(dto.getEffectiveDate()), dataStyle);
                    excelUtility.createCell(row, 7, utility.formatCommaSeperatedCurrency(Double.parseDouble(dto.getRebateCharges().toString())), dataStyle);
                }
            }

            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headerList.length; i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 10000) {
                    sheet.setColumnWidth(i, 10000);
                }
            }


        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating Temporary Accommodation Config List report", exception);
        }
        return workbook;
    }

}
