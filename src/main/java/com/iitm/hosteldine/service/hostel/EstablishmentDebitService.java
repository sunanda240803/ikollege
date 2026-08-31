package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.transactions.EstablishmentDebitDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.student.StudentWithRemarksService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstablishmentDebitService {

    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final ExcelUtility excelUtility;
    private final MessLedgerARepository messLedgerARepository;
    private final MessLedgerBRepository messLedgerBRepository;
    private final AccountHeadService accountHeadService;
    private final AuditTrailService auditTrailService;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentWithRemarksService studentWithRemarksService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;

    public Page<EstablishmentDebitDto> getEstablishmentDebitList(PaginationForm form) {
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        String fixedOption = Optional.ofNullable(form.getAdditionalParam().get("fixed"))
                .map(Object::toString)
                .orElse(ModelConstants.EMPTY_STRING);
        Long hostelId = Optional.ofNullable(form.getAdditionalParam().get("hostelId"))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(0L);

        Double amount = Optional.ofNullable(form.getAdditionalParam().get("amount"))
                .map(Object::toString)
                .map(Double::parseDouble)
                .orElse(0.0);

        String description = Optional.ofNullable(form.getAdditionalParam().get("amount"))
                .map(Object::toString)
                .orElse(ModelConstants.EMPTY_STRING);

        LocalDate date = Optional.ofNullable(form.getAdditionalParam().get("date"))
                .map(dt -> LocalDate.parse(dt.toString()))
                .orElse(LocalDate.now());

        return getEstablishmentList(pageable, hostelId, fixedOption, amount, description, date);
    }

    private Page<EstablishmentDebitDto> getEstablishmentList(Pageable pageable, Long hostelId, String fixedOption, Double amount, String description, LocalDate date) {
        return ("fixed".equalsIgnoreCase(fixedOption) && hostelId != 0L)
                ? allStudentsDetailsViewRepository.getFixedEstablishmentDebitList(hostelId, pageable).map(entity -> returnPageDto(entity, amount, description, date))
                : allStudentsDetailsViewRepository.getExcludeEstablishmentDebitList(
                hostelId, ModelConstants.STATUS_ACTIVE, LocalDate.now(), pageable).map(entity -> returnPageDto(entity, amount, description, date));
    }

    private EstablishmentDebitDto returnPageDto(AllStudentsDetailsViewEntity entity, double amount, String description, LocalDate date) {
        EstablishmentDebitDto establishmentDebitDto = EstablishmentDebitDto.builder()
                .hostelName(entity.getHostelName())
                .studentId(entity.getStudentId())
                .roomNo(entity.getRoomNumber())
                .studentName(entity.getStudentName())
                .build();
        establishmentDebitDto.setAmount(amount);
        establishmentDebitDto.setDescription(description);
        establishmentDebitDto.setDate(date);
        return establishmentDebitDto;
    }

    public Workbook downloadExcelTemplate() {
        String sheetName = commonResponseUtil.getMessage("message.label.establishment.debit");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        String[] headerData = ExcelConstants.ESTABLISHMENT_DEBIT_DATA;
        String[] headerDataWidth = ExcelConstants.ESTABLISHMENT_DEBIT_DATA_WIDTH;
        excelUtility.setDataStyle(workbook);
        Row headerRow = sheet.createRow(0);
        excelUtility.createHeader(headerRow, 0, headerData, workbook);
        IntStream.range(0, headerDataWidth.length)
                .forEach(i -> sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i])));
        return workbook;
    }

    @Transactional
    public String saveEstablishmentDebitList(Pageable pageable, EstablishmentDebitDto establishmentDebitDto) {
        List<EstablishmentDebitDto> dtoList = getEstablishmentList(pageable, establishmentDebitDto.getHostelId(), establishmentDebitDto.getSelectedOption(), establishmentDebitDto.getAmount(), establishmentDebitDto.getDescription(), establishmentDebitDto.getDate()).getContent();
        AtomicLong nextVoucherNo = new AtomicLong(messLedgerARepository.getNextValMessLedger());
        dtoList.forEach(dto -> dto.setVoucherNo(String.valueOf(nextVoucherNo.getAndIncrement())));
        var finYearDto = accountHeadService.getFinYearDto();
        List<MessLedgerAEntity> messLedgerAEntityList = buildMessLedgerAList(dtoList, finYearDto);
        messLedgerARepository.saveAllAndFlush(messLedgerAEntityList);
        List<MessLedgerBEntity> messLedgerBEntityList = buildMessLedgerBList(dtoList, finYearDto);
        messLedgerBRepository.saveAll(messLedgerBEntityList);
        boolean isUserAuditTrailSaved = saveUserAuditTrial();
        return isUserAuditTrailSaved ? Constants.SAVED : Constants.ERROR;
    }


    private List<MessLedgerAEntity> buildMessLedgerAList(List<EstablishmentDebitDto> dtoList, FinancialYearDto finYearDto) {
        return dtoList.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setAccHead(Constants.ODEP);
                    txnDto.setDescription1(dto.getStudentId() + ModelConstants.SPACE + dto.getAmount());
                    txnDto.setDocRefNo(dto.getStudentId() + ModelConstants.SPACE + dto.getAmount());
                    txnDto.setDebitOrCredit(Constants.DEBIT);
                    txnDto.setScreenType(Constants.ESTABLISHMENT_DEBIT);
                    return TransferAmountUtils.createMessLedgerAEntity(txnDto, finYearDto.getFinYear());
                })
                .toList();
    }

    private List<MessLedgerBEntity> buildMessLedgerBList(List<EstablishmentDebitDto> dtoList, FinancialYearDto finYearDto) {
        return dtoList.stream()
                .map(dto -> {
                    TransactionDto txnDto = new TransactionDto();
                    populateCommonTransactionFields(txnDto, dto, finYearDto);
                    txnDto.setSlNo(1);
                    txnDto.setAccHead(dto.getStudentId());
                    txnDto.setDescription1(String.valueOf(dto.getHostelId()));
                    txnDto.setDocRefNo(dto.getStudentId() + ModelConstants.SPACE + dto.getAmount());
                    txnDto.setDebitOrCredit(Constants.CREDIT);
                    return TransferAmountUtils.createMessLedgerBEntity(txnDto, finYearDto.getFinYear(), 0, 1);
                })
                .toList();
    }

    private void populateCommonTransactionFields(TransactionDto txnDto, EstablishmentDebitDto dto, FinancialYearDto finYearDto) {
        txnDto.setFinYear(finYearDto.getFinYear());
        txnDto.setVoucherNo(dto.getVoucherNo());
        txnDto.setBookType(Constants.MESS_MS);
        txnDto.setDate(dto.getDate());
        txnDto.setSubAccHead(Constants.NIL);
        txnDto.setDescription(dto.getDescription());
        txnDto.setAmount(dto.getAmount());
        txnDto.setRecon(ModelConstants.STATUS_INACTIVE);
        txnDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
    }

    private boolean saveUserAuditTrial() {
        return auditTrailService.saveAuditTrail("EstablishmentDebitForm", commonResponseUtil.getMessage("url.establishment.debit"),
                this.getClass().getName() + Constants.HYPHEN + "saveEstablishmentDebitList()");
    }

    @Transactional
    public EstablishmentDebitDto saveEstablishmentDebitUpload(EstablishmentDebitDto establishmentDebitDtoParam) {
        EstablishmentDebitDto establishmentDebitDto = EstablishmentDebitDto.builder().build();
        var errorList = new ArrayList<String>();
        try {
            var dtos = parseExcelFile(establishmentDebitDtoParam.getFile(), errorList);

            if (errorList.isEmpty()) {
                errorList.addAll(validateDtos(dtos));
            }
            if (!errorList.isEmpty()) {
                establishmentDebitDto.setErrorList(errorList);
                return establishmentDebitDto;
            }
            dtos.forEach(val -> setParamsValue(val, establishmentDebitDtoParam));
            saveExcelFile(dtos);
        } catch (Exception e) {
            errorList.add(commonResponseUtil.getMessage("message.validation.error.invalid.file.type"));
            establishmentDebitDto.setErrorList(errorList);
        }
        return establishmentDebitDto;
    }

    private void setParamsValue(EstablishmentDebitDto val, EstablishmentDebitDto establishmentDebitDtoParam) {
        val.setDate(establishmentDebitDtoParam.getDate());
        val.setHostelId(establishmentDebitDtoParam.getHostelId());
        val.setDescription(establishmentDebitDtoParam.getDescription());
        val.setTotalAmount(establishmentDebitDtoParam.getTotalAmount());
        val.setSelectedOption(establishmentDebitDtoParam.getSelectedOption());
    }

    private void saveExcelFile(List<EstablishmentDebitDto> dtos) {
        var finYearDto = accountHeadService.getFinYearDto();
        AtomicLong nextVoucherNo = new AtomicLong(messLedgerARepository.getNextValMessLedger());
        dtos.forEach(dto -> dto.setVoucherNo(String.valueOf(nextVoucherNo.getAndIncrement())));
        messLedgerARepository.saveAll(buildMessLedgerAList(dtos, finYearDto));
        messLedgerBRepository.saveAll(buildMessLedgerBList(dtos, finYearDto));
        saveUserAuditTrial();
    }

    private List<EstablishmentDebitDto> parseExcelFile(MultipartFile file, List<String> errorList) {
        var dtos = new ArrayList<EstablishmentDebitDto>();
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
                if (!studentWithRemarksService.isRowEmpty(row)) hasData.set(true); else return;
                if (isMandatoryFieldsEmpty(row)) {
                    errorList.add(StudentConstants.ROW.getStudentConstant() +
                            ModelConstants.SPACE +
                            (row.getRowNum() + 1) + ModelConstants.SPACE +
                            commonResponseUtil.getMessage("message.label.file.missing.mandatory.fields"));
                    return;
                }
                var dto = EstablishmentDebitDto.builder().build();
                dto.setRowNumber(row.getRowNum()+1);
                dto.setStudentId(studentWithRemarksService.getCellValueAsString(row.getCell(0)).replace('\u00A0', ' ').trim());
                dto.setAmount(Double.parseDouble(studentWithRemarksService.getCellValueAsString(row.getCell(1))));
                dtos.add(dto);
            });
            if (!hasData.get()) {
                errorList.add(commonResponseUtil.getMessage("message.label.empty.sheet.note"));
            }
        } catch (Exception e) {
            errorList.add(commonResponseUtil.getMessage("message.validation.error.invalid.file.type"));
        }
        return dtos;
    }

    private List<String> validateHeaders(Row headerRow) {
        List<String> expectedHeaders = Arrays.stream(ExcelConstants.ESTABLISHMENT_DEBIT_DATA)
                .map(header -> header.replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                .toList();
        DataFormatter formatter = new DataFormatter();
        List<String> actualHeaders = StreamSupport.stream(headerRow.spliterator(), false)
                .map(cell -> formatter.formatCellValue(cell).replaceAll(ModelConstants.NON_ALPHA_NUMERIC, ModelConstants.EMPTY_STRING).trim())
                .toList();
        boolean isInvalidFormat = IntStream.range(0, expectedHeaders.size())
                .anyMatch(i -> i >= actualHeaders.size() || !expectedHeaders.get(i).equalsIgnoreCase(actualHeaders.get(i)));

        if (isInvalidFormat) {
            return List.of("Invalid file format");
        }
        return List.of();
    }

    public List<String> validateDtos(List<EstablishmentDebitDto> dtos) {
        Map<String, Long> studentIdCounts = dtos.stream()
                .map(EstablishmentDebitDto::getStudentId)
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Set<String> reportedDuplicateStudentIds = new HashSet<>();
        List<String> allErrors = new ArrayList<>();

        dtos.forEach(dto -> {
            var errorMessages = new ArrayList<String>();
            var studentId = dto.getStudentId().toUpperCase();
            if (studentId.isBlank()) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ commonResponseUtil.getMessage("message.label.student.id.empty"));
            } else {
                if (studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
                    if (studentIdCounts.getOrDefault(studentId, 0L) > 1 && !reportedDuplicateStudentIds.contains(studentId)) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
                                studentId + ModelConstants.SPACE +
                                commonResponseUtil.getMessage("message.label.repeated.in.same.sheet"));
                        reportedDuplicateStudentIds.add(studentId);
                    }

                    var existingStudent = studentDetailsInfoService.getStudentInfoDetails(studentId);
                    if (existingStudent.getStudentId() == null) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ StudentConstants.STUDENT_ID.getStudentConstant() + ModelConstants.SPACE +
                                studentId + ModelConstants.SPACE +
                                commonResponseUtil.getMessage("message.label.does.not.exist"));
                    }
//                    var previousStudent = studentDetailsInfoService.getStudentPreviousInfoDetails(Objects.requireNonNull(existingStudent).getPreviousId());
                    if (studentDetailsInfoRepository.checkStudentIdExistInPreviousId(studentId, ModelConstants.STATUS_ACTIVE).isPresent()) {
                        errorMessages.add("Row number "+dto.getRowNumber() + " : "+ commonResponseUtil.getMessage("message.label.student.id.changed") + ModelConstants.SPACE + studentId);
                    }

                    if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(ModelConstants.STATUS_ACTIVE, studentId, ModelConstants.YES)) {
                        errorMessages.add(BulkAllotmentStudentsConstants.SETTLEMENT_COMPLETED_FOR_THE_STUDENT.getString() + ModelConstants.SPACE + studentId);
                    }
                } else {
                    errorMessages.add("Row number "+dto.getRowNumber() + " : "+ commonResponseUtil.getMessage("message.label.alphanumeric.student.id"));
                }
            } if (dto.getAmount() <= 0) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ commonResponseUtil.getMessage("message.label.amount.positive"));
            } else if (dto.getAmount() > 99999999L) {
                errorMessages.add("Row number "+dto.getRowNumber() + " : "+ commonResponseUtil.getMessage("message.label.amount.max.limit"));
            }
            allErrors.addAll(errorMessages);
        });
        return allErrors;
    }

    public boolean isMandatoryFieldsEmpty(Row row) {
        return studentWithRemarksService.isCellEmpty(row.getCell(0)) ||
                studentWithRemarksService.isCellEmpty(row.getCell(1));
    }
}
