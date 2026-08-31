package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.UploadReceiptsDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.form.common.StudentDebitForm;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.*;
import com.iitm.hosteldine.service.student.StudentWithRemarksService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UploadReceiptsHelper {
    private final CommonResponseUtil commonResponseUtil;
    private final StudentWithRemarksService studentWithRemarksService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final MessLedgerBRepository messLedgerBRepository;
    private final AccountHeadService accountHeadService;
    private final MessLedgerARepository messLedgerARepository;
    private final AuditTrailService auditTrailService;
    private final SimsConfigDataService simsConfigDataService;
    private final TransferAmountUtils transferAmountUtils;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final AllStudentsDetailsViewService allStudentsDetailsViewService;
    private final InMemoryLogService logService;

    /* Get Header Details */
    public String[] getHeaderDataWidth(String receiptType) {
        return switch (receiptType) {
            case "fee" -> ExcelConstants.IFPP_RECEIPT_DATA_WIDTH;
            case "loan" -> ExcelConstants.LOAN_RECEIPT_DATA_WIDTH;
            case "subsidy" -> ExcelConstants.SUBSIDY_RECEIPT_DATA_WIDTH;
            case "scholarship" -> ExcelConstants.SCHOLARSHIP_DATA_WIDTH;
            case "mess" -> ExcelConstants.MESS_BILLING_DATA_WIDTH;
            case "rebate" -> ExcelConstants.MESS_REBATE_DATA_WIDTH;
            case "dayScholar" -> ExcelConstants.DAYS_SCHOLAR_DATA_WIDTH;
            default -> throw new IllegalStateException("Unexpected value: " + receiptType);
        };
    }

    public String[] getHeaderDate(String receiptType) {
        return switch (receiptType) {
            case "fee" -> ExcelConstants.IFPP_RECEIPT_DATA;
            case "loan" -> ExcelConstants.LOAN_RECEIPT_DATA;
            case "subsidy" -> ExcelConstants.SUBSIDY_RECEIPT_DATA;
            case "scholarship" -> ExcelConstants.SCHOLARSHIP_DATA;
            case "mess" -> ExcelConstants.MESS_BILLING_DATA;
            case "rebate" -> ExcelConstants.MESS_REBATE_DATA;
            case "dayScholar" -> ExcelConstants.DAYS_SCHOLAR_DATA;
            default -> throw new IllegalStateException("Unexpected value: " + receiptType);
        };
    }

    public String getFileName(String receiptType) {
        return switch (receiptType) {
            case "fee" -> commonResponseUtil.getMessage("message.label.fee.receipt");
            case "loan" -> commonResponseUtil.getMessage("message.label.loan.receipt");
            case "subsidy" -> commonResponseUtil.getMessage("message.label.subsidy.receipt");
            case "scholarship" -> commonResponseUtil.getMessage("message.label.scholarship");
            case "mess" -> commonResponseUtil.getMessage("message.label.mess.bill");
            case "rebate" -> commonResponseUtil.getMessage("message.label.mess.rebate");
            case "dayScholar" -> commonResponseUtil.getMessage("message.label.days.scholar");
            default -> throw new IllegalStateException("Unexpected value: " + receiptType);
        };
    }

    /* Parse Excel File */
    public List<UploadReceiptsDto> parseExcelFile(UploadReceiptsDto uploadReceiptsDto) {
        List<UploadReceiptsDto> dtos = new ArrayList<>();
        Map<String, Integer> bankRefNoCounts = new HashMap<>();
        boolean hasData = false;
//        try (InputStream inputStream = uploadReceiptsDto.getFile().getInputStream();
        try (InputStream inputStream = new ByteArrayInputStream(uploadReceiptsDto.getFileBytes());
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            // Validate headers
            List<String> headerErrors = validateHeaders(headerRow, uploadReceiptsDto);
            if (!headerErrors.isEmpty()) {
                UploadReceiptsDto errorDto = UploadReceiptsDto.builder().build();
                errorDto.getErrorList().addAll(headerErrors);
                dtos.add(errorDto);
                return dtos;
            }
            for (Row row : sheet) {
                // Skip header
                if (row.getRowNum() == 0) {
                    continue;
                }
                // Skip empty rows
                if (studentWithRemarksService.isRowEmpty(row)) {
                    continue;
                }
                hasData = true;

                // Create DTO
                UploadReceiptsDto dto = UploadReceiptsDto.builder().build();
                dto.setRowNumber(row.getRowNum() + 1);
                dto.setColumnIndexMap(extractColumnIndexMap(headerRow));
                addLog(uploadReceiptsDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.validating.row")+" " +dto.getRowNumber());
                // Mandatory field validation
                if (isMandatoryFieldsEmpty(row, uploadReceiptsDto)) {
                    dto.getErrorList().add(StudentConstants.ROW.getStudentConstant() + ModelConstants.SPACE +
                            (row.getRowNum() + 1) + ModelConstants.SPACE +
                            commonResponseUtil.getMessage("message.label.file.missing.mandatory.fields"));
                    dtos.add(dto);
                    continue;
                }

                // Populate row values
                setUploadReceiptsRows(dto, uploadReceiptsDto, row);
                // Row-level validation
                dto.getErrorList().addAll(validateExcelRowsAndColumns(dto, uploadReceiptsDto, bankRefNoCounts));
                dtos.add(dto);
            }

            // No data case
            if (!hasData) {
                UploadReceiptsDto dto = UploadReceiptsDto.builder().build();
                dto.getErrorList().add(commonResponseUtil.getMessage("message.label.empty.sheet.note"));
                dtos.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            UploadReceiptsDto dto = UploadReceiptsDto.builder().build();
            dto.getErrorList().add(commonResponseUtil.getMessage("message.validation.error.something.went.wrong"));
            dtos.add(dto);
        }
        return dtos;
    }


    public List<String> validateHeaders(Row headerRow, UploadReceiptsDto uploadReceiptsDto) {
        List<String> expectedHeaders = getExpectedHeaders(uploadReceiptsDto);
        DataFormatter formatter = new DataFormatter();
        List<String> actualHeaders = getActualHeader(headerRow, formatter);
        return isInvalidFormat(expectedHeaders, actualHeaders) ?
                List.of(commonResponseUtil.getMessage("message.validation.error.invalid.file.type")) :
                List.of();
    }

    public List<String> getActualHeader(Row headerRow, DataFormatter formatter) {
        return StreamSupport.stream(headerRow.spliterator(), false)
                .map(cell -> formatter.formatCellValue(cell).replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                .toList();
    }

    public boolean isInvalidFormat(List<String> expectedHeaders, List<String> actualHeaders) {
        return IntStream.range(0, expectedHeaders.size())
                .anyMatch(i -> i >= actualHeaders.size() || !expectedHeaders.get(i).equalsIgnoreCase(actualHeaders.get(i)));
    }

    public List<String> getExpectedHeaders(UploadReceiptsDto uploadReceiptsDto) {
        return switch (uploadReceiptsDto.getReceiptType()) {
            case "fee" -> Arrays.stream(ExcelConstants.IFPP_RECEIPT_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            case "loan" -> Arrays.stream(ExcelConstants.LOAN_RECEIPT_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            case "subsidy" -> Arrays.stream(ExcelConstants.SUBSIDY_RECEIPT_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            case "scholarship" -> Arrays.stream(ExcelConstants.SCHOLARSHIP_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            case "mess" -> Arrays.stream(ExcelConstants.MESS_BILLING_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            case "rebate" -> Arrays.stream(ExcelConstants.MESS_REBATE_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            case "dayScholar" -> Arrays.stream(ExcelConstants.DAYS_SCHOLAR_DATA)
                    .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                    .toList();
            default -> List.of();
        };
    }

    public boolean isMandatoryFieldsEmpty(Row row, UploadReceiptsDto uploadReceiptsDto) {
        return switch (uploadReceiptsDto.getReceiptType()) {
            case "fee", "mess" -> isIFPPMessBillingMandatoryFieldsEmpty(row);
            case "loan" -> isLoanMandatoryFieldsEmpty(row);
            case "subsidy", "scholarship", "rebate" -> isSubsidyScholarshipMessRebateMandatoryFieldsEmpty(row);
            case "dayScholar" -> isDaysScholarMandatoryFieldsEmpty(row);
            default -> false;
        };
    }

    public boolean isIFPPMessBillingMandatoryFieldsEmpty(Row row) {
        return studentWithRemarksService.isCellEmpty(row.getCell(0)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(1)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(2)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(3));
    }

    public boolean isLoanMandatoryFieldsEmpty(Row row) {
        return studentWithRemarksService.isCellEmpty(row.getCell(0)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(1)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(2)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(3)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(4)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(5)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(6));
    }

    public boolean isSubsidyScholarshipMessRebateMandatoryFieldsEmpty(Row row) {
        return studentWithRemarksService.isCellEmpty(row.getCell(0)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(1)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(2)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(3)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(4));
    }

    public boolean isDaysScholarMandatoryFieldsEmpty(Row row) {
        return studentWithRemarksService.isCellEmpty(row.getCell(0)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(1)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(2)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(3)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(4)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(5));
    }

    private Map<String, Integer> extractColumnIndexMap(Row headerRow) {

        Map<String, Integer> map = new HashMap<>();

        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue()
                    .trim()
                    .toLowerCase();
            int index = cell.getColumnIndex();
            if (header.contains("bank reference")) map.put("bankRefNo", index);
            if (header.contains("transaction date")) map.put("chequeDate", index);
            if (header.contains("amount")) map.put("amount", index);
            if (header.contains("basic")) map.put("amount", index);
            if (header.contains("roll") || header.contains("student id") || header.contains("studentid")) {map.put("studentId", index);}
            if (header.contains("name") && !header.contains("hostel")) map.put("studentName", index);
            if (header.contains("hostel")) map.put("hostelName", index);
            if (header.contains("room")) map.put("roomNo", index);
            if (header.contains("account")) map.put("accNo", index);
            if (header.contains("ccw fee")) map.put("ccwFee", index);
            if (header.contains("rebate days")) map.put("rebateDays", index);
            if (header.contains("rebate amt")) map.put("amount", index);
            if (header.contains("sanction") || header.contains("claim")) map.put("sanctionPeriod", index);
            if (header.contains("fee for semester")) map.put("feeForSemester", index);
            if (header.contains("year")) map.put("year", index);
        }
        return map;
    }


    private void setUploadReceiptsRows(UploadReceiptsDto dto, UploadReceiptsDto uploadReceiptsDto, Row row) {
        switch (uploadReceiptsDto.getReceiptType()) {
            case "fee" -> setIFPPRows(dto, row);
            case "loan" -> setLoanRows(dto, row);
            case "subsidy" -> setSubsidyRows(dto, row);
            case "mess" -> setMessBillingRows(dto, row);
            case "rebate" -> setMessRebateRows(dto, row);
            case "dayScholar" -> setDaysScholarRows(dto, row);
        }
    }

    public void setIFPPRows(UploadReceiptsDto dto, Row row) {
        dto.setBankRefNo(studentWithRemarksService.getCellValueAsString(row.getCell(0)));
        dto.setChequeDate(studentWithRemarksService.parseDate(row.getCell(1), dto.getErrorList(), row.getRowNum() + 1));
        dto.setAmount(parseDoubleCell(row,2, "Amount","amount",dto));
        dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(3)).replace('\u00A0', ' ').trim());
    }

    public void setLoanRows(UploadReceiptsDto dto, Row row) {
        dto.setStudentName(studentWithRemarksService.getCellValueAsString(row.getCell(1)));
        dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(2)).replace('\u00A0', ' ').trim());
        dto.setRoomNo(studentWithRemarksService.getCellValueAsString(row.getCell(3)));
        dto.setHostelName(studentWithRemarksService.getCellValueAsString(row.getCell(4)));
        dto.setAccNo(studentWithRemarksService.getCellValueAsString(row.getCell(5)));
        dto.setCcwFee(parseDoubleCell(row,6, "CCW Fee","ccwFee",dto));
    }

    public void setSubsidyRows(UploadReceiptsDto dto, Row row) {
        dto.setStudentName(studentWithRemarksService.getCellValueAsString(row.getCell(1)));
        dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(2)).replace('\u00A0', ' ').trim());
        dto.setSanctionPeriod(studentWithRemarksService.getCellValueAsString(row.getCell(3)));
        dto.setAmount(parseDoubleCell(row,4, "Amount","amount", dto));
    }

    public void setMessBillingRows(UploadReceiptsDto dto, Row row) {
        dto.setStudentName(studentWithRemarksService.getCellValueAsString(row.getCell(1)));
        dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(2)).replace('\u00A0', ' ').trim());
        dto.setAmount(parseDoubleCell(row,3, "Amount","amount", dto));
    }

    public void setMessRebateRows(UploadReceiptsDto dto, Row row) {
        dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(1)).replace('\u00A0', ' ').trim());
        dto.setStudentName(studentWithRemarksService.getCellValueAsString(row.getCell(2)));
        try {
            dto.setRebateDays(Long.valueOf(studentWithRemarksService.getCellValueAsString(row.getCell(3))));
        } catch (Exception e) {
            validateRebateDays(dto);
        }
        dto.setAmount(parseDoubleCell(row,4, "Amount","amount", dto));
    }

    public void setDaysScholarRows(UploadReceiptsDto dto, Row row) {
        dto.setBankRefNo(studentWithRemarksService.getCellValueAsString(row.getCell(0)));
        dto.setTransactionDate(studentWithRemarksService.parseDate(row.getCell(1), dto.getErrorList(), row.getRowNum() + 1));
        dto.setAmount(parseDoubleCell(row,2, "Amount","amount", dto));
        dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(3)).replace('\u00A0', ' ').trim());
        dto.setFeeForSemester(studentWithRemarksService.getCellValueAsString(row.getCell(4)));
        dto.setYear(studentWithRemarksService.getCellValueAsString(row.getCell(5)));
    }

    private Double parseDoubleCell(Row row, int colIndex, String fieldName, String columnHeader, UploadReceiptsDto dto) {
        String value = studentWithRemarksService.getCellValueAsString(row.getCell(colIndex));
        String column = ModelConstants.EXCEL_COLUMNS[dto.getColumnIndexMap().get(columnHeader)];
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            if (parsed == 0.0) {
                dto.getErrorList().add(StudentConstants.ROW.getStudentConstant() + " " + dto.getRowNumber() + ", Column " + column + ": " + fieldName + " cannot be zero");
                return 0.0;
            }
            return parsed;
        } catch (NumberFormatException e) {
            dto.getErrorList().add(StudentConstants.ROW.getStudentConstant() +
                    ModelConstants.SPACE + dto.getRowNumber() + ModelConstants.COMMA + ModelConstants.SPACE + "Column" + ModelConstants.SPACE + column +
                    ModelConstants.COLAN + ModelConstants.SPACE + fieldName + " should be a number");
            return 0.0;
        }
    }


    /* Validate Upload Receipts Dtos */
    public ArrayList<String> validateExcelRowsAndColumns(UploadReceiptsDto dto, UploadReceiptsDto uploadReceiptsDtoParam, Map<String, Integer> bankRefNoCounts) {
        return switch (uploadReceiptsDtoParam.getReceiptType()) {
            case "fee" -> validateIFPPRowsAndColumns(dto, bankRefNoCounts);
            case "loan" -> validateLoanRowsAndColumns(dto);
            case "subsidy" -> validateSubsidyRowsAndColumns(dto);
            case "mess" -> validateMessBillingRowsAndColumns(dto);
            case "rebate" -> validateMessRebateRowsAndColumns(dto);
            case "dayScholar" -> validateDaysScholarRowsAndColumns(dto, bankRefNoCounts);
            default -> new ArrayList<>();
        };
    }

    public ArrayList<String> validateIFPPRowsAndColumns(UploadReceiptsDto dto, Map<String, Integer> bankRefNoCounts) {
        ArrayList<String> allErrors = new ArrayList<>();
        Set<String> uniqueErrMsg = new HashSet<>();
        var errorMessages = new ArrayList<String>();
        validateBankRefNo(dto, errorMessages, bankRefNoCounts);
        validateStudentId(dto, errorMessages);
        errorMessages.stream().filter(uniqueErrMsg::add).forEach(allErrors::add);
        return allErrors;
    }

    public ArrayList<String> validateLoanRowsAndColumns(UploadReceiptsDto dto) {
        ArrayList<String> allErrors = new ArrayList<>();
        Set<String> uniqueErrMsg = new HashSet<>();
        var errorMessages = new ArrayList<String>();
        validateStudentName(dto, errorMessages);
        validateStudentId(dto, errorMessages);
        validateHostelName(dto, errorMessages);
        validateRoomNumber(dto, errorMessages);
        validateAccountNumber(dto, errorMessages);
        errorMessages.stream().filter(uniqueErrMsg::add).forEach(allErrors::add);
        return allErrors;
    }

    public ArrayList<String> validateSubsidyRowsAndColumns(UploadReceiptsDto dto) {
        ArrayList<String> allErrors = new ArrayList<>();
        Set<String> uniqueErrMsg = new HashSet<>();
        var errorMessages = new ArrayList<String>();
        validateStudentName(dto, errorMessages);
        validateStudentId(dto, errorMessages);
        errorMessages.stream().filter(uniqueErrMsg::add).forEach(allErrors::add);
        return allErrors;
    }

    public ArrayList<String> validateMessBillingRowsAndColumns(UploadReceiptsDto dto) {
        ArrayList<String> allErrors = new ArrayList<>();
        Set<String> uniqueErrMsg = new HashSet<>();
        var errorMessages = new ArrayList<String>();
        validateStudentName(dto, errorMessages);
        validateStudentId(dto, errorMessages);
        errorMessages.stream().filter(uniqueErrMsg::add).forEach(allErrors::add);
        return allErrors;
    }

    public ArrayList<String> validateMessRebateRowsAndColumns(UploadReceiptsDto dto) {
        ArrayList<String> allErrors = new ArrayList<>();
        Set<String> uniqueErrMsg = new HashSet<>();
        var errorMessages = new ArrayList<String>();
        validateStudentName(dto, errorMessages);
        validateStudentId(dto, errorMessages);
        validateRebateDays(dto);
        errorMessages.stream().filter(uniqueErrMsg::add).forEach(allErrors::add);
        return allErrors;
    }

    public ArrayList<String> validateDaysScholarRowsAndColumns(UploadReceiptsDto dto, Map<String, Integer> bankRefNoCounts) {
        ArrayList<String> allErrors = new ArrayList<>();
        Set<String> uniqueErrMsg = new HashSet<>();
        var errorMessages = new ArrayList<String>();
        validateBankRefNo(dto, errorMessages, bankRefNoCounts);
        validateStudentId(dto, errorMessages);
        validateSemesterFee(dto, errorMessages);
        validateYear(dto, errorMessages);
        errorMessages.stream().filter(uniqueErrMsg::add).forEach(allErrors::add);
        return allErrors;
    }

    public void validateBankRefNo(UploadReceiptsDto dto, List<String> errorMessages, Map<String, Integer> bankRefNoCounts) {
        var bankRefNo = dto.getBankRefNo();
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("bankRefNo")];
        var messLedgerB = messLedgerBRepository.findTop1ByDocRefNoOrderByVoucherDateDesc(bankRefNo);
        if (bankRefNo.isBlank()) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.bank.ref.no.empty"));
        } else {
            if (bankRefNoCounts.containsKey(bankRefNo)) {
                errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE  + commonResponseUtil.getMessage("message.label.bank.ref.no") +
                        ModelConstants.SPACE + bankRefNo + ModelConstants.SPACE +
                        commonResponseUtil.getMessage("message.label.repeated.in.same.sheet"));
                return;
            }
            bankRefNoCounts.put(bankRefNo, rowNum);
            messLedgerB.ifPresent(o -> errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE +
                    commonResponseUtil.getMessage("message.label.bank.ref.no") + ModelConstants.COLAN + ModelConstants.SPACE + bankRefNo + ModelConstants.SPACE +
                    commonResponseUtil.getMessage("message.label.bank.ref.no.already.exist") + ModelConstants.SPACE + o.getAcchead()));
        }
    }

    public void validateStudentId(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        var studentId = dto.getStudentId().toUpperCase().trim();
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("studentId")];
        if (studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
//                if (studentIdCounts.getOrDefault(studentId, 0L) > 1 && !reportedDuplicateStudentIds.contains(studentId)) {
//                    errorMessages.add(StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
//                            studentId + ModelConstants.SPACE +
//                            commonResponseUtil.getMessage("message.label.repeated.in.same.sheet"));
//                    reportedDuplicateStudentIds.add(studentId);
//                }

            AllStudentsDetailsViewDto existingStudent = allStudentsDetailsViewService.getCompleteStudentDetails(dto.getStudentId());
            if (Objects.nonNull(existingStudent) && Objects.nonNull(existingStudent.getStudentId())) {
                String hostelId = Objects.nonNull(existingStudent.getHostelId()) ? existingStudent.getHostelId().toString() : ModelConstants.NOT_APPLICABLE;
                dto.setHostelId(hostelId);
            } else {
                errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
                        studentId + ModelConstants.SPACE +
                        commonResponseUtil.getMessage("message.label.does.not.exist"));
            }
//                var previousStudent = studentDetailsInfoService.getStudentPreviousInfoDetails(Objects.requireNonNull(existingStudent).getPreviousId());
            if (studentDetailsInfoRepository.checkStudentIdExistInPreviousId(studentId, ModelConstants.STATUS_ACTIVE).isPresent()) {
                errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.student.id.changed") + ModelConstants.SPACE + studentId);
            }

            if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(ModelConstants.STATUS_ACTIVE, studentId, ModelConstants.YES)) {
                errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + BulkAllotmentStudentsConstants.SETTLEMENT_COMPLETED_FOR_THE_STUDENT.getString() + ModelConstants.SPACE + studentId);
            }
        } else {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.alphanumeric.student.id") + ModelConstants.SPACE + studentId);
        }
    }

    public void validateStudentName(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        var studentName = dto.getStudentName();
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("studentName")];
        if (studentName.isBlank()) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.student.name.empty"));
        }
    }

    public void validateHostelName(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        var hostelName = dto.getHostelName();
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("hostelName")];
        if (hostelName.isBlank()) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.hostel.name.empty"));
        } else if (!hostelName.matches(ModelConstants.ALPHA_WITH_SPACE)){
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.hostel.name.contains.character") + ModelConstants.SPACE + hostelName);
        }
    }

    public void validateRoomNumber(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        var roomNo = dto.getRoomNo();
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("roomNo")];
        if (roomNo.isBlank()) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.room.no.empty"));
        } else if (!ModelConstants.numericRegex.matcher(roomNo).matches()){
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.room.no.contains.numbers") + ModelConstants.SPACE + roomNo);
        }
    }

    public void validateAccountNumber(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        var accNo = dto.getAccNo();
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("accNo")];
        if (accNo.isBlank()) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.acc.no.empty"));
        } else if (!ModelConstants.numericRegex.matcher(accNo).matches()){
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.acc.no.contains.numbers") + ModelConstants.SPACE + accNo);
        }
    }

    public void validateAmount(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("amount")];
        if (dto.getAmount() <= 0) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.amount.positive"));
        } else if (dto.getAmount() > 99999999L) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.amount.max.limit"));
        }
    }

    public void validateRebateDays(UploadReceiptsDto dto) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("rebateDays")];
        if (Objects.nonNull(dto.getRebateDays()) && dto.getRebateDays() <= 0) {
            dto.getErrorList().add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.rebate.days.positive"));
        } else if (!ModelConstants.numericRegex.matcher(String.valueOf(dto.getRebateDays())).matches()) {
            dto.getErrorList().add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.rebate.days.numeric"));
        }
    }

    public void validateYear(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("year")];
        var year = dto.getYear();
        if (year.isBlank()) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.year.empty"));
        } else if (!ModelConstants.numericRegex.matcher(year).matches()){
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.year.contains.numeric") + ModelConstants.SPACE + year);
        }
    }

    public void validateCCWFee(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("ccwFee")];
        if (dto.getCcwFee() <= 0) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.amount.positive"));
        } else if (dto.getCcwFee() > 99999999L) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.amount.max.limit"));
        }
    }

    private void validateTransactionDate(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("chequeDate")];
        if (dto.getTransactionDate() == null) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.transaction.date.empty"));
        }
    }

    private void validateSemesterFee(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("feeForSemester")];
        if (dto.getFeeForSemester() == null) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.semester.empty"));
        }
    }

    private void validateSanctionPeriod(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("sanctionPeriod")];
        if (dto.getSanctionPeriod() == null) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.sanction.period.empty"));
        }
    }

    private void validateChequeDate(UploadReceiptsDto dto, ArrayList<String> errorMessages) {
        int rowNum = dto.getRowNumber();
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        String colNum=columns[dto.getColumnIndexMap().get("chequeDate")];
        if (dto.getChequeDate() == null) {
            errorMessages.add("Row " + rowNum + ", Column " + colNum + ModelConstants.COLAN + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.date.empty"));
        }
    }

//    private Map<String, Long> getStudentIdCounts(List<UploadReceiptsDto> dtos) {
//        return dtos.stream()
//                .map(UploadReceiptsDto::getStudentId)
//                .filter(Objects::nonNull)
//                .filter(s -> !s.isBlank())
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//    }

    private Map<String, Long> getBankRefNoCounts(UploadReceiptsDto dto) {
        return Map.of(dto.getBankRefNo(), (long) dto.getStudentCount() + 1);
    }

    public void saveExcelFile(List<UploadReceiptsDto> dtos, UploadReceiptsDto uploadReceiptsDto,String tag) throws Exception {
        long sessionID = 0;
        var finYearDto = accountHeadService.getFinYearDto();
        boolean faIntegrationFlag = Boolean.parseBoolean(simsConfigDataService.getSimConfigValue(SimsConfigDataService.IKOLLEGE_FA_INTEGRATION));
        AtomicInteger slNo = new AtomicInteger(0);
        dtos.forEach(dto -> {
            AtomicLong voucherNo = new AtomicLong(messLedgerARepository.getNextValMessLedger());
            dto.setVoucherNo(String.valueOf(voucherNo));
            dto.setStudentCount(dtos.size());
            dto.setDate(uploadReceiptsDto.getDate());
            dto.setSlNo(slNo.incrementAndGet());
        });
        var firstVoucherNo = dtos.getFirst().getVoucherNo();
        switch (uploadReceiptsDto.getReceiptType()) {
            case "fee" -> saveIFPPExcelFile(dtos, finYearDto, uploadReceiptsDto, faIntegrationFlag, firstVoucherNo,tag);
            case "loan" -> saveLoanExcelFile(dtos, finYearDto, uploadReceiptsDto, faIntegrationFlag, firstVoucherNo,tag);
            case "subsidy" -> saveSubsidyExcelFile(dtos, finYearDto, uploadReceiptsDto, faIntegrationFlag, firstVoucherNo,tag);
            case "mess" -> saveMessBillingExcelFile(dtos, finYearDto, uploadReceiptsDto, faIntegrationFlag, firstVoucherNo,tag);
            case "rebate" -> saveMessRebateExcelFile(dtos, finYearDto, uploadReceiptsDto, faIntegrationFlag, firstVoucherNo,tag);
            case "dayScholar" -> saveDayScholarExcelFile(dtos, finYearDto, uploadReceiptsDto, faIntegrationFlag, firstVoucherNo,tag);
        }
        saveUserAuditTrial();
    }

    private void saveIFPPExcelFile(List<UploadReceiptsDto> dtos, FinancialYearDto finYearDto, UploadReceiptsDto uploadReceiptsDto,
                                   boolean faIntegrationFlag, String voucherNo,String tag) throws Exception {
        List<MessLedgerAEntity> messLedgerAEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setAccHead(uploadReceiptsDto.getBank());
                    txnDto.setDate(dto.getChequeDate());
                    txnDto.setDescription(commonResponseUtil.getMessage("message.label.towards.ifpp.payments") + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFeeTypes());
                    txnDto.setDocRefNo(dto.getBankRefNo());
                    txnDto.setDebitOrCredit(Constants.DEBIT);
                    txnDto.setScreenType("ur_" + (Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.ifpp.payments.ms") : commonResponseUtil.getMessage("message.label.ifpp.payments.card")));
                    txnDto.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
                    return TransferAmountUtils.createMessLedgerAEntity(txnDto, finYearDto.getFinYear());
                })
                .toList();
        List<MessLedgerBEntity> messLedgerBEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setSlNo(1);
                    txnDto.setAccHead(dto.getStudentId().toUpperCase());
                    txnDto.setDate(dto.getChequeDate());
                    txnDto.setDescription(commonResponseUtil.getMessage("message.label.towards.ifpp.payments") + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFeeTypes());
                    txnDto.setScreenType("ur_" + (Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.ifpp.payments.ms") : commonResponseUtil.getMessage("message.label.ifpp.payments.card")));
                    txnDto.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
                    txnDto.setDocRefNo(dto.getBankRefNo());
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, 1);
                })
                .toList();
        messLedgerARepository.saveAll(messLedgerAEntityList);
        List<MessLedgerBEntity> saved = messLedgerBRepository.saveAll(messLedgerBEntityList);
        addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));
        if (faIntegrationFlag){
            StudentDebitForm studentDebitForm = new StudentDebitForm();
            studentDebitForm.setReferenceNumber(String.valueOf(voucherNo));
            studentDebitForm.setAmount(uploadReceiptsDto.getAmount());
            studentDebitForm.setTransferStatus(ModelConstants.INITIATED);
            studentDebitForm.setIKollegeTransferType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ?
                    commonResponseUtil.getMessage("message.label.ifpp.payments.ms") :
                    commonResponseUtil.getMessage("message.label.ifpp.payments.card"));
            studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
            studentDebitForm.setHostelName(ModelConstants.EMPTY_STRING);
            studentDebitForm.setDescription(commonResponseUtil.getMessage("message.label.towards.ifpp.payments") +
                    ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE +
                    uploadReceiptsDto.getFeeTypes());
            transferAmountUtils.saveTransactioninFA(studentDebitForm);
        }
    }

    private void saveLoanExcelFile(List<UploadReceiptsDto> dtos, FinancialYearDto finYearDto, UploadReceiptsDto uploadReceiptsDto,
                                   boolean faIntegrationFlag, String voucherNo,String tag) throws Exception {
        TransactionDto txnDtoMessLedgerA = new TransactionDto();
        double totalAmount = dtos.stream()
                .mapToDouble(UploadReceiptsDto::getCcwFee)
                .sum();
        uploadReceiptsDto.setVoucherNo(String.valueOf(voucherNo));
        uploadReceiptsDto.setStudentCount(dtos.size());
        populateCommonTransactionFields(txnDtoMessLedgerA, uploadReceiptsDto, finYearDto);
        txnDtoMessLedgerA.setAccHead(uploadReceiptsDto.getBank());
        txnDtoMessLedgerA.setAmount(totalAmount);
        txnDtoMessLedgerA.setDescription(commonResponseUtil.getMessage("message.label.towards.loan.receipt"));
        txnDtoMessLedgerA.setFcno(String.valueOf(0L));
        txnDtoMessLedgerA.setDebitOrCredit(Constants.DEBIT);
        txnDtoMessLedgerA.setScreenType("ur_" + (Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.loan.receipt.ms") : commonResponseUtil.getMessage("message.label.loan.receipt.card")));
        txnDtoMessLedgerA.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(txnDtoMessLedgerA, finYearDto.getFinYear());
        List<MessLedgerBEntity> messLedgerBEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setVoucherNo(String.valueOf(voucherNo));
                    txnDto.setDate(dto.getDate());
                    txnDto.setScreenType("ur_" + (Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.loan.receipt.ms") : commonResponseUtil.getMessage("message.label.loan.receipt.card")));
                    txnDto.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
                    txnDto.setSlNo(dto.getSlNo());
                    txnDto.setAccHead(dto.getStudentId());
                    txnDto.setDescription(commonResponseUtil.getMessage("message.label.towards.loan.receipt"));
                    txnDto.setDocRefNo(dto.getAccNo());
                    txnDto.setAmount(dto.getCcwFee());
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, dto.getSlNo());
                })
                .toList();
        messLedgerARepository.save(messLedgerAEntity);
        List<MessLedgerBEntity> saved = messLedgerBRepository.saveAll(messLedgerBEntityList);
        addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));
        if (faIntegrationFlag){
            StudentDebitForm studentDebitForm = new StudentDebitForm();
            studentDebitForm.setReferenceNumber(String.valueOf(voucherNo));
            studentDebitForm.setAmount(uploadReceiptsDto.getAmount());
            studentDebitForm.setTransferStatus(ModelConstants.INITIATED);
            studentDebitForm.setIKollegeTransferType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.loan.receipt.ms") : commonResponseUtil.getMessage("message.label.loan.receipt.card"));
            studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
            studentDebitForm.setHostelName(ModelConstants.EMPTY_STRING);
            studentDebitForm.setDescription(commonResponseUtil.getMessage("message.label.towards.loan.receipt"));
            transferAmountUtils.saveTransactioninFA(studentDebitForm);
        }
    }

    private void saveSubsidyExcelFile(List<UploadReceiptsDto> dtos, FinancialYearDto finYearDto, UploadReceiptsDto uploadReceiptsDto,
                                      boolean faIntegrationFlag, String voucherNo,String tag) throws Exception {
        TransactionDto txnDtoMessLedgerA = new TransactionDto();
        double totalAmount = dtos.stream()
                .mapToDouble(UploadReceiptsDto::getAmount)
                .sum();
        String dateStr = Objects.nonNull(uploadReceiptsDto.getDate()) ?
                ModelConstants.COLAN  + ModelConstants.SPACE + uploadReceiptsDto.getDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)) : ModelConstants.EMPTY_STRING;
        uploadReceiptsDto.setVoucherNo(String.valueOf(voucherNo));
        uploadReceiptsDto.setStudentCount(dtos.size());
        populateCommonTransactionFields(txnDtoMessLedgerA, uploadReceiptsDto, finYearDto);
        txnDtoMessLedgerA.setAmount(totalAmount);
        txnDtoMessLedgerA.setAccHead(uploadReceiptsDto.getBank());
        txnDtoMessLedgerA.setDescription(commonResponseUtil.getMessage("message.label.towards.subsidy.receipt")
                + ModelConstants.COLAN  + ModelConstants.SPACE + "On " + dateStr);
        txnDtoMessLedgerA.setFcno(String.valueOf(0L));
        txnDtoMessLedgerA.setDebitOrCredit(Constants.DEBIT);
        txnDtoMessLedgerA.setScreenType("ur_" + (Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.subsidy.receipt.ms") : commonResponseUtil.getMessage("message.label.subsidy.receipt.card")));
        txnDtoMessLedgerA.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(txnDtoMessLedgerA, finYearDto.getFinYear());
        List<MessLedgerBEntity> messLedgerBEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setVoucherNo(String.valueOf(voucherNo));
                    txnDto.setScreenType("ur_" + (Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.subsidy.receipt.ms") : commonResponseUtil.getMessage("message.label.subsidy.receipt.card")));
                    txnDto.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
                    txnDto.setSlNo(dto.getSlNo());
                    txnDto.setAccHead(dto.getStudentId());
                    txnDto.setDescription(commonResponseUtil.getMessage("message.label.towards.subsidy.receipt")
                            + ModelConstants.SPACE + ModelConstants.COLAN  + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.sanction.and.claim.period")
                            + ModelConstants.COLAN  + ModelConstants.SPACE + dto.getSanctionPeriod());
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, dto.getSlNo());
                })
                .toList();
        messLedgerARepository.save(messLedgerAEntity);
        List<MessLedgerBEntity> saved = messLedgerBRepository.saveAll(messLedgerBEntityList);
        addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));

        if (faIntegrationFlag){
            StudentDebitForm studentDebitForm = new StudentDebitForm();
            studentDebitForm.setReferenceNumber(String.valueOf(voucherNo));
            studentDebitForm.setAmount(uploadReceiptsDto.getAmount());
            studentDebitForm.setTransferStatus(ModelConstants.INITIATED);
            studentDebitForm.setIKollegeTransferType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? commonResponseUtil.getMessage("message.label.subsidy.receipt.ms") : commonResponseUtil.getMessage("message.label.subsidy.receipt.card"));
            studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
            studentDebitForm.setHostelName(ModelConstants.EMPTY_STRING);
            studentDebitForm.setDescription(commonResponseUtil.getMessage("message.label.towards.subsidy.receipt")
                    + ModelConstants.SPACE + ModelConstants.COLAN  + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.sanction.and.claim.period")
                    + ModelConstants.COLAN  + ModelConstants.SPACE + uploadReceiptsDto.getSanctionPeriod());
            transferAmountUtils.saveTransactioninFA(studentDebitForm);
        }
    }

    private void saveMessBillingExcelFile(List<UploadReceiptsDto> dtos, FinancialYearDto finYearDto, UploadReceiptsDto uploadReceiptsDto,
                                          boolean faIntegrationFlag, String voucherNo,String tag) throws Exception {
        TransactionDto txnDtoMessLedgerA = new TransactionDto();
        double totalAmount = dtos.stream()
                .mapToDouble(UploadReceiptsDto::getAmount)
                .sum();
        uploadReceiptsDto.setVoucherNo(String.valueOf(voucherNo));
        uploadReceiptsDto.setStudentCount(dtos.size());
        populateCommonTransactionFields(txnDtoMessLedgerA, uploadReceiptsDto, finYearDto);
        txnDtoMessLedgerA.setAmount(totalAmount);
        txnDtoMessLedgerA.setAccHead(uploadReceiptsDto.getAccHead());
        txnDtoMessLedgerA.setDescription(uploadReceiptsDto.getDescription()
                + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFromDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))
                + Constants.TO + uploadReceiptsDto.getToDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
        txnDtoMessLedgerA.setFcno(String.valueOf(0L));
        txnDtoMessLedgerA.setDebitOrCredit(Constants.DEBIT);
        txnDtoMessLedgerA.setScreenType("ur_" + (commonResponseUtil.getMessage("message.label.mess.bill.receipt") + uploadReceiptsDto.getAccHead()));
        txnDtoMessLedgerA.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(txnDtoMessLedgerA, finYearDto.getFinYear());
        List<MessLedgerBEntity> messLedgerBEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setSlNo(dto.getSlNo());
                    txnDto.setVoucherNo(String.valueOf(voucherNo));
                    txnDto.setScreenType("ur_" + (commonResponseUtil.getMessage("message.label.mess.bill.receipt") + uploadReceiptsDto.getAccHead()));
                    txnDto.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
                    txnDto.setAccHead(dto.getStudentId());
                    txnDto.setDescription(uploadReceiptsDto.getDescription()
                            + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFromDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))
                            + Constants.TO + uploadReceiptsDto.getToDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, dto.getSlNo());
                })
                .toList();
        messLedgerARepository.save(messLedgerAEntity);
        List<MessLedgerBEntity> saved = messLedgerBRepository.saveAll(messLedgerBEntityList);
        addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));

        if (faIntegrationFlag){
            StudentDebitForm studentDebitForm = new StudentDebitForm();
            studentDebitForm.setReferenceNumber(String.valueOf(voucherNo));
            studentDebitForm.setAmount(uploadReceiptsDto.getAmount());
            studentDebitForm.setTransferStatus(ModelConstants.INITIATED);
            studentDebitForm.setIKollegeTransferType(commonResponseUtil.getMessage("message.label.mess.bill.receipt") + uploadReceiptsDto.getAccHead());
            studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
            studentDebitForm.setHostelName(ModelConstants.EMPTY_STRING);
            studentDebitForm.setDescription(uploadReceiptsDto.getDescription()
                    + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFromDate()
                    + Constants.TO + uploadReceiptsDto.getToDate());
            transferAmountUtils.saveTransactioninFA(studentDebitForm);
        }
    }

    private void saveMessRebateExcelFile(List<UploadReceiptsDto> dtos, FinancialYearDto finYearDto, UploadReceiptsDto uploadReceiptsDto,
                                         boolean faIntegrationFlag, String voucherNo,String tag) throws Exception {
        TransactionDto txnDtoMessLedgerA = new TransactionDto();
        double totalAmount = dtos.stream()
                .mapToDouble(UploadReceiptsDto::getAmount)
                .sum();
        uploadReceiptsDto.setVoucherNo(String.valueOf(voucherNo));
        uploadReceiptsDto.setStudentCount(dtos.size());
        populateCommonTransactionFields(txnDtoMessLedgerA, uploadReceiptsDto, finYearDto);
        txnDtoMessLedgerA.setAmount(totalAmount);
        txnDtoMessLedgerA.setAccHead(uploadReceiptsDto.getAccHead());
        txnDtoMessLedgerA.setDescription(uploadReceiptsDto.getDescription()
                + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFromDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))
                + Constants.TO + uploadReceiptsDto.getToDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
        txnDtoMessLedgerA.setFcno(String.valueOf(0L));
        txnDtoMessLedgerA.setDebitOrCredit(Constants.DEBIT);
        txnDtoMessLedgerA.setScreenType("ur_" + (commonResponseUtil.getMessage("message.label.mess.rebate.receipt") + uploadReceiptsDto.getAccHead()));
        txnDtoMessLedgerA.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);

        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(txnDtoMessLedgerA, finYearDto.getFinYear());
        List<MessLedgerBEntity> messLedgerBEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setVoucherNo(String.valueOf(voucherNo));
                    txnDto.setSlNo(dto.getSlNo());
                    txnDto.setAccHead(dto.getStudentId());
                    txnDto.setDescription(uploadReceiptsDto.getDescription() + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + "Rebate Days : " + dto.getRebateDays());
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    txnDto.setScreenType("ur_" + (commonResponseUtil.getMessage("message.label.mess.rebate.receipt") + uploadReceiptsDto.getAccHead()));
                    txnDto.setBookType(Constants.MESS_MS.equalsIgnoreCase(uploadReceiptsDto.getBookType()) ? Constants.MESS_MS : Constants.CREDIT_CARD);
                    addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, dto.getSlNo());
                })
                .toList();
        messLedgerARepository.save(messLedgerAEntity);
        List<MessLedgerBEntity> saved = messLedgerBRepository.saveAll(messLedgerBEntityList);
        addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));

        if (faIntegrationFlag){
            StudentDebitForm studentDebitForm = new StudentDebitForm();
            studentDebitForm.setReferenceNumber(String.valueOf(voucherNo));
            studentDebitForm.setAmount(uploadReceiptsDto.getAmount());
            studentDebitForm.setTransferStatus(ModelConstants.INITIATED);
            studentDebitForm.setIKollegeTransferType(commonResponseUtil.getMessage("message.label.mess.bill.receipt") + uploadReceiptsDto.getAccHead());
            studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
            studentDebitForm.setHostelName(ModelConstants.EMPTY_STRING);
            studentDebitForm.setDescription(uploadReceiptsDto.getDescription()
                    + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + uploadReceiptsDto.getFromDate()
                    + Constants.TO + uploadReceiptsDto.getToDate());
            transferAmountUtils.saveTransactioninFA(studentDebitForm);
        }
    }

    private void saveDayScholarExcelFile(List<UploadReceiptsDto> dtos, FinancialYearDto finYearDto, UploadReceiptsDto uploadReceiptsDto,
                                         boolean faIntegrationFlag, String voucherNo,String tag) throws Exception {
        TransactionDto txnDtoMessLedgerA = new TransactionDto();
        double totalAmount = dtos.stream()
                .mapToDouble(UploadReceiptsDto::getAmount)
                .sum();
        uploadReceiptsDto.setVoucherNo(String.valueOf(voucherNo));
        uploadReceiptsDto.setStudentCount(dtos.size());
        populateCommonTransactionFields(txnDtoMessLedgerA, uploadReceiptsDto, finYearDto);
        txnDtoMessLedgerA.setAmount(totalAmount);
        txnDtoMessLedgerA.setAccHead(uploadReceiptsDto.getBank());
        txnDtoMessLedgerA.setDocRefNo(uploadReceiptsDto.getBankRefNo());
        txnDtoMessLedgerA.setDescription(commonResponseUtil.getMessage("message.label.days.scholar.fee.payment"));
        txnDtoMessLedgerA.setReportrp(commonResponseUtil.getMessage("message.label.ds"));
        txnDtoMessLedgerA.setDebitOrCredit(Constants.DEBIT);
        txnDtoMessLedgerA.setScreenType("ur_" + (commonResponseUtil.getMessage("message.label.day.scholar.fee")));

        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(txnDtoMessLedgerA, finYearDto.getFinYear());
        List<MessLedgerBEntity> messLedgerBEntityList = dtos.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setVoucherNo(String.valueOf(voucherNo));
                    txnDto.setSlNo(dto.getSlNo());
                    txnDto.setAccHead(dto.getStudentId());
                    txnDto.setDescription(getDayScholarLedgerBDescription(dto));
                    txnDto.setDocRefNo(dto.getBankRefNo());
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, dto.getSlNo());
                })
                .toList();
        messLedgerARepository.save(messLedgerAEntity);
        List<MessLedgerBEntity> saved = messLedgerBRepository.saveAll(messLedgerBEntityList);
        addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));

        if (faIntegrationFlag){
            StudentDebitForm studentDebitForm = new StudentDebitForm();
            studentDebitForm.setReferenceNumber(String.valueOf(voucherNo));
            studentDebitForm.setAmount(uploadReceiptsDto.getAmount());
            studentDebitForm.setTransferStatus(ModelConstants.INITIATED);
            studentDebitForm.setIKollegeTransferType(commonResponseUtil.getMessage("message.label.day.scholar.fee"));
            studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
            studentDebitForm.setHostelName(uploadReceiptsDto.getHostelName());
            studentDebitForm.setDescription(commonResponseUtil.getMessage("message.label.days.scholar.fee.payment"));
            transferAmountUtils.saveTransactioninFA(studentDebitForm);
        }
    }

    private String getDayScholarLedgerBDescription(UploadReceiptsDto dto) {
        return (commonResponseUtil.getMessage("message.label.days.scholar.fee.payment")
                + ModelConstants.SPACE + ModelConstants.HYPHEN  + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.fee.for.semester")
                + ModelConstants.RIGHT_BRACKET + dto.getFeeForSemester() + ModelConstants.LEFT_BRACKET
                + ModelConstants.SPACE + commonResponseUtil.getMessage("message.label.year.to.colon")
                + ModelConstants.RIGHT_BRACKET + dto.getYear() + ModelConstants.LEFT_BRACKET);
    }

    private void populateCommonTransactionFields(TransactionDto txnDto, UploadReceiptsDto dto, FinancialYearDto finYearDto) {
        txnDto.setFinYear(finYearDto.getFinYear());
        txnDto.setBookType(Constants.MESS_MS);
        txnDto.setVoucherNo(dto.getVoucherNo());
        txnDto.setDate(dto.getDate());
        txnDto.setSubAccHead(Constants.NIL);
        txnDto.setAmount(dto.getAmount());
        txnDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        txnDto.setStudentCount(dto.getStudentCount());
        txnDto.setRecon(ModelConstants.STATUS_INACTIVE);
        txnDto.setFcno(dto.getHostelId());
    }

    private boolean saveUserAuditTrial() {
        return auditTrailService.saveAuditTrail("upload-receipts", commonResponseUtil.getMessage("url.upload.receipts"),
                this.getClass().getName() + Constants.HYPHEN + "saveUploadReceiptsBulkUpload()");
    }

    private void addLog(String tag, long sessionId, String msg) {
        logService.addLog(tag, " <-" + sessionId + "-> " + msg);
    }
}
