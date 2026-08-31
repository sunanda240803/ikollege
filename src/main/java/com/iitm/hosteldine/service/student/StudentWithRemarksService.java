package com.iitm.hosteldine.service.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.dashboard.student.StudentBlackListDetailMapper;
import com.iitm.hosteldine.model.dashboard.student.StudentBlackListDetailEntity;
import com.iitm.hosteldine.repository.dashboard.student.StudentBlackListDetailRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.util.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class StudentWithRemarksService {
    private final StudentBlackListDetailRepository studentBlackListDetailRepository;
    private final MessageSource messageSource;
    private final ExcelUtility excelUtility;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;


    public Page<StudentBlackListDetailDto> getStudentWithRemarksList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("createdAt").descending());
        Page<StudentBlackListDetailDto> pageResult = Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(search -> studentBlackListDetailRepository.findByActiveFlagAndSearch(
                        ModelConstants.STATUS_ACTIVE, pageRequest, search))
                .orElseGet(() -> studentBlackListDetailRepository.findAllByActiveFlag(
                        ModelConstants.STATUS_ACTIVE, pageRequest));
        pageResult.getContent().forEach(dto -> {
            if (dto.getCreatedAt() != null) {
                LocalDateTime localDateTime = dto.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime adjustedDateTime = localDateTime.atZone(ZoneId.systemDefault()).toLocalDateTime();
                dto.setCreatedAt(adjustedDateTime);
            } else {
                dto.setCreatedAt(null);
            }
        });
        return pageResult;
    }

    public StudentBlackListDetailDto getStudentBlackListById(Long id) {
        return studentBlackListDetailRepository.findById(id).map(StudentBlackListDetailMapper.INSTANCE::toDto).orElse(new StudentBlackListDetailDto());
    }

    public String saveOrStudentBlackList(StudentBlackListDetailDto studentBlackListDetailDto) {
        return Optional.ofNullable(studentBlackListDetailDto.getId())
                .filter(id -> id > 0L)
                .flatMap(studentBlackListDetailRepository::findById)
                .map(existingEntity -> {
                    StudentBlackListDetailMapper.INSTANCE.onUpdateEntity(existingEntity, studentBlackListDetailDto);
                    existingEntity.setStudentId(studentBlackListDetailDto.getStudentId().toUpperCase());
                    studentBlackListDetailRepository.save(existingEntity);
                    return Constants.UPDATED;
                })
                .orElseGet(() -> {
                    StudentBlackListDetailEntity entity = StudentBlackListDetailMapper.INSTANCE.toEntity(studentBlackListDetailDto);
                    entity.setStudentId(studentBlackListDetailDto.getStudentId().toUpperCase());
                    entity.setCurrentlyActive(StudentConstants.ACTIVE.getStudentConstant());
                    entity.onCreate();
                    studentBlackListDetailRepository.save(entity);
                    return Constants.SAVED;
                });
    }

    public boolean deleteStudentBlackList(Long id) throws RecordNotExistsException {
        return studentBlackListDetailRepository.findById(id)
                .map(entity->{
                    entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                    studentBlackListDetailRepository.save(entity);
                    return true;
                })
                .orElseThrow(() -> new RecordNotExistsException(
                        messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
                        )));
    }

    public boolean revokeStudentBlackList(Long id) throws RecordNotExistsException {
        return studentBlackListDetailRepository.findById(id)
                .map(entity->{
                    entity.setCurrentlyActive(StudentConstants.REVOKED.getStudentConstant());
                    entity.setCurrentlyActiveDate(LocalDate.now(ZoneId.systemDefault()));
                    studentBlackListDetailRepository.save(entity);
                    return true;
                })
                .orElseThrow(() -> new RecordNotExistsException(
                        messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
                        )));
    }

    public Workbook downloadStudentRemarkUploadTemplate() {
        String sheetName = StudentConstants.STUDENT_WITH_REMARK_FILE_NAME.getStudentConstant();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        String[] headerData = ExcelConstants.STUDENT_WITH_REMARK_DATA;
        String[] headerDataWidth = ExcelConstants.STUDENT_WITH_REMARK_DATA_WIDTH;
        excelUtility.setDataStyle(workbook);
        Row headerRow = sheet.createRow(0);
        excelUtility.createHeader(headerRow, 0, headerData, workbook);
        IntStream.range(0, headerDataWidth.length)
                .forEach(i -> sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i])));
        return workbook;
    }

    public StudentBlackListDetailDto saveStudentBlacklistBulkUpload(MultipartFile file) {
        var studentBlackListDetailDto = new StudentBlackListDetailDto();
        var errorList = new ArrayList<String>();
        try {
            var dtos = parseExcelFile(file, errorList);
            if (errorList.isEmpty()) {
                errorList.addAll(validateDtos(dtos));
            }
            if (!errorList.isEmpty()) {
                studentBlackListDetailDto.setErrorList(errorList);
                return studentBlackListDetailDto;
            }
            var savedDtos = dtos.stream()
                    .peek(this::saveOrStudentBlackList)
                    .toList();
        } catch (Exception e) {
            errorList.add(messageSource.getMessage("message.validation.error.invalid.file.type", null, Locale.getDefault()));
            studentBlackListDetailDto.setErrorList(errorList);
        }
        return studentBlackListDetailDto;
    }

    private List<StudentBlackListDetailDto> parseExcelFile(MultipartFile file, List<String> errorList) {
        var dtos = new ArrayList<StudentBlackListDetailDto>();
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
                if (row.getRowNum() == 0) return;
                if (!isRowEmpty(row)) hasData.set(true); else return;
                if (isMandatoryFieldsEmpty(row)) {
                    errorList.add(StudentConstants.ROW.getStudentConstant() +
                            ModelConstants.SPACE +
                            (row.getRowNum() + 1) + ModelConstants.SPACE +
                            messageSource.getMessage("message.label.file.missing.mandatory.fields", null, Locale.getDefault()));
                    return;
                }
                var dto = StudentBlackListDetailDto.builder()
                        .studentId(getCellValueAsString(row.getCell(0)).replace('\u00A0', ' ').trim())
                        .fromDate(parseDate(row.getCell(1), errorList, row.getRowNum() + 1))
                        .toDate(parseDate(row.getCell(2), errorList, row.getRowNum() + 1))
                        .remarkDescription(getCellValueAsString(row.getCell(3)))
                        .rowNumber(row.getRowNum()+1)
                        .build();
                dtos.add(dto);
            });
            if (!hasData.get()) {
                errorList.add(messageSource.getMessage("message.label.empty.sheet.note", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            errorList.add(messageSource.getMessage("message.validation.error.invalid.file.type", null, Locale.getDefault()));
        }
        return dtos;
    }

    private List<String> validateHeaders(Row headerRow) {
        List<String> expectedHeaders = Arrays.stream(ExcelConstants.STUDENT_WITH_REMARK_DATA)
                .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim()) // Remove non-alphanumeric and trim
                .toList();
        DataFormatter formatter = new DataFormatter();
        List<String> actualHeaders = StreamSupport.stream(headerRow.spliterator(), false)
                .map(cell -> formatter.formatCellValue(cell).replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim()) // Remove non-alphanumeric and trim
                .toList();
        boolean isInvalidFormat = IntStream.range(0, expectedHeaders.size())
                .anyMatch(i -> i >= actualHeaders.size() || !expectedHeaders.get(i).equalsIgnoreCase(actualHeaders.get(i)));

        if (isInvalidFormat) {
            return List.of("Invalid file format");
        }
        return List.of();
    }



    public List<String> validateDtos(List<StudentBlackListDetailDto> dtos) {
        Map<String, Long> studentIdCounts = dtos.stream()
                .map(StudentBlackListDetailDto::getStudentId)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Set<String> reportedDuplicateStudentIds = new HashSet<>();
        List<String> allErrors = new ArrayList<>();

        dtos.forEach(dto -> {
            var errorMessages = new ArrayList<String>();
            var studentId = dto.getStudentId().toUpperCase();
            var fromDate = dto.getFromDate();
            var toDate = dto.getToDate();

            if (studentId.isBlank()) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.id.empty", null, Locale.getDefault()));
            } else {
                if (studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
                    if (studentIdCounts.getOrDefault(studentId, 0L) > 1 && !reportedDuplicateStudentIds.contains(studentId)) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
                                studentId + ModelConstants.SPACE +
                                messageSource.getMessage("message.label.repeated.in.same.sheet", null, Locale.getDefault()));
                        reportedDuplicateStudentIds.add(studentId);
                    }

                    var existingStudent = studentDetailsInfoService.getStudentInfoDetails(studentId);
                    if (existingStudent.getStudentId() == null) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
                                studentId + ModelConstants.SPACE +
                                messageSource.getMessage("message.label.does.not.exist", null, Locale.getDefault()));
                    }
//                    var previousStudent = studentDetailsInfoRepository.checkStudentIdExistInPreviousId(existingStudent.getStudentId().toUpperCase());
                    if (!studentDetailsInfoRepository.checkStudentIdExistInPreviousId(studentId, ModelConstants.STATUS_ACTIVE).isEmpty()) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.id.changed", null, Locale.getDefault()) + ModelConstants.SPACE + studentId);
                    }
                } else {
                    errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.alphanumeric.student.id", null, Locale.getDefault()));
                }
            }
            if (fromDate == null) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.from.date.empty", null, Locale.getDefault()) + ModelConstants.SPACE + studentId);
            } else if (toDate == null) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.to.date.empty", null, Locale.getDefault()) + ModelConstants.SPACE + studentId);
            } else {
                if (fromDate.isAfter(toDate)) {
                    errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.date.compare", null, Locale.getDefault()) + ModelConstants.SPACE + studentId);
                } else {
                    var duplicateEntry = studentBlackListDetailRepository.getStudentDetailsInfo(
                            fromDate, toDate, studentId, ModelConstants.STATUS_ACTIVE, StudentConstants.ACTIVE.getStudentConstant());
                    if (duplicateEntry.isPresent()) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ messageSource.getMessage("message.label.student.remarks.for.given.date", null, Locale.getDefault()) + ModelConstants.SPACE + studentId);
                    }
                }
            }
            allErrors.addAll(errorMessages);
        });
        return allErrors;
    }

    public Optional<StudentBlackListDetailEntity> getStudentDuplicateDateBlacklistInfo(LocalDate fromDate, LocalDate toDate, String studentId) {
        return studentBlackListDetailRepository.getStudentDetailsInfo(fromDate, toDate, studentId, ModelConstants.STATUS_ACTIVE, StudentConstants.ACTIVE.getStudentConstant());
    }

    public Optional<StudentBlackListDetailEntity> getStudentDateBlacklistDetails(String studentId) {
        return studentBlackListDetailRepository.getStudentBlackListDetails(LocalDate.now(), studentId, ModelConstants.STATUS_ACTIVE, StudentConstants.ACTIVE.getStudentConstant());
    }

    public LocalDate parseDate(Cell cell, List<String> errorList, int rowNum) {
        if (cell == null) {
            errorList.add(StudentConstants.ROW.getStudentConstant() + ModelConstants.SPACE + rowNum +
                    messageSource.getMessage("message.label.student.date.cell.empty", null, Locale.getDefault()));
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } if (cell.getCellType() == CellType.STRING) {
                String dateString = cell.getStringCellValue().trim();

                LocalDate parsedDate = tryParseWithFormats(dateString);

                if (parsedDate != null) {
                    return parsedDate;
                }

                errorList.add(StudentConstants.ROW.getStudentConstant() + " " + rowNum + ": " +
                        messageSource.getMessage("message.label.student.invalid.date.format", null, Locale.getDefault()));
                return null;
            }
            errorList.add(StudentConstants.ROW.getStudentConstant() + ModelConstants.SPACE + rowNum + ModelConstants.COLAN + ModelConstants.SPACE +
                    messageSource.getMessage("message.label.student.invalid.date.format", null, Locale.getDefault()));
            return null;
        } catch (Exception e) {
            errorList.add(StudentConstants.ROW.getStudentConstant() + ModelConstants.SPACE + rowNum +
                    messageSource.getMessage("message.label.student.invalid.date.format", null, Locale.getDefault()));
            return null;
        }
    }

    public String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> ModelConstants.EMPTY_STRING;
        };
    }

    public boolean isRowEmpty(Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .allMatch(this::isCellEmpty);
    }

    public boolean isMandatoryFieldsEmpty(Row row) {
        return isCellEmpty(row.getCell(0)) ||
                isCellEmpty(row.getCell(1)) ||
                isCellEmpty(row.getCell(2)) ||
                isCellEmpty(row.getCell(3));
    }

    public boolean isCellEmpty(Cell cell) {
        return cell == null || switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim().isEmpty();
            case NUMERIC, BOOLEAN -> false;
            case FORMULA -> {
                try {
                    yield cell.getCachedFormulaResultType() == CellType.BLANK;
                } catch (Exception e) {
                    yield true;
                }
            }
            default -> true;
        };
    }

    private LocalDate tryParseWithFormats(String dateString) {
        for (String pattern : Constants.SUPPORTED_DATE_FORMATS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException ignored) { }
        }
        return null;
    }

}
