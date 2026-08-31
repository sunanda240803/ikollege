package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.StudentDetailsInfoMapper;
import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.dto.mess.FoodCourtLedgerDto;
import com.iitm.hosteldine.dto.mess.FoodCourtTransactionDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.form.common.FoodCourtPurchaseForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.FoodCourtLedgerMapper;
import com.iitm.hosteldine.mapper.mess.MessMasterMapper;
import com.iitm.hosteldine.model.mess.FoodCourtLedgerEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.mess.CurrentMessDetailsViewRepository;
import com.iitm.hosteldine.repository.mess.FoodCourtLedgerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.student.StudentWithRemarksService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FoodCourtDebitCreditService {

    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessMasterService messMasterService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final FoodCourtLedgerRepository foodCourtLedgerRepository;
    private final MessageSource messageSource;
    private final ExcelUtility excelUtility;
    private final StudentWithRemarksService studentWithRemarksService;
    private final MessMasterCommonService messMasterCommonService;
    private final MessMasterRepository messMasterRepository;
    private final CurrentMessDetailsViewRepository currentMessDetailsViewRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;

    public List<FoodCourtLedgerDto> getDebitCreditList(long id) {
        MessMasterControllerDto messMasterControllerDto = messMasterCommonService.getCurrentMessPeriod();
        return getFoodCourtList(id==0 ? Optional.ofNullable(messMasterControllerDto).map(MessMasterControllerDto::getId).orElse(0L) : id);
    }

    @Transactional
    public String saveFoodCourtList(Long messPeriod) {
        MessMasterControllerDto messMasterControllerDto = messMasterCommonService.getCurrentMessPeriod();
        Long messPeriodId = Objects.nonNull(messPeriod) && messPeriod != 0 ? messPeriod : Optional.ofNullable(messMasterControllerDto).map(MessMasterControllerDto::getId).orElse(0L);
        List<FoodCourtLedgerDto> foodCourtList = getFoodCourtList(messPeriodId);
        boolean hasZeroAmount = foodCourtList.stream()
                .anyMatch(dto -> dto.getFoodCourtAmount() == null || dto.getFoodCourtAmount() == 0);
        if (hasZeroAmount) {
            return commonResponseUtil.getMessage("message.label.food.court.amount.zero");
        } else if (foodCourtList.isEmpty()) {
            return commonResponseUtil.getMessage("message.label.food.court.list.empty");
        }
        List<FoodCourtLedgerEntity> entities = foodCourtList.stream().map(dto -> saveFoodCourt(dto, messPeriodId)).toList();
        foodCourtLedgerRepository.saveAll(entities);
        return Constants.SAVED;
    }


    private List<FoodCourtLedgerDto> getFoodCourtList(Long id) {
        Optional<List<Object[]>> optionalList = messMasterControllerRepository.getFoodCourtCreditList(ModelConstants.STATUS_ACTIVE, id);
        return optionalList.map(list -> list.stream().map(this::toLedgerDto).toList()).orElseGet(ArrayList::new);
    }

    private FoodCourtLedgerDto toLedgerDto(Object[] record) {
        return FoodCourtLedgerDto.builder()
                .messName(getString(record[0]))
                .studentId(getString(record[1]))
                .messId(getString(record[2]))
                .amount(getSafeDouble(record))
                .changeFromDate(getLocalDate(record[4]))
                .changeToDate(getLocalDate(record[5]))
                .foodCourtAmount(getDouble(record))
                .debitOrCredit(Constants.CREDIT_FULL_FORM)
                .build();
    }

    private Double getDouble(Object[] record) {
        return (record.length > 6 && record[6] != null) ? Double.parseDouble(record[6].toString()) : 0.00;
    }

    private String getString(Object record) {
        return record != null ? record.toString() : null;
    }

    private LocalDate getLocalDate(Object record) {
        return (record != null) ? LocalDate.parse(record.toString()) : null;
    }

    private Double getSafeDouble(Object[] record) {
        if (record.length > 3) {
            Object value = record[3];
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value != null) {
                try {
                    return Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    return 0.00;
                }
            }
        }
        return 0.0;
    }

    public FoodCourtLedgerEntity saveFoodCourt(FoodCourtLedgerDto foodCourtLedgerDto, Long mmcId) {
        FoodCourtLedgerEntity foodCourtLedgerEntity = new FoodCourtLedgerEntity();
        foodCourtLedgerEntity.setMessMaster(MessMasterMapper.INSTANCE
                .toMessMasterEntity(
                        messMasterService.getMessMasterDetailsById(Long.parseLong(foodCourtLedgerDto.getMessId()))
                ));
        foodCourtLedgerEntity.setStudent(StudentDetailsInfoMapper.INSTANCE
                .toEntity(
                        studentDetailsInfoService.getStudentInfoDetails(foodCourtLedgerDto.getStudentId())
                ));
        foodCourtLedgerEntity.setMessPeriodId(mmcId);
        foodCourtLedgerEntity.setAmount(foodCourtLedgerDto.getAmount());
        foodCourtLedgerEntity.setPurchaseTimestamp(DateUtility.getNowTimeInstant());
        foodCourtLedgerEntity.setDebitOrCredit(Constants.CREDIT);
        foodCourtLedgerEntity.setDescription(commonResponseUtil.getMessage("message.label.food.court.credit.description"));
        return foodCourtLedgerEntity;
    }

    public FoodCourtLedgerDto getStudentLedgerDetails(String studentId) {
        return foodCourtLedgerRepository.getStudentNameMessNameBalAmount(studentId)
                .map(result -> (Object[]) result)
                .filter(record -> record.length >= 3)
                .map(record -> FoodCourtLedgerDto.builder()
                        .studentName(ValidationCommon.toStringOrNull(record[0]))
                        .messName(ValidationCommon.toStringOrNull(record[1]))
                        .amount(asDouble(record))
                        .build())
                .orElse(null);
    }

    private Double asDouble(Object[] obj) {
        if (obj.length > 2) {
            Object val = obj[2];
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            } else if (val != null) {
                try {
                    return Double.parseDouble(val.toString());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }

    @Transactional
    public String saveFoodCourtTopUpAmount(FoodCourtLedgerDto foodCourtLedgerDto) {
        try {
            FoodCourtLedgerEntity foodCourtLedgerEntity = getFoodCourtDetails(foodCourtLedgerDto);
            foodCourtLedgerEntity.setPurchaseTimestamp(DateUtility.getNowTimeInstant());
            foodCourtLedgerEntity.setDescription(messageSource.getMessage("message.label.food.court.top.up.description", null, Locale.getDefault()));
            foodCourtLedgerRepository.save(foodCourtLedgerEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.FAILURE;
        }
        return Constants.UPDATED;
    }

    private FoodCourtLedgerEntity getFoodCourtDetails(FoodCourtLedgerDto foodCourtLedgerDto) {
        FoodCourtLedgerDto foodCourtMessIdMessPeriodIdDetails = mapToDto(foodCourtLedgerDto);
        foodCourtLedgerDto.setMessId(foodCourtMessIdMessPeriodIdDetails.getMessId());
        foodCourtLedgerDto.setMessPeriodId(foodCourtMessIdMessPeriodIdDetails.getMessPeriodId());
        StudentDetailsInfoEntity studentDetailsInfoEntity = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(foodCourtLedgerDto.getStudentId(), ModelConstants.STATUS_ACTIVE).orElseThrow(RuntimeException::new);
        MessMasterEntity messMaster = messMasterRepository.findByIdAndActiveFlag(Long.parseLong(foodCourtMessIdMessPeriodIdDetails.getMessId()), ModelConstants.STATUS_ACTIVE).orElseThrow(RuntimeException::new);
        FoodCourtLedgerEntity foodCourtLedgerEntity = FoodCourtLedgerMapper.INSTANCE.toEntity(foodCourtLedgerDto);
        foodCourtLedgerEntity.setMessMaster(messMaster);
        foodCourtLedgerEntity.setStudent(studentDetailsInfoEntity);
        return foodCourtLedgerEntity;
    }

    private FoodCourtLedgerDto mapToDto(FoodCourtLedgerDto foodCourtLedgerDto) {
        return currentMessDetailsViewRepository.findByStudentId(foodCourtLedgerDto.getStudentId().toUpperCase())
                .map(record -> FoodCourtLedgerDto
                        .builder()
                        .messId(String.valueOf(record.getMessId()))
                        .messPeriodId(record.getMessPeriodId())
                        .build())
                .orElse(null);
    }

    public Workbook downloadExcelTemplate() {
        String sheetName = messageSource.getMessage("message.label.food.court.debit", null, Locale.getDefault());
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        String[] headerData = ExcelConstants.FOOD_COURT_DEBIT_DATA;
        String[] headerDataWidth = ExcelConstants.FOOD_COURT_DEBIT_DATA_WIDTH;
        excelUtility.setDataStyle(workbook);
        Row headerRow = sheet.createRow(0);
        excelUtility.createHeader(headerRow, 0, headerData, workbook);
        IntStream.range(0, headerDataWidth.length)
                .forEach(i -> sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i])));
        return workbook;
    }

    public FoodCourtLedgerDto saveFoodCourtBulkDetails(MultipartFile file) {
        var foodCourtLedgerDto = FoodCourtLedgerDto.builder();
        var errorList = new ArrayList<String>();
        try {
            var dtos = parseExcelFile(file, errorList);
            if (errorList.isEmpty()) {
                errorList.addAll(validateDtos(dtos));
            }
            if (!errorList.isEmpty()) {
                return foodCourtLedgerDto.errorList(errorList).build();
            }
            dtos.forEach(this::saveFoodCourtDebitDetails);
        } catch (Exception e) {
            e.printStackTrace();
            errorList.add(messageSource.getMessage("message.validation.invalid.file.format", null, Locale.getDefault()));
            foodCourtLedgerDto.errorList(errorList);
        }
        return foodCourtLedgerDto.build();
    }

    private List<String> validateDtos(List<FoodCourtLedgerDto> dtos) {
        Map<String, Long> studentIdCounts = dtos.stream()
                .map(FoodCourtLedgerDto::getStudentId)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Set<String> reportedDuplicateStudentIds = new HashSet<>();
        List<String> allErrors = new ArrayList<>();

        dtos.forEach(dto -> {
            var errorMessages = new ArrayList<String>();
            var studentId = dto.getStudentId().toUpperCase().trim();

            if (studentId.isBlank()) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.id.empty", null, Locale.getDefault()));
            } else {
                if (studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
//                    if (studentIdCounts.getOrDefault(studentId, 0L) > 1 && !reportedDuplicateStudentIds.contains(studentId)) {
//                        errorMessages.add(StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
//                                studentId + ModelConstants.SPACE +
//                                messageSource.getMessage("message.label.repeated.in.same.sheet", null, Locale.getDefault()));
//                        reportedDuplicateStudentIds.add(studentId);
//                    }

                    var existingStudent = studentDetailsInfoService.getStudentInfoDetails(studentId);
                    if (existingStudent.getStudentId() == null) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
                                studentId + ModelConstants.SPACE +
                                messageSource.getMessage("message.label.does.not.exist", null, Locale.getDefault()));
                    }
                } else {
                    errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.alphanumeric.student.id", null, Locale.getDefault()));
                }
            }

            String messHead = Optional.ofNullable(dto.getMessHead()).orElse("").trim();
            if (messHead.isEmpty()) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.mess.head.empty", null, Locale.getDefault()));
            } else {
                var messMaster = messMasterRepository.findFirstByActiveFlagAndMessHeadIgnoreCaseOrderByCreatedAtAsc(ModelConstants.STATUS_ACTIVE, dto.getMessHead());
                if (messMaster.isPresent()) {
                    var mess = messMaster.get();
                    dto.setMessId(String.valueOf(mess.getId()));
                } else {
                    errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.mess.head.not.exist", null, Locale.getDefault()));
                }
            }

            if (dto.getAmount() == null) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.amount.empty", null, Locale.getDefault()));
            } else if (dto.getAmount() <= 0) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.amount.positive", null, Locale.getDefault()));
            } else if (dto.getAmount() > 99999999L) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.amount.max.limit", null, Locale.getDefault()));
            }

            if (dto.getDate() == null) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.purchase.date.empty", null, Locale.getDefault()));
            } else {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT, Locale.ENGLISH);
                    formatter.format(dto.getDate());
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.purchase.date.invalid", null, Locale.getDefault()));
                }
            }
            allErrors.addAll(errorMessages);
        });
        return allErrors;
    }

    private List<FoodCourtLedgerDto> parseExcelFile(MultipartFile file, ArrayList<String> errorList) {
        var dtos = new ArrayList<FoodCourtLedgerDto>();
        try (var inputStream = file.getInputStream();
             var workbook = WorkbookFactory.create(inputStream)) {
            var sheet = workbook.getSheetAt(0);
            var headerRow = sheet.getRow(0);
            var headerErrors = validateHeaders(headerRow);
            if (!headerErrors.isEmpty()) {
                errorList.addAll(headerErrors);
                return dtos;
            }
            var rows = sheet.iterator();
            AtomicBoolean hasData = new AtomicBoolean(false);
            rows.forEachRemaining(row -> {
                double amount;
                if (row.getRowNum() == 0) return;
                if (!studentWithRemarksService.isRowEmpty(row)) hasData.set(true);
                else return;
                if (studentWithRemarksService.isMandatoryFieldsEmpty(row)) {
                    errorList.add(StudentConstants.ROW.getStudentConstant() +
                            ModelConstants.SPACE +
                            (row.getRowNum() + 1) + ModelConstants.SPACE +
                            messageSource.getMessage("message.label.file.missing.mandatory.fields", null, Locale.getDefault()));
                    return;
                }
                try {
                    amount = Double.parseDouble(studentWithRemarksService.getCellValueAsString(row.getCell(2)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    errorList.add(StudentConstants.ROW.getStudentConstant() + ModelConstants.SPACE + (row.getRowNum() + 1) + ModelConstants.SPACE +
                            messageSource.getMessage("message.label.amount.invalid.amount", null, Locale.getDefault()));
                    return;
                }
                var dto = FoodCourtLedgerDto.builder()
                        .studentId(studentWithRemarksService.getCellValueAsString(row.getCell(0)).replace('\u00A0', ' ').trim())
                        .messHead(studentWithRemarksService.getCellValueAsString(row.getCell(1)))
                        .amount(amount)
                        .date(studentWithRemarksService.parseDate(row.getCell(3), errorList, row.getRowNum() + 1))
                        .rowNumber(row.getRowNum()+1)
                        .build();

                dtos.add(dto);
            });
            if (!hasData.get()) {
                errorList.add(messageSource.getMessage("message.label.empty.sheet.note", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorList.add(messageSource.getMessage("message.validation.invalid.file.format", null, Locale.getDefault()));
        }
        return dtos;
    }

    private List<String> validateHeaders(Row headerRow) {
        List<String> expectedHeaders = Arrays.stream(ExcelConstants.FOOD_COURT_DEBIT_DATA)
                .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                .toList();
        DataFormatter formatter = new DataFormatter();
        List<String> actualHeaders = StreamSupport.stream(headerRow.spliterator(), false)
                .map(cell -> formatter.formatCellValue(cell).replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                .toList();
        boolean isInvalidFormat = IntStream.range(0, expectedHeaders.size())
                .anyMatch(i -> i >= actualHeaders.size() || !expectedHeaders.get(i).equalsIgnoreCase(actualHeaders.get(i)));
        if (isInvalidFormat) {
            return List.of(messageSource.getMessage("message.label.student.invalid.file.format", null, Locale.getDefault()));
        }
        return List.of();
    }

    @Transactional
    public void saveFoodCourtDebitDetails(FoodCourtLedgerDto foodCourtLedgerDto) {
        MessMasterControllerDto messMasterControllerDto = messMasterCommonService.getCurrentMessPeriod();
        FoodCourtLedgerEntity foodCourtLedgerEntity = new FoodCourtLedgerEntity();
        foodCourtLedgerEntity.setMessMaster(MessMasterMapper.INSTANCE
                .toMessMasterEntity(
                        messMasterService.getMessMasterDetailsById(Long.parseLong(foodCourtLedgerDto.getMessId()))
                ));
        foodCourtLedgerEntity.setStudent(StudentDetailsInfoMapper.INSTANCE
                .toEntity(
                        studentDetailsInfoService.getStudentInfoDetails(foodCourtLedgerDto.getStudentId().toUpperCase())
                ));
        foodCourtLedgerEntity.setMessPeriodId(Optional.ofNullable(messMasterControllerDto).map(MessMasterControllerDto::getId).orElse(0L));
        foodCourtLedgerEntity.setAmount(foodCourtLedgerDto.getAmount());
        foodCourtLedgerEntity.setPurchaseTimestamp(foodCourtLedgerDto.getDate().atStartOfDay());
        foodCourtLedgerEntity.setDebitOrCredit(Constants.DEBIT);
        foodCourtLedgerEntity.setDescription(messageSource.getMessage("message.label.food.court.bulk.insert.description", null, Locale.getDefault()));
        foodCourtLedgerRepository.save(foodCourtLedgerEntity);
    }


    public Page<FoodCourtTransactionDto> getFoodCourtTransactions(PaginationForm form, FoodCourtPurchaseForm searchForm) {
        // Handle null values with proper defaults
        String studentId = searchForm.getStudentId() != null ? searchForm.getStudentId() : null;
        String userRole = SecurityCtxUtil.userRole();
        Long messPeriodId = searchForm.getMessPeriodId() != null ? searchForm.getMessPeriodId().longValue() : null;
        Long foodCourtMessNameId = searchForm.getMessId() != null ? searchForm.getMessId().longValue() : 0;

        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        Page<MessInspectionReportDto> result = Page.empty();

        // Convert LocalDate to String if needed (adjust format as necessary)
        String fromDate = searchForm.getPurchasedFromDate() != null ?
                searchForm.getPurchasedFromDate().toString() : null;
        String toDate = searchForm.getPurchasedToDate() != null ?
                searchForm.getPurchasedToDate().toString() : null;

        // Add validation if needed
        if (messPeriodId == null) {
            throw new IllegalArgumentException("Mess period is required");
        }

        Page<Object[]> pageArray = foodCourtLedgerRepository.getFoodCourtTransactions(
                studentId,
                userRole,
                messPeriodId,
                Math.toIntExact(foodCourtMessNameId),
                fromDate,
                toDate, pageable);
        return pageArray.map(this::mapToFoodCourtPurchaseDto);
    }

    private FoodCourtTransactionDto mapToFoodCourtPurchaseDto(Object[] objects) {
        FoodCourtTransactionDto dto = new FoodCourtTransactionDto();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .toFormatter();

        dto.setVoucherNo((String) objects[0]);
        dto.setPurchaseDate(objects[1] != null ? LocalDateTime.parse((String) objects[1], formatter) : null);
        dto.setStudentId((String) objects[2]);
        dto.setMessName((String) objects[3]);
        dto.setDiningFromDate(objects[4] != null ? LocalDate.parse((String) objects[4]) : null);
        dto.setDiningToDate(objects[5] != null ? LocalDate.parse((String) objects[5]) : null);
        dto.setAmount(objects[6] != null ? ((Number) objects[6]).doubleValue() : null);
        dto.setDebitOrCredit((String) objects[7]);
        dto.setTotalPurchaseAmount(objects[8] != null ? ((Number) objects[8]).doubleValue() : null);
        dto.setTotalCreditAmount(objects[9] != null ? ((Number) objects[9]).doubleValue() : null);
        dto.setBalanceAmount(objects[10] != null ? ((Number) objects[10]).doubleValue() : null);
        return dto;
    }

    public Workbook getFoodCourtTransactionsReport(List<FoodCourtTransactionDto> content) throws Exception {
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.mess.food.court.purchase.list", null, Locale.getDefault()));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create second header row
            XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(2);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.mess.food.court.purchase.list", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

            // Create third header row for report date
            XSSFRow rowheadSecond = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();
            excelUtility.createCell(rowheadSecond, 0, messageSource.getMessage("message.label.report.date", null, Locale.getDefault()) + " : " + DateUtility.formatDate(date), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 9));

            XSSFRow rowheadThird = sheet.createRow(4);
           if (CollectionUtils.isNotEmpty(content)) {
               double totalCredit = content.stream()
                       .filter(t -> "c".equalsIgnoreCase(t.getDebitOrCredit()))
                       .map(FoodCourtTransactionDto::getAmount)
                       .filter(Objects::nonNull)
                       .mapToDouble(Double::doubleValue)
                       .sum();

               double totalDebit = content.stream()
                       .filter(t -> "d".equalsIgnoreCase(t.getDebitOrCredit()))
                       .map(FoodCourtTransactionDto::getAmount)
                       .filter(Objects::nonNull)
                       .mapToDouble(Double::doubleValue)
                       .sum();

               excelUtility.createCell(rowheadThird, 0,
                       messageSource.getMessage("message.mess.food.court.total.credit", null, Locale.getDefault())
                       + String.format("%,.2f", totalCredit) + "  "
                       + messageSource.getMessage("message.mess.food.court.total.debit", null, Locale.getDefault())
                       + String.format("%,.2f", totalDebit), headerStyle);
               sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 9));

           }
            // Create column headers
            XSSFRow rowhead = sheet.createRow(5);
            String[] headers = { messageSource.getMessage("message.label.mess.rebate.sl.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.voucher.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.purchase.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.studentID", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.mess.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.dining.from.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.dining.to.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.credit.amount", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.debit.amount", null, Locale.getDefault()),

            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 8000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowcount = 5;int slno = 1;
            if (CollectionUtils.isNotEmpty(content)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_MON_YEAR_TIME_FORMAT);

                for (FoodCourtTransactionDto dto : content) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);
                    excelUtility.createCell(row, 0, slno, dataStyle);
                    excelUtility.createCell(row, 1, dto.getVoucherNo(), dataStyle);
                    excelUtility.createCell(row, 2, dto.getPurchaseDate() != null ? dto.getPurchaseDate().format(formatter) : ModelConstants.NOT_APPLICABLE, dataStyle);
                    excelUtility.createCell(row, 3, dto.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 4, dto.getMessName(), dataStyle);
                    excelUtility.createCell(row, 5, DateUtility.formatDate(dto.getDiningFromDate()), dataStyle);
                    excelUtility.createCell(row, 6, DateUtility.formatDate(dto.getDiningToDate()), dataStyle);
                    excelUtility.createCell(row, 7, String.format("%.2f", dto.getAmount() != null && Constants.CREDIT.equalsIgnoreCase(dto.getDebitOrCredit()) ? dto.getAmount() : 0.0), dataStyle);
                    excelUtility.createCell(row, 8, String.format("%.2f", dto.getAmount() != null && Constants.DEBIT.equalsIgnoreCase(dto.getDebitOrCredit()) ? dto.getAmount() : 0.0), dataStyle);
                    slno++;
                }
            }

            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 10000) {
                    sheet.setColumnWidth(i, 10000);
                }
            }


        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating food court List report", exception);
        }
        return workbook;
    }
}
