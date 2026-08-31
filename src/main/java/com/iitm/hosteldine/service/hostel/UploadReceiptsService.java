package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.UploadReceiptsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UploadReceiptsService {
    private final MessLedgerARepository messLedgerARepository;
    private final CommonResponseUtil commonResponseUtil;
    private final ExcelUtility excelUtility;
    private final UploadReceiptsHelper uploadReceiptsHelper;
    private final HostelMasterService hostelMasterService;
    private final HostelRoomInfoService hostelRoomInfoService;
    private final InMemoryLogService logService;
    private final BulkAsyncExecutor bulkAsyncExecutor;
    private final MessageSource messageSource;
    private final Utility utility;

    public Page<UploadReceiptsRecord> getUploadReceipts(PaginationForm form) {
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        LocalDate fromDate = Optional.ofNullable(form.getAdditionalParam().get("fromDate"))
                .map(Object::toString)
                .filter(dt -> !dt.isBlank())
                .map(LocalDate::parse)
                .orElse(null);
        LocalDate toDate = Optional.ofNullable(form.getAdditionalParam().get("toDate"))
                .map(Object::toString)
                .filter(dt -> !dt.isBlank())
                .map(LocalDate::parse)
                .orElse(null);
        String bookType = Optional.ofNullable(form.getAdditionalParam().get("bookType"))
                .map(Object::toString)
                .filter(type -> !Constants.MESS.equalsIgnoreCase(type))
                .map(type -> Constants.CREDIT_CARD)
                .orElse(Constants.MESS_MS);
        String receiptType = Optional.ofNullable(form.getAdditionalParam().get("receiptTypeList"))
                .map(Object::toString)
                .filter(rt -> !rt.isBlank())
                .orElse(ModelConstants.EMPTY_STRING);
        return Optional.ofNullable(fromDate)
                .flatMap(fd -> Optional.ofNullable(toDate)
                        .map(td -> messLedgerARepository.getUploadReceiptsRecordsByDateRange(
                                pageable,
                                ModelConstants.STATUS_ACTIVE,
                                ModelConstants.STATUS_INACTIVE,
                                fd,
                                td,
                                bookType,
                                receiptType
                        ))
                ).orElseGet(() -> messLedgerARepository.getUploadReceiptsRecordsByDefaultDate(
                        pageable,
                        ModelConstants.STATUS_ACTIVE,
                        ModelConstants.STATUS_INACTIVE,
                        LocalDate.now().minusDays(5),
                        Constants.MESS_MS,
                        receiptType
                ));
    }

    public Workbook downloadExcelTemplate(String receiptType) {
        String sheetName = uploadReceiptsHelper.getFileName(receiptType);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        String[] headerData = uploadReceiptsHelper.getHeaderDate(receiptType);
        String[] headerDataWidth = uploadReceiptsHelper.getHeaderDataWidth(receiptType);
        excelUtility.setDataStyle(workbook);
        Row headerRow = sheet.createRow(0);
        excelUtility.createHeader(headerRow, 0, headerData, workbook);
        IntStream.range(0, headerDataWidth.length)
                .forEach(i -> sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i])));
        if ("loan".equalsIgnoreCase(receiptType)){
            XSSFSheet hiddenSheet = workbook.createSheet("DropdownData");
            workbook.setSheetHidden(workbook.getSheetIndex("DropdownData"), true);
            List<String> hostelList = hostelMasterService.getHostelList().stream().map(HostelMasterDto::getHostelName).toList();
            List<String> roomNumbers = hostelRoomInfoService.getRoomNoList();
            excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 3, roomNumbers);
            excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 4, hostelList);
            ExcelUtility.addDropdownToColumn(sheet, 1, 100, 3, "DropdownData!$D$1:$D$" + roomNumbers.size());
            ExcelUtility.addDropdownToColumn(sheet, 1, 2500, 4, "DropdownData!$E$1:$E$" + hostelList.size());
        }
        return workbook;
    }

//    @Transactional(rollbackFor = Exception.class)
//    public UploadReceiptsDto saveUploadReceiptsBulkUpload(UploadReceiptsDto uploadReceiptsDtoParam) throws Exception{
//        UploadReceiptsDto uploadReceiptsDto = UploadReceiptsDto.builder().build();
//        try {
//            addLog(tag, 0, "Validation Started: ");
//            List<UploadReceiptsDto> uploadReceiptsDtoList = uploadReceiptsHelper.parseExcelFile(uploadReceiptsDtoParam);
//            ArrayList<String> allErrors = uploadReceiptsDtoList.stream()
//                    .filter(dto -> dto.getErrorList() != null && !dto.getErrorList().isEmpty())
//                    .flatMap(dto -> dto.getErrorList().stream())
//                    .distinct()
//                    .collect(Collectors.toCollection(ArrayList::new));
//            System.out.println("ERROR LIST SIZE: "+ allErrors.size());
//            if (!allErrors.isEmpty()) {
//                addLog(tag, 0, "Validation Errors: ");
//                uploadReceiptsDto.setErrorList(allErrors);
//                return uploadReceiptsDto;
//            }
//            System.out.println("Before save-----");
//            addLog(tag, 0, "Validation Done");
//            uploadReceiptsHelper.saveExcelFile(uploadReceiptsDtoList, uploadReceiptsDtoParam,tag);
//        } catch (Exception e) {
//            e.printStackTrace();
//            ArrayList<String> errorList = new ArrayList<>();
//            errorList.add(commonResponseUtil.getMessage("message.validation.error.invalid.file.type"));
//            uploadReceiptsDto.setErrorList(errorList);
//        }
//        return uploadReceiptsDto;
//    }

    public UploadReceiptsDto startBulkUpload(UploadReceiptsDto uploadReceiptsDto) {

        addLog(uploadReceiptsDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.started"));

        // 🔥 Run heavy process in background
        bulkAsyncExecutor.execute(uploadReceiptsDto.getLogTag(), () -> {
            saveUploadReceiptsBulkUpload(uploadReceiptsDto);
        });

        return uploadReceiptsDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUploadReceiptsBulkUpload(UploadReceiptsDto uploadReceiptsDto) {
        String tag=uploadReceiptsDto.getLogTag();
        try {
            addLog(tag, 0, commonResponseUtil.getMessage("upload.validation.start"));

            List<UploadReceiptsDto> uploadReceiptsDtoList =
                    uploadReceiptsHelper.parseExcelFile(uploadReceiptsDto);

            ArrayList<String> allErrors = uploadReceiptsDtoList.stream()
                    .filter(dto -> dto.getErrorList() != null && !dto.getErrorList().isEmpty())
                    .flatMap(dto -> dto.getErrorList().stream())
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));

            System.out.println("ERROR LIST SIZE: "+ allErrors.size());
            if (!allErrors.isEmpty()) {
                addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
                allErrors.forEach(err -> addValidation(tag, 0, err));
                return;
            }
            addLog(tag, 0, commonResponseUtil.getMessage("upload.validation.done"));

            addLog(tag, 0, commonResponseUtil.getMessage("upload.save.started"));
            uploadReceiptsHelper.saveExcelFile(uploadReceiptsDtoList,uploadReceiptsDto,tag);

            addLog(tag, 0, commonResponseUtil.getMessage("upload.success"));

        } catch (Exception e) {
            addError(tag, 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
        }
    }

    public String getCurrentTotalAmount() {
        return utility.formatCommaSeperatedCurrency(messLedgerARepository.getCurrentDateTotalAmount(
                ModelConstants.STATUS_ACTIVE,
                commonResponseUtil.getMessage("message.label.towards.i.collect.fee.note"),
                ModelConstants.STATUS_INACTIVE
        ).orElse(0.0));
    }

    private void addLog(String tag, long sessionId, String msg) {
        logService.addLog(tag, " <-" + sessionId + "-> " + msg);
    }

    private void addError(String tag, long sessionId, String msg,Exception e) {
        logService.addError(tag, " <-" + sessionId + "-> " + msg,e);
    }

    private void addValidation(String tag, long sessionId, String msg) {
        logService.addValidation(tag, " <-" + sessionId + "-> " + msg);
    }

    public Workbook getUploadReceiptReport(List<UploadReceiptsRecord> uploadReceiptsRecords) throws Exception {
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.upload.receipt", null, Locale.getDefault()));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.upload.receipt", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));

            // Create third header row for report date
            XSSFRow rowheadSecond = sheet.createRow(2);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(3);
            String[] headers = {
                    messageSource.getMessage("message.label.mess.rebate.sl.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.ref.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.transaction.checker.approval.account.head", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.description", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.voucher.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.amount", null, Locale.getDefault())
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 6000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowcount = 3;
            int count = 1;
            if (CollectionUtils.isNotEmpty(uploadReceiptsRecords)) {
                for (UploadReceiptsRecord uploadReceiptsRecord : uploadReceiptsRecords) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);
                    Cell dateCell = row.createCell(5);
                    excelUtility.createCell(row, 0, count++, dataStyle);
                    excelUtility.createCell(row, 1, uploadReceiptsRecord.docRefNo() != null && !uploadReceiptsRecord.docRefNo().isEmpty() ?
                            uploadReceiptsRecord.docRefNo() : ModelConstants.NOT_APPLICABLE, dataStyle);
                    excelUtility.createCell(row, 2, uploadReceiptsRecord.accHead(), dataStyle);
                    excelUtility.createCell(row, 3, uploadReceiptsRecord.studentName(), dataStyle);
                    excelUtility.createCell(row, 4, uploadReceiptsRecord.description(), dataStyle);

                    if (uploadReceiptsRecord.voucherDate() != null) {
                        dateCell.setCellValue(java.sql.Date.valueOf(uploadReceiptsRecord.voucherDate()));
                    }
                    CellStyle dateStyle = workbook.createCellStyle();
                    CreationHelper createHelper = workbook.getCreationHelper();
                    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(Constants.FRONTEND_DATE_FORMAT));
                    dateCell.setCellStyle(dateStyle);
                    excelUtility.createCell(row, 6, uploadReceiptsRecord.amount() != null ?
                            String.format("%.2f", uploadReceiptsRecord.amount()) : ModelConstants.NOT_APPLICABLE, dataStyle);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating Mess Rebate List report", exception);
        }
        return workbook;
    }
}
