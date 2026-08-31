package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.*;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.dto.hostel.StudentPurchaseEventDto;
import com.iitm.hosteldine.dto.student.StudentChargesDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.studentDashboard.LedgerReportDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntityId;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntityId;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.entity.student.UserFpCardEntity;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.SelectForm;
import com.iitm.hosteldine.form.common.SettlementFormDTO;
import com.iitm.hosteldine.form.common.StudentDebitForm;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.model.SettlementHistoryEntity;
import com.iitm.hosteldine.model.StudentNetBalanceEntity;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import com.iitm.hosteldine.model.hostel.ShowEventPurchaseClaim;
import com.iitm.hosteldine.model.hostel.StudentHostelRoomVacatingRequestViewEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.ArchiveTableMasterRepository;
import com.iitm.hosteldine.repository.SettlementHistoryEntityRepository;
import com.iitm.hosteldine.repository.StudentNetBalanceEntityRepository;
import com.iitm.hosteldine.repository.dean.DeanHdcComplaintRepository;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.hostel.ShowEventMasterRepository;
import com.iitm.hosteldine.repository.hostel.ShowEventPurchaseClaimRepository;
import com.iitm.hosteldine.repository.hostel.StudentHostelRoomVacatingRequestViewRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.repository.student.UserFpCardRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessOpeningBalService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NocVacationPaymentService {

    private final MessLedgerARepository messLedgerARepository;
    private final MessLedgerBRepository messLedgerBRepository;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final MessMasterCommonService messMasterCommonService;
    private final MessOpeningBalService messOpeningBalService;
    private final Utility utility;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentHostelRoomVacatingRequestViewRepository studentHostelRoomVacatingRequestViewRepository;
    private final ShowStudentDetailRepository showStudentDetailRepository;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final FinancialYearRepository financialYearRepository;
    private final SimsConfigDataService smsConfigDataService;
    private final TransferAmountUtils transferAmountUtils;
    private final UserFpCardRepository userFpCardRepository;
    private final SettlementHistoryEntityRepository settlementHistoryEntityRepository;
    private final AuditTrailService auditTrailService;
    private final StudentNetBalanceEntityRepository studentNetBalanceEntityRepository;
    private final ExcelUtility excelUtility;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final ShowEventMasterRepository showEventMasterRepository;
    private final ShowEventPurchaseClaimRepository showEventPurchaseClaimRepository;
    private final DeanHdcComplaintRepository deanHdcComplaintRepository;


    public Map<String, Map<String, Object>> getLedgerReport(MessLedgerReportSelectForm selectForm, boolean isReport) {
        Stream<LedgerReportDto> ledgerList = getLedgerReportList(selectForm, isReport).stream();
            var ledgerReport = ledgerList
                    .collect(Collectors.groupingBy(this::organizeBySemester, LinkedHashMap::new, Collectors.toList()));

            return ledgerReport.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> calculateTransactions(entry.getValue()),
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));
    }

    public List<LedgerReportDto> getLedgerReportList(MessLedgerReportSelectForm selectForm, boolean isReport){
        var studentId = selectForm.getStudentID();
        List<String> accountHeads = new ArrayList<>();
        var bookType = Constants.MESS.equalsIgnoreCase(selectForm.getSelectedCriteria()) ? Constants.MESS_MS : Constants.CREDIT_CARD;
        accountHeads.add(studentId);
        var studentInfo = studentDetailsInfoService.getStudentInfoWithoutActive(studentId);
        Optional.ofNullable(studentInfo)
                .map(StudentDetailsInfoDto::getPreviousId)
                .filter(id -> !id.isEmpty())
                .ifPresent(previousId -> accountHeads.addAll(Arrays.asList(previousId.split(ModelConstants.COMMA))));
        var runningBalance = new AtomicReference<>(messOpeningBalService.getOpeningBal());

        Optional<MessLedgerBEntity> firstRecord = messLedgerBRepository.findFirstByActiveFlagAndAccheadInOrderByVoucherDate(ModelConstants.STATUS_ACTIVE, accountHeads);
        List<LedgerReportDto> ledgerList;
        if (firstRecord.isPresent()) {
             ledgerList = messLedgerARepository.getLedgerReport(bookType, accountHeads)
                    .stream()
                    .map(this::mapToLedgerReportDto)
                    .sorted(Comparator.comparing(LedgerReportDto::getVoucherDate).thenComparing(LedgerReportDto::getCreatedAt))
                    .map(dto -> setClosingBalance(dto, runningBalance)).toList();
            if (selectForm.getFromDate() != null && selectForm.getToDate() != null) {
                ledgerList = ledgerList.stream()
                        .filter(it -> it.getVoucherDate().isAfter(selectForm.getFromDate()) && it.getVoucherDate().isBefore(selectForm.getToDate())).toList();
            }
        } else{
            ledgerList = new ArrayList<>();
        }
        return ledgerList;
    }

    public LedgerReportDto getTotalLedgerSummary(Map<String, Map<String, Object>> ledgerReport) {
        double totalCredit = 0.00d;
        double totalDebit = 0.00d;
        double totalClosingBalance = 0.00d;
        String closingDirection = ModelConstants.CR;

        Pattern pattern = Pattern.compile("Cr|Dr", Pattern.CASE_INSENSITIVE);
        for (Map<String, Object> yearData : ledgerReport.values()) {
            Double yearCredit = (Double) yearData.getOrDefault(Constants.TOTAL_CREDIT, 0.00d);
            Double yearDebit = (Double) yearData.getOrDefault(Constants.TOTAL_DEBIT, 0.00d);
            String closingBalStr = (String) yearData.getOrDefault(Constants.TOTAL_CLOSING_BAL, ModelConstants.TWO_DECIMAL_ZERO);
            totalCredit += yearCredit;
            totalDebit += yearDebit;
            try {
                Matcher matcher = pattern.matcher(closingBalStr);
                if (matcher.find()) {
                    closingDirection = matcher.group();
                }
                closingBalStr = closingBalStr.replaceAll(ModelConstants.decimalNumericPatternWithHyphen, ModelConstants.EMPTY_STRING);
                totalClosingBalance = Double.parseDouble(closingBalStr);
            } catch (Exception ignored) {}
        }

        return LedgerReportDto.builder()
                .totalCreditAmount(totalCredit)
                .totalPurchaseAmount(totalDebit)
                .closingBalance(totalClosingBalance)
                .closingDirection(closingDirection)
                .build();
    }


    public StudentChargesDto getStudentChargesAndAmountDetails(SelectForm selectForm) {
        if (selectForm == null) {
            return new StudentChargesDto();
        }

        var studentId = selectForm.getStudentID().toUpperCase();
        if (studentId == null) {
            return new StudentChargesDto();
        }

        StudentDetailsInfoDto studentInfo = studentDetailsInfoService.getStudentInfoWithoutActive(studentId);
        if (studentInfo == null) {
            studentInfo = new StudentDetailsInfoDto();
        }

        /* settlement flag details */
        StudentChargesDto studentChargesDto = new StudentChargesDto();
        studentChargesDto.setStudentID(studentId);
        studentChargesDto.setStudentName(studentInfo.getStudentName()!=null ? studentInfo.getStudentName() : null);
        studentChargesDto.setSettlementFlag(studentInfo.getSettlementFlag() != null ? studentInfo.getSettlementFlag().trim() : null);
        studentChargesDto.setAllocationStatus(studentInfo.getDayScholar());
        boolean isSettlementEligible = StringUtils.equalsIgnoreCase(studentChargesDto.getSettlementFlag(), ModelConstants.STATUS_INACTIVE)
                && selectForm.getSelectedCriteria() != null
                && selectForm.getSelectedCriteria().equals("Mess");
        boolean isUndoSettlementEligible = StringUtils.equalsIgnoreCase(studentChargesDto.getSettlementFlag(), ModelConstants.STATUS_ACTIVE)
                && selectForm.getSelectedCriteria() != null
                && selectForm.getSelectedCriteria().equals("Mess");
        studentChargesDto.setIsSettlementEligible(isSettlementEligible);
        studentChargesDto.setIsUndoSettlementEligible(isUndoSettlementEligible);

        /* Hostel details */
        AllStudentsDetailsViewEntity studentDetail=new AllStudentsDetailsViewEntity();
        Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(studentId);
        if(studentDetailOpt.isPresent()) {
            studentDetail = studentDetailOpt.get();

            studentChargesDto.setRoomNo(studentDetail.getRoomNumber()!=null ?studentDetail.getRoomNumber() : null);
            studentChargesDto.setHostelName(studentDetail.getHostelName()!=null ? studentDetail.getHostelName() : null);
        }
        /* Hostel deposit refund amount */
        String previousId = StringUtils.isNotBlank(studentInfo.getPreviousId()) ? studentInfo.getPreviousId() : "";
        studentChargesDto.setStudentPreviousID(previousId);
        List<Object[]> studentLeadAmount = messLedgerARepository.getStudentLeadAmount(studentId, previousId);
        if (CollectionUtils.isNotEmpty(studentLeadAmount)) {
            Object[] objects = studentLeadAmount.get(0);
            if (objects != null && objects.length > 0 && objects[0] != null) {
                try {
                    studentChargesDto.setLeadAmount(Double.valueOf(objects[0].toString()).longValue());
                } catch (NumberFormatException e) {
                    studentChargesDto.setLeadAmount(0L);
                }
            }
        }
        /* Card amount */
        List<Object[]> studentCardAmount = messLedgerARepository.getStudentCardAmount(studentId, previousId);
        if (CollectionUtils.isNotEmpty(studentCardAmount)) {
            Object[] objects = studentCardAmount.get(0);
            //studentChargesDto.setCardAmount(Double.valueOf(objects[0].toString()).longValue());
        }

        Optional<StudentNetBalanceEntity> studentNetBalanceEntityOptional = studentNetBalanceEntityRepository.findByStudentId(studentId);
        if (studentNetBalanceEntityOptional.isPresent()) {
            StudentNetBalanceEntity balanceEntity = studentNetBalanceEntityOptional.get();
            studentChargesDto.setCardAmount(balanceEntity.getNetBalanceCard() != null ? balanceEntity.getNetBalanceCard().longValue() : 0L);
        }
        /* Penalty and Donation amount */
        Optional<StudentHostelRoomVacatingRequestViewEntity> studentCharges = studentHostelRoomVacatingRequestViewRepository
                .getStudentCharges(studentId, ModelConstants.STATUS_ACTIVE);

        if (studentCharges.isPresent()) {
            StudentHostelRoomVacatingRequestViewEntity studentChargesEntity = studentCharges.get();
            Long penaltyAmount = Optional.ofNullable(studentChargesEntity.getPenalityAmount()).orElse(0L) -
                    Optional.ofNullable(studentChargesEntity.getPenaltyAmountCollected()).orElse(0L);
            Long donationAmount = Optional.ofNullable(studentChargesEntity.getDonationAmount()).orElse(0L) -
                    Optional.ofNullable(studentChargesEntity.getDonationAmountCollected()).orElse(0L);

            studentChargesDto.setPenaltyChargesAmount(penaltyAmount);

            if (studentChargesEntity.getDonatorType() != null) {
                if (StringUtils.equalsIgnoreCase(studentChargesEntity.getDonatorType(), Constants.HOSTEL)) {
                    studentChargesDto.setDonatedTo(studentChargesEntity.getDonatorType() + " - " +
                            (studentChargesEntity.getDonatedHostel() != null ? studentChargesEntity.getDonatedHostel() : ""));
                } else if (StringUtils.equalsIgnoreCase(studentChargesEntity.getDonatorType(), Constants.OTHERS)) {
                    studentChargesDto.setDonatedTo(studentChargesEntity.getDonatorType() + " - " +
                            (studentChargesEntity.getOthersDescription() != null ? studentChargesEntity.getOthersDescription() : ""));
                } else {
                    studentChargesDto.setDonatedTo(studentChargesEntity.getDonatorType() != null ?
                            studentChargesEntity.getDonatorType() : "");
                }
            }

            studentChargesDto.setDonationAmount(donationAmount);
        }

        /* Purchase amount */
        List<StudentPurchaseEventDto> eventDtoList = new ArrayList<>();
        List<Object[]> studentPurchaseInfo = showStudentDetailRepository.getStudentPurchaseInfo(studentId);

        if (CollectionUtils.isNotEmpty(studentPurchaseInfo)) {
            for (Object[] objects : studentPurchaseInfo) {
                if (objects != null && objects.length > 3) {
                    StudentPurchaseEventDto studentPurchaseEventDto = new StudentPurchaseEventDto();
                    try {
                        studentPurchaseEventDto.setEventId(objects[1] != null ?
                                Double.valueOf(objects[1].toString()).longValue() : 0L);
                    } catch (NumberFormatException e) {
                        studentPurchaseEventDto.setEventId(0L);
                    }
                    try {
                        studentPurchaseEventDto.setPurchaseAmount(objects[3] != null ?
                                Double.valueOf(objects[3].toString()).longValue() : 0L);
                    } catch (NumberFormatException e) {
                        studentPurchaseEventDto.setPurchaseAmount(0L);
                    }
                    studentPurchaseEventDto.setEventName(objects[2] != null ? objects[2].toString() : "");
                    eventDtoList.add(studentPurchaseEventDto);
                }
            }
            studentChargesDto.setEventList(eventDtoList);
        }

        /* HDC amount */
        List<StudentChargesDto> hdcList = new ArrayList<>();
        List<Object[]> hdcObj = deanHdcComplaintRepository.getPendingAmountsByStudentId(studentId);
        if (CollectionUtils.isNotEmpty(hdcObj)) {
            for (Object[] objects : hdcObj) {
                if (objects != null && objects.length > 0) {
                    StudentChargesDto chargesDto = new StudentChargesDto();
                    try {
                        chargesDto.setHdcId(objects[0] != null ?
                                Double.valueOf(objects[0].toString()).longValue() : 0L);
                    } catch (NumberFormatException e) {
                        chargesDto.setHdcId(0L);
                    }

                    try {
                        chargesDto.setHdcAmount(objects[2] != null ?
                                Double.valueOf(objects[2].toString()).longValue() : 0L);
                    } catch (NumberFormatException e) {
                        chargesDto.setHdcAmount(0L);
                    }
                    hdcList.add(chargesDto);
                }
            }
            studentChargesDto.setHdcList(hdcList);
        }

        return studentChargesDto;
    }

    private String organizeBySemester(LedgerReportDto dto) {
        var date = dto.getVoucherDate();
        return (date.getMonthValue() <= 6 ? commonResponseUtil.getMessage("message.label.jan.to.jun") : commonResponseUtil.getMessage("message.label.jul.to.dec")) +" "+date.getYear();
    }

    private LedgerReportDto setClosingBalance(LedgerReportDto dto, AtomicReference<Double> runningBalance) {
        runningBalance.updateAndGet(balance -> {
            double updatedBalance = switch (dto.getDebitOrCredit().toLowerCase()) {
                case Constants.DEBIT -> balance - dto.getAmount();
                case Constants.CREDIT -> balance + dto.getAmount();
                default -> balance;
            };

            if(dto.getAmount() < 0.0){
                dto.setAmount(0.0-dto.getAmount());
                updatedBalance = updatedBalance * -1;
            }
            dto.setClosingBalance(updatedBalance);

            // Format the closing balance
            String formattedClosingBalance = formatTransactionAmount(updatedBalance);

            dto.setFormattedClosingBalance(formattedClosingBalance);

            return updatedBalance;
        });
        return dto;
    }


    private double calculateTotal(List<LedgerReportDto> dtos, String type) {
        return dtos.stream()
                .filter(dto -> type.equalsIgnoreCase(dto.getDebitOrCredit()))
                .mapToDouble(LedgerReportDto::getAmount)
                .sum();
    }

    private String calculateOpeningClosingBalance(List<LedgerReportDto> dtos) {
        if (dtos.isEmpty()) return "";
        var firstDto = dtos.getFirst();
        var lastDto = dtos.getLast();

        var intervalOpeningBalance = firstDto.getClosingBalance()
                - firstDto.getAmount()
                * (Constants.CREDIT.equalsIgnoreCase(firstDto.getDebitOrCredit()) ? 1 : -1);


        var intervalClosingBalance = lastDto.getClosingBalance();

        String formattedOpeningBalance = formatTransactionAmount(intervalOpeningBalance);
        String formattedClosingBalance = formatTransactionAmount(intervalClosingBalance);
        return String.format(
                "%s %s %s %s",
                commonResponseUtil.getMessage("message.label.opening.balance"),
                formattedOpeningBalance,
                commonResponseUtil.getMessage("message.label.closing.balance"),
                formattedClosingBalance
            );

    }

    private Map<String, Object> calculateTransactions(List<LedgerReportDto> dtos) {
        if (dtos.isEmpty()) {
            return Map.of(
                    Constants.DATA, List.of(),
                    Constants.TOTAL_CREDIT, 0.0,
                    Constants.TOTAL_DEBIT, 0.0,
                    Constants.TOTAL_CLOSING_BAL, 0.0,
                    Constants.TOTAL_OPENING_CLOSING_BAL, Strings.EMPTY
            );
        }

        double totalCredit = calculateTotal(dtos, Constants.CREDIT);
        double totalDebit = calculateTotal(dtos, Constants.DEBIT);
        double totalClosingBalance = dtos.getLast().getClosingBalance();
        String totalOpeningClosingBalance = calculateOpeningClosingBalance(dtos);
        return Map.of(
                Constants.DATA, List.copyOf(dtos),
                Constants.TOTAL_CREDIT, totalCredit,
                Constants.TOTAL_DEBIT, totalDebit,
                Constants.TOTAL_CLOSING_BAL, formatTransactionAmount(totalClosingBalance),
                Constants.TOTAL_OPENING_CLOSING_BAL, totalOpeningClosingBalance
        );
    }


    private LedgerReportDto mapToLedgerReportDto(Object[] o) {
        System.out.println(o[7].getClass());
        return LedgerReportDto.builder()
                .voucherDate(utility.convertToLocalDate(o[1]))
                .description(Objects.nonNull(o[2])?o[2].toString():"")
                .refNo(Objects.nonNull(o[3])?o[3].toString():"")
                .amount(utility.parseDouble(o[4]))
                .debitOrCredit(Objects.nonNull(o[5])?o[5].toString():"")
                .voucherNo(Objects.nonNull(o[6])?o[6].toString():"")
                .createdAt( o[7] == null ? null : DateUtility.toLocalDateTime(o[7]))
                .slNo(utility.parseInt(o[8]))
                .build();
    }

    private LedgerReportDto mapToFoodCourtReportDto(Object[] o) {
        return LedgerReportDto.builder()
                .voucherNo(Objects.nonNull(o[0])?o[0].toString():"")
                .voucherDate(utility.convertToLocalDate(o[1]))
                .studentId(Objects.nonNull(o[2])?o[2].toString():"")
                .messName(Objects.nonNull(o[3])?o[3].toString():"")
                .messDiningFromDate(utility.convertToLocalDate(o[4]))
                .messDiningToDate(utility.convertToLocalDate(o[5]))
                .amount(utility.parseDouble(o[6]))
                .debitOrCredit(o[7].toString())
                .totalPurchaseAmount(utility.parseDouble(o[8]))
                .totalCreditAmount(utility.parseDouble(o[9]))
                .balanceAmount(utility.parseDouble(o[10]))
                .description(commonResponseUtil.getMessage("message.label.purchased.at")+ o[3])
                .build();
    }

    private String formatTransactionAmount(Double amount) {
        String formattedAmount;
        if(amount < 0){
           formattedAmount = String.format(commonResponseUtil.getMessage("message.label.negative.balance.format"), utility.formatCommaSeperatedCurrency(Math.abs(amount)));
        }
        else{
            formattedAmount = String.format(commonResponseUtil.getMessage("message.label.positive.balance.format"), utility.formatCommaSeperatedCurrency(amount));
        }
        return formattedAmount;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean claimPenaltyCharge(String studentID, String claimAmount, String transactionType,String eventId) throws Exception {
        try {
            Optional<StudentDetailsInfoEntity> byStudentId = studentDetailsInfoRepository.findByStudentId(studentID);
            if (byStudentId.isPresent()) {
                if (StringUtils.equalsIgnoreCase(byStudentId.get().getSettlementFlag().trim(), ModelConstants.STATUS_ACTIVE)) {
                    throw new Exception(commonResponseUtil.getMessage("response.noc.vacation.settlement.already.completed"));
                }
            }
            /* For Card transfer */
            if (StringUtils.equalsIgnoreCase(transactionType, Constants.CARD_AMOUNT_TRANSFER)) {
                return cardAmountTransfer(studentID, claimAmount);
            }

            String facilityMasterId = "";
            String hostelName = "";
            List<Object[]> objList = null;
            String transferType = "";
            String donatorType = "";
            String donationOthersDesc = "";
            String eventName = "";
            String eventAccHead = "";

            String faIntegrationFlag = smsConfigDataService.getSimConfigValue(SimsConfigDataService.IKOLLEGE_FA_INTEGRATION);
            boolean needFA = Boolean.parseBoolean(faIntegrationFlag);
            FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);

            if (StringUtils.equalsIgnoreCase(transactionType, Constants.PENALTY_CLAIM)) {
                objList = studentDetailsInfoRepository.getPenaltyFacilityMasterId(studentID);
                transferType = Constants.CLAIM_PENALTY_CHARGES;
            } else if (StringUtils.equalsIgnoreCase(transactionType, Constants.DONATE_CLAIM)) {
                objList = studentDetailsInfoRepository.getDonationHostelId(studentID);
                transferType = Constants.CLAIM_DONATION;
            } else if (StringUtils.equalsIgnoreCase(transactionType, Constants.HOSTEL_DEPOSIT_REFUND)) {
                facilityMasterId = "0";
                transferType = Constants.REFUND_HOSTEL_DEPOSIT;
            } else if (StringUtils.equalsIgnoreCase(transactionType, Constants.CARD_AMOUNT_TRANSFER)) {
                facilityMasterId = "0";
                transferType = Constants.CARD_AMOUNT_TRANSFER;
            } else if (StringUtils.equalsIgnoreCase(transactionType, Constants.PURCHASE_AMOUNT_DEDUCT)) {
                Optional<ShowEventMasterEntity> show = showEventMasterRepository.findById(Long.valueOf(eventId));
                if (show.isPresent()) {
                    eventName = show.get().getEventName();
                    eventAccHead = show.get().getAcchead();
                }
                facilityMasterId = "0";
                transferType = eventAccHead;
            }

            if (CollectionUtils.isNotEmpty(objList)) {
                Object[] firstElement = objList.getFirst();
                if (firstElement != null) {
                    facilityMasterId = getSafely(firstElement, 1);
                    hostelName = getSafely(firstElement, 2);
                    donatorType = getSafely(firstElement, 3);
                    donationOthersDesc = getSafely(firstElement, 4);
                }
            }

            Integer jvseq = messLedgerARepository.getNextValMessLedger();
            Integer nextVoucherNoMessLedger = messLedgerARepository.getNextVoucherNoMessLedger();
            String voucherNo = "JV" + String.format("%08d", jvseq);

            MessLedgerAEntity messLedgerAEntity = getMessLedgerAEntity(studentID, Double.parseDouble(claimAmount),
                    finYearDetails.getFinYear(), voucherNo, facilityMasterId, transactionType, eventName);

            String description = messLedgerAEntity.getDescription() + " - " + messLedgerAEntity.getAcchead();
            if (StringUtils.equalsIgnoreCase(transactionType, Constants.DONATE_CLAIM)) {
                description = description + " - " + donatorType + donationOthersDesc;
                if (StringUtils.isNotEmpty(donatorType)) {
                    transferType = "claim_" + donatorType + "_donation";
                }
            }

            if (needFA) {
                messLedgerAEntity.setDescription(description);
            }

            messLedgerAEntity = messLedgerARepository.save(messLedgerAEntity);
            MessLedgerBEntity messLedgerBEntity = null;
            if (messLedgerAEntity.getId() != null) {
                messLedgerBEntity = getMessLedgerBEntity(messLedgerAEntity, finYearDetails.getFinYear(), voucherNo,
                        facilityMasterId, transactionType, eventAccHead);
                messLedgerBEntity.getId().setSlno(1);
                messLedgerBEntity = messLedgerBRepository.save(messLedgerBEntity);
            }

            String status = null;
            if (needFA) {
                if (messLedgerAEntity != null && messLedgerBEntity != null) {
                    status = Constants.SAVED;
                    StudentDebitForm studentDebitForm = new StudentDebitForm();
                    studentDebitForm.setReferenceNumber(voucherNo);
                    studentDebitForm.setAmount(Double.parseDouble(claimAmount));
                    studentDebitForm.setStudentCount(1);
                    studentDebitForm.setTransferStatus(WorkflowStatus.INITIATED.getStatus());
                    studentDebitForm.setIKollegeTransferType(transferType);
                    studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
                    studentDebitForm.setHostelName(hostelName);
                    studentDebitForm.setDescription(description);
                    studentDebitForm.setStudentIds(List.of(studentID));
                    studentDebitForm.setNumOfTransaction(1);
                    ;

                    if (needFA) {
                        status = transferAmountUtils.saveTransactioninFA(studentDebitForm);
                    }
                }
            }

            if (StringUtils.equalsIgnoreCase(transactionType, Constants.PENALTY_CLAIM) ||
                    StringUtils.equalsIgnoreCase(transactionType, Constants.DONATE_CLAIM)) {
                Optional<StudentHostelRoomVacatingRequestViewEntity> studentCharge =
                        studentHostelRoomVacatingRequestViewRepository.getStudentCharges(studentID, ModelConstants.STATUS_ACTIVE);
                StudentHostelRoomVacatingRequestViewEntity studentHostelRoomVacatingRequestViewEntity = studentCharge.get();
                if (StringUtils.equalsIgnoreCase(transactionType, Constants.PENALTY_CLAIM)) {
                    studentHostelRoomVacatingRequestViewEntity.setPenaltyAmountCollected(Long.valueOf(claimAmount));
                } else if (StringUtils.equalsIgnoreCase(transactionType, Constants.DONATE_CLAIM)) {
                    studentHostelRoomVacatingRequestViewEntity.setDonationAmountCollected(Long.valueOf(claimAmount));
                }
                studentHostelRoomVacatingRequestViewRepository.save(studentHostelRoomVacatingRequestViewEntity);
            }

            //Purchase claim save
            if (StringUtils.equalsIgnoreCase(transactionType, Constants.PURCHASE_AMOUNT_DEDUCT)) {
                ShowEventPurchaseClaim claim = new ShowEventPurchaseClaim();
                ShowEventMasterEntity event = new ShowEventMasterEntity();
                event.setId(Long.valueOf(eventId));
                claim.setStudentId(studentID);
                claim.setAmount(Double.parseDouble(claimAmount));
                claim.setShowEventMaster(event);
                showEventPurchaseClaimRepository.save(claim);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    private boolean cardAmountTransfer(String studentID, String claimAmount) throws Exception {
        try {
            String faIntegrationFlag = smsConfigDataService.getSimConfigValue(SimsConfigDataService.IKOLLEGE_FA_INTEGRATION);
            boolean needFA = Boolean.parseBoolean(faIntegrationFlag);
            FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);

            int seq = 0;
            int slno = 1;
            long hostelid = 0;
            Integer jvseq = 0;

            jvseq = messLedgerARepository.getNextValMessLedger();
            Integer nextVoucherNoMessLedger = messLedgerARepository.getNextVoucherNoMessLedger();
            String voucherNo = "JV" + String.format("%08d", jvseq);

            // debit to credit
            MessLedgerAEntity messLedgerAEntity = getMessLedgerAEntity(studentID, Double.parseDouble(claimAmount),
                    finYearDetails.getFinYear(), voucherNo, "0", null, null);

            messLedgerAEntity.getId().setBookType(Constants.CREDIT_CARD);
            messLedgerAEntity.setSubAccountHead("STUD");
            messLedgerAEntity.setDebitOrCredit(Constants.DEBIT);
            messLedgerAEntity.setDescription(Constants.CC_TO_MS_EXCHANGE);
            messLedgerAEntity = messLedgerARepository.save(messLedgerAEntity);

            MessLedgerBEntity messLedgerBEntity = null;
            if (messLedgerAEntity.getId() != null) {
                messLedgerBEntity = getMessLedgerBEntity(messLedgerAEntity, finYearDetails.getFinYear(), voucherNo, "0", null, null);

                messLedgerBEntity.getId().setBookType(Constants.CREDIT_CARD);
                messLedgerBEntity.setDebitOrCredit(Constants.CREDIT);
                messLedgerBEntity.setAcchead(Constants.CC_TO_MS_EXCHANGE);
                messLedgerBEntity.setSubAccountHead("0");
                messLedgerBEntity.setDescription(Constants.CC_TO_MS_EXCHANGE);
                messLedgerBEntity.getId().setSlno(slno++);
                messLedgerBEntity = messLedgerBRepository.save(messLedgerBEntity);
            }


            // credit to debit
            MessLedgerAEntity messLedgerAEntity1 = getMessLedgerAEntity(studentID, Double.parseDouble(claimAmount),
                    finYearDetails.getFinYear(), voucherNo, "0", null, null);

            messLedgerAEntity1.getId().setBookType(Constants.MESS_MS);
            messLedgerAEntity1.setDebitOrCredit(Constants.DEBIT);
            messLedgerAEntity1.setSubAccountHead("0");
            messLedgerAEntity1.setAcchead(Constants.CC_TO_MS_EXCHANGE);
            messLedgerAEntity1.setDescription(Constants.CC_TO_MS_EXCHANGE);
            messLedgerAEntity1 = messLedgerARepository.save(messLedgerAEntity1);

            MessLedgerBEntity messLedgerBEntity1 = null;
            if (messLedgerAEntity1.getId() != null) {
                messLedgerBEntity1 = getMessLedgerBEntity(messLedgerAEntity1, finYearDetails.getFinYear(), voucherNo, "0", null, null);

                messLedgerBEntity1.getId().setBookType(Constants.MESS_MS);
                messLedgerBEntity1.setAcchead(studentID.toUpperCase());
                messLedgerBEntity1.setSubAccountHead("STUD");
                messLedgerBEntity1.setDescription(Constants.CC_TO_MS_EXCHANGE);
                messLedgerBEntity1.setDebitOrCredit(Constants.CREDIT);
                messLedgerBEntity1.getId().setSlno(slno++);
                messLedgerBEntity1.setDocRefNo(studentID.toUpperCase());
                messLedgerBEntity1 = messLedgerBRepository.save(messLedgerBEntity1);
            }

            Optional<StudentNetBalanceEntity> studentNetBalanceEntityOptional = studentNetBalanceEntityRepository.findByStudentId(studentID);
            if (studentNetBalanceEntityOptional.isPresent()) {
                StudentNetBalanceEntity studentNetBalanceEntity = studentNetBalanceEntityOptional.get();
                studentNetBalanceEntity.setNetBalanceCard(0.0);
                studentNetBalanceEntityRepository.save(studentNetBalanceEntity);
            }

            String status = null;
            if (needFA) {
                if (messLedgerAEntity != null && messLedgerBEntity != null) {
                    status = Constants.SAVED;
                    StudentDebitForm studentDebitForm = new StudentDebitForm();
                    studentDebitForm.setReferenceNumber(voucherNo);
                    studentDebitForm.setAmount(Double.parseDouble(claimAmount));
                    studentDebitForm.setStudentCount(1);
                    studentDebitForm.setTransferStatus(WorkflowStatus.INITIATED.getStatus());
                    studentDebitForm.setIKollegeTransferType(Constants.IKOLLEGE_CC_TO_MS_EXCHANGE);
                    studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
                    studentDebitForm.setHostelName("");
                    studentDebitForm.setDescription(commonResponseUtil.getMessage("response.noc.vacation.card.balance.transfer") + " - " + studentID);
                    studentDebitForm.setStudentIds(List.of(studentID));
                    studentDebitForm.setNumOfTransaction(1);

                    if (needFA) {
                        status = transferAmountUtils.saveTransactioninFA(studentDebitForm);
                    }
                }
            }

            if (needFA) {
                UserFpCardEntity entity = userFpCardRepository.findByUserId(studentID.toUpperCase());
                entity.setCardActiveStatus(ModelConstants.STATUS_INACTIVE);
                userFpCardRepository.save(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }


    public MessLedgerAEntity getMessLedgerAEntity(String studentId, Double amount,
                                                  String activeFinancialYear, String voucherNo,
                                                  String facilityMasterId, String transactionType,String eventName) {

        String description="";
        String screenType = "";
        String bookType = "";
        String debitOrCredit = "";
        if(StringUtils.equalsIgnoreCase(transactionType,Constants.PENALTY_CLAIM)){
            description = commonResponseUtil.getMessage("message.noc.towards.penalty.amount.deduction");
            screenType =  Constants.SCREEN_TYPE_CLAIM_PENALTY_CHARGES;
            bookType = Constants.MESS_MS;
            debitOrCredit = Constants.DEBIT;
        } else if(StringUtils.equalsIgnoreCase(transactionType,Constants.DONATE_CLAIM)){
            description = commonResponseUtil.getMessage("message.noc.towards.donation.amount.deduction");
            screenType =  Constants.SCREEN_TYPE_CLAIM_DONATION_CHARGES;
            bookType = Constants.MESS_MS;
            debitOrCredit = Constants.DEBIT;
        } else if(StringUtils.equalsIgnoreCase(transactionType,Constants.HOSTEL_DEPOSIT_REFUND)){
            description = commonResponseUtil.getMessage("message.noc.hostel.deposit");
            screenType = Constants.SCREEN_TYPE_REFUND_HOSTEL_DEPOSIT;
            bookType = Constants.MESS_MS;
            debitOrCredit = Constants.CREDIT;
        } else if(StringUtils.equalsIgnoreCase(transactionType,Constants.CARD_AMOUNT_TRANSFER)){
            screenType = Constants.SCREEN_TYPE_TRANSFER_AMOUNT;
        }else if(StringUtils.equalsIgnoreCase(transactionType,Constants.PURCHASE_AMOUNT_DEDUCT)){
            String desc_format = String.format(commonResponseUtil.getMessage("message.noc.towards.purchase.amount.deduction"),
                    eventName == null ? "" : eventName);
            description = desc_format;
            screenType = Constants.SCREEN_TYPE_PURCHASE_AMOUNT;
            bookType = Constants.MESS_MS;
            debitOrCredit = Constants.DEBIT;
        }

        // Create TransactionDto with common values
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBookType(bookType);
        transactionDto.setFinYear(activeFinancialYear);
        transactionDto.setVoucherNo(voucherNo);
        transactionDto.setDate(LocalDate.now());
        transactionDto.setAccHead(studentId.toUpperCase());
        transactionDto.setSubAccHead("STUD");
        transactionDto.setDescription(description);
        transactionDto.setFcno(facilityMasterId);
        transactionDto.setAmount(amount);
        transactionDto.setRecon(ModelConstants.STATUS_INACTIVE);
        transactionDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        transactionDto.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        transactionDto.setDebitOrCredit(debitOrCredit);
        transactionDto.setScreenType(screenType);
        transactionDto.setItcons("0");
        transactionDto.setItacc("0");
        transactionDto.setAdv("");
        transactionDto.setAdj("");
        transactionDto.setChequeNo("");
        transactionDto.setDescription1("");

        // Use utility method to create the entity
        return TransferAmountUtils.createMessLedgerAEntity(transactionDto, activeFinancialYear);
    }

    public MessLedgerBEntity getMessLedgerBEntity(MessLedgerAEntity messLedgerAEntity,
                                                  String activeFinancialYear, String voucherNo, String facilityMasterId,
                                                  String transactionType,String eventAccHead) {


        String accHead = "";
        String debitOrCredit = "";
        if(StringUtils.equalsIgnoreCase(transactionType,Constants.PENALTY_CLAIM)){
            accHead = Constants.ACC_HEAD_PENCHAR;
            debitOrCredit = Constants.CREDIT;
        } else if(StringUtils.equalsIgnoreCase(transactionType,Constants.DONATE_CLAIM)){
            accHead = Constants.ACC_HEAD_DONAMNT;
            debitOrCredit = Constants.CREDIT;
        } else if(StringUtils.equalsIgnoreCase(transactionType,Constants.HOSTEL_DEPOSIT_REFUND)){
            accHead = Constants.ACC_HEAD_HOSDEP;
            debitOrCredit = Constants.DEBIT;
        } else if(StringUtils.equalsIgnoreCase(transactionType,Constants.PURCHASE_AMOUNT_DEDUCT)){
            accHead = eventAccHead;
            debitOrCredit = Constants.CREDIT;
        }

        // Create TransactionDto with common values
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBookType(Constants.MESS_MS);
        transactionDto.setFinYear(activeFinancialYear);
        transactionDto.setVoucherNo(voucherNo);
        transactionDto.setDate(LocalDate.now());
        transactionDto.setAccHead(accHead);
        transactionDto.setSubAccHead("0");
        transactionDto.setDescription(messLedgerAEntity.getDescription());
        transactionDto.setDescription1("");
        transactionDto.setChequeNo("");
        transactionDto.setDocRefNo(messLedgerAEntity.getAcchead().toUpperCase());
        transactionDto.setAmount(messLedgerAEntity.getAmount());
        transactionDto.setRecon(ModelConstants.STATUS_INACTIVE);
        transactionDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        transactionDto.setDebitOrCredit(debitOrCredit);
        transactionDto.setFcno(facilityMasterId);
        transactionDto.setTdsValue(0D);
        transactionDto.setAdvAmount(0D);
        transactionDto.setBillAmount(0D);
        transactionDto.setActiveFlag(ModelConstants.STATUS_ACTIVE);

        // Generate random slno (if still needed)
        //int slno = 334 + (int) (Math.random() * 10);
         int slno=0;

        // Use utility method to create the entity
        return TransferAmountUtils.createMessLedgerBEntity(transactionDto, activeFinancialYear, 0, slno);
    }

    private String getSafely(Object[] array, int index) {
        if (array == null || index < 0 || index >= array.length) {
            return null;
        }
        return array[index] != null ? array[index].toString() : null;
    }

    private final ArchiveTableMasterRepository archiveTableMasterRepository;
    private final AccountHeadService accountHeadService;

    public SettlementFormDTO getSettlementId(String studentID, String amount) {
        SettlementFormDTO settlementFormDTO = new SettlementFormDTO();
        Integer settlementCount = archiveTableMasterRepository.getSettlementCount();
        settlementFormDTO.setSettlementCount(settlementCount);
        int i = settlementCount != null ? settlementCount + 1 : 1;
        settlementFormDTO.setDdNo(i);

        List<StudentHostelRoomVacatingRequestViewEntity> byStudentIdList = studentHostelRoomVacatingRequestViewRepository.findByStudentId(studentID);
        if(CollectionUtils.isNotEmpty(byStudentIdList)){
            String vacationDateString = DateUtility.formatDate(byStudentIdList.getFirst().getVacatingDate());
            settlementFormDTO.setVacatingDate(byStudentIdList.getFirst().getVacatingDate());
            settlementFormDTO.setClearanceReason(byStudentIdList.getFirst().getVacatingReason());
        }

        List<AccountHeadDto> bankList = accountHeadService.getBankList(List.of("B", "C"));
        settlementFormDTO.setBankList(bankList);
        settlementFormDTO.setStudentId(studentID);
        settlementFormDTO.setAmount(Double.parseDouble(amount));
        settlementFormDTO.setMessCard("MS");
        settlementFormDTO.setDdDate(DateUtility.currentDateOnly());
        settlementFormDTO.setSystemDate(DateUtility.currentDateOnly());
        settlementFormDTO.setAccountHead(studentID.toUpperCase());
        settlementFormDTO.setHostelId(0);
        settlementFormDTO.setHostelName("");
        settlementFormDTO.setDescription(commonResponseUtil.getMessage("message.noc.refund.with.deposit"));
        settlementFormDTO.setDuesIfAny(false);

        List<String> clearanceReasonList = Arrays.stream(ClearanceReasonEnum.values())
                .map(ClearanceReasonEnum::getValue)
                .toList();
        settlementFormDTO.setClearanceReasonList(clearanceReasonList);
        return settlementFormDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean saveSettlement(@Valid SettlementFormDTO settlementFormDTO) throws Exception {
        try {
            //int seq = 0;
            String facility = settlementFormDTO.getHostelName();
            String description = "";
            if (settlementFormDTO.getBankId().equals("SBI")) {
                description = settlementFormDTO.getDescription() + " by cheque no:"
                        + settlementFormDTO.getDdNo() + " /cheque date:"
                        + DateUtility.formatDate(settlementFormDTO.getDdDate());
            } else {
                description = settlementFormDTO.getDescription();
            }

            String faIntegrationFlag = smsConfigDataService.getSimConfigValue(SimsConfigDataService.IKOLLEGE_FA_INTEGRATION);
            boolean needFA = Boolean.parseBoolean(faIntegrationFlag);
            FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);

            String voucherNo = "";

            String voucherNoByStudentId = messLedgerARepository.getVoucherNoByStudentId(finYearDetails.getFinYear(), settlementFormDTO.getAccountHead());
            voucherNo = voucherNoByStudentId != null ? voucherNoByStudentId : voucherNo;

//        String voucherNoMax = messLedgerARepository.getVoucherNoMax();
//        seq = voucherNoMax != null ? Integer.parseInt(voucherNoMax) : seq;
            AtomicLong seq = new AtomicLong(messLedgerARepository.getNextValMessLedger());
            // Create TransactionDto with common values
            MessLedgerAEntity messLedgerAEntity = new MessLedgerAEntity();
            MessLedgerAEntityId messLedgerAEntityId = new MessLedgerAEntityId();
            messLedgerAEntityId.setBookType(settlementFormDTO.getMessCard());
            messLedgerAEntityId.setFinYear(finYearDetails.getFinYear());
            messLedgerAEntityId.setVoucherNo(String.valueOf(seq));
            messLedgerAEntity.setId(messLedgerAEntityId);
            messLedgerAEntity.setVoucherDate(settlementFormDTO.getSystemDate());
            messLedgerAEntity.setAcchead(settlementFormDTO.getAccountHead());
            messLedgerAEntity.setSubAccountHead(facility);
            messLedgerAEntity.setDescription(description);
            messLedgerAEntity.setDescription1("");
            messLedgerAEntity.setChequeNo(settlementFormDTO.getDdNo() != null ? String.valueOf(settlementFormDTO.getDdNo()) : "");
            messLedgerAEntity.setChequeDate(settlementFormDTO.getDdDate());
            messLedgerAEntity.setAmount(settlementFormDTO.getAmount());
            messLedgerAEntity.setRp(null);
            messLedgerAEntity.setAdv(settlementFormDTO.getClearanceReason());
            messLedgerAEntity.setAdj(StringUtils.isNotBlank(settlementFormDTO.getClearanceReason()) ? ModelConstants.STATUS_ACTIVE : "");
            messLedgerAEntity.setRecon(StringUtils.isNotBlank(settlementFormDTO.getClearanceReason()) ? "" : ModelConstants.STATUS_ACTIVE);
            messLedgerAEntity.setCancelStatus(ModelConstants.STATUS_INACTIVE);
            messLedgerAEntity.setUsr("");
            messLedgerAEntity.setFcNo(String.valueOf(settlementFormDTO.getHostelId()));
            messLedgerAEntity.setDocRefNo("");
            messLedgerAEntity.setA2no("");
            messLedgerAEntity.setParty("");
            messLedgerAEntity.setDebitOrCredit(Constants.DEBIT);
            messLedgerAEntity.setItAmount(0.0);
            messLedgerAEntity.setItPer(0.0);
            messLedgerAEntity.setItCons("");
            messLedgerAEntity.setItAcc("");
            messLedgerAEntity.setLink(0);
            messLedgerAEntity.setMatchDate(settlementFormDTO.getVacatingDate());
            messLedgerAEntity.setSurcharge(0.0);
            messLedgerAEntity.setUnmatchAmnt(0.0);
            messLedgerAEntity.setMrNo("");
            messLedgerAEntity.setMrDate(null);
            messLedgerAEntity.onCreate();

            MessLedgerAEntity messLedgerAEntitySaved = messLedgerARepository.save(messLedgerAEntity);

            MessLedgerBEntity messLedgerBEntity = new MessLedgerBEntity();
            MessLedgerBEntityId messLedgerBEntityId = new MessLedgerBEntityId();
            messLedgerBEntityId.setBookType(settlementFormDTO.getMessCard());
            messLedgerBEntityId.setFinYear(finYearDetails.getFinYear());
            messLedgerBEntityId.setSlno(1);
            messLedgerBEntityId.setVoucherNo(String.valueOf(seq));
            messLedgerBEntity.setId(messLedgerBEntityId);
            messLedgerBEntity.setVoucherDate(settlementFormDTO.getSystemDate());
            messLedgerBEntity.setAcchead(settlementFormDTO.getBankId());
            messLedgerBEntity.setSubAccountHead(facility);
            messLedgerBEntity.setDescription(description);
            messLedgerBEntity.setDescription1("");
            messLedgerBEntity.setChequeNo(settlementFormDTO.getDdNo() != null ? String.valueOf(settlementFormDTO.getDdNo()) : "");
            messLedgerBEntity.setChequeDate(settlementFormDTO.getDdDate());
            messLedgerBEntity.setAmount(settlementFormDTO.getAmount());
            messLedgerBEntity.setRp(null);
            messLedgerBEntity.setAdv(settlementFormDTO.getClearanceReason());
            messLedgerBEntity.setAdj(StringUtils.isNotBlank(settlementFormDTO.getClearanceReason()) ? ModelConstants.STATUS_ACTIVE : "");
            messLedgerBEntity.setRecon(StringUtils.isNotBlank(settlementFormDTO.getClearanceReason()) ? "" : ModelConstants.STATUS_ACTIVE);
            messLedgerBEntity.setCancelStatus(ModelConstants.STATUS_INACTIVE);

            messLedgerBEntity.setFcNo(String.valueOf(settlementFormDTO.getHostelId()));
            messLedgerBEntity.setDocRefNo("");
            messLedgerBEntity.setA2no("");
            messLedgerBEntity.setDebitOrCredit(Constants.CREDIT);
            messLedgerBEntity.setRegNo("");
            messLedgerBEntity.setChalNo("");
            messLedgerBEntity.setTdsValue(0.0);
            messLedgerBEntity.setBillAmt(null);
            messLedgerBEntity.setMatchDate(settlementFormDTO.getVacatingDate());
            messLedgerBEntity.setDocDt(null);
            messLedgerBEntity.setLink(0);
            messLedgerBEntity.setSurcharge(0.0);
            messLedgerBEntity.setUnmatchAmnt(0.0);
            messLedgerBEntity.onCreate();
            MessLedgerBEntity messLedgerBEntitySaved = messLedgerBRepository.save(messLedgerBEntity);

            if (messLedgerAEntity == null || messLedgerBEntitySaved == null) {
                throw new RuntimeException(commonResponseUtil.getMessage("message.noc.ledger.did.not.saved"));
            }

            Optional<StudentDetailsInfoEntity> byStudentId = studentDetailsInfoRepository.findByStudentId(settlementFormDTO.getStudentId());
            if (byStudentId.isPresent()) {
                StudentDetailsInfoEntity studentDetailsInfoEntity = byStudentId.get();
                studentDetailsInfoEntity.setSettlementFlag(ModelConstants.STATUS_ACTIVE);
                studentDetailsInfoRepository.save(studentDetailsInfoEntity);
            }

            SettlementHistoryEntity settlementHistoryEntity = new SettlementHistoryEntity();
            settlementHistoryEntity.setOldSettlementId(String.valueOf(settlementFormDTO.getDdNo()));
            settlementHistoryEntity.setStudentId(settlementFormDTO.getStudentId());
            settlementHistoryEntity.setAmount(settlementFormDTO.getAmount());
            settlementHistoryEntity.setDebitOrCredit(Constants.DEBIT);
            settlementHistoryEntity.setSettlementDate(settlementFormDTO.getDdDate());
            settlementHistoryEntity.setDescription(description);
            settlementHistoryEntityRepository.save(settlementHistoryEntity);


            auditTrailService.saveAuditTrail("Settlement Form", commonResponseUtil.getMessage("url.hostel.noc.settlement.save"),
                    this.getClass().getName() + Constants.HYPHEN + "saveSettlement()");

            String status = null;
            if (needFA) {
                if (messLedgerAEntity != null && messLedgerBEntity != null) {
                    status = Constants.SAVED;
                    StudentDebitForm studentDebitForm = new StudentDebitForm();
                    studentDebitForm.setReferenceNumber(String.valueOf(seq));
                    studentDebitForm.setAmount(settlementFormDTO.getAmount());
                    studentDebitForm.setStudentCount(1);
                    studentDebitForm.setTransferStatus(WorkflowStatus.INITIATED.getStatus());
                    studentDebitForm.setIKollegeTransferType(Constants.IKOLLEGE_SETTLEMENT_REFUND);
                    studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
                    studentDebitForm.setHostelName("");
                    studentDebitForm.setDescription(description);
                    studentDebitForm.setStudentIds(List.of(settlementFormDTO.getStudentId()));
                    studentDebitForm.setNumOfTransaction(1);
                    ;

                    if (needFA) {
                        status = transferAmountUtils.saveTransactioninFA(studentDebitForm);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    public Workbook downloadMessLedgerReportExcel(List<String> split) {
        var selectForm = new MessLedgerReportSelectForm();
        var view = split.getFirst();
        var year = split.get(1);

        selectForm.setStudentID(split.get(2));
        Optional.ofNullable(split.get(3))
                .filter(s -> !s.isBlank())
                .map(LocalDate::parse)
                .ifPresent(selectForm::setFromDate);
        Optional.ofNullable(split.get(4))
                .filter(s -> !s.isBlank())
                .map(LocalDate::parse)
                .ifPresent(selectForm::setToDate);
        selectForm.setSelectedCriteria(split.getLast());

        var ledgerList = getLedgerReportList(selectForm, true);
        var studentDetails = getStudentChargesAndAmountDetails(selectForm);
        var targetYear = Integer.parseInt(year.replaceAll("\\D+", ModelConstants.EMPTY_STRING).substring(0, 4));
        var filteredSortedList = ledgerList.stream()
                .filter(dto -> dto.getVoucherDate() != null && dto.getVoucherDate().getYear() >= targetYear)
                .sorted(Comparator.comparing(LedgerReportDto::getVoucherDate))
                .toList();
        var totalAmount = getTotalLedgerAmount(view, ledgerList, filteredSortedList);
        var openingBal = messOpeningBalService.getOpeningBal();
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(ExcelConstants.MESS_LEDGER_REPORT);
        var headerStyle = excelUtility.setHeaderStyle(workbook);
        var dataStyle = excelUtility.setDataStyle(workbook);
        var row = sheet.createRow(0);
        var cellHeader = row.createCell(0);
        var wrappedHeaderStyle = workbook.createCellStyle();
        var columnWidths = ExcelConstants.MESS_LEDGER_REPORT_DATA_WIDTH;
        var rowIndex = new AtomicInteger(8);
        var headers = ExcelConstants.MESS_LEDGER_REPORT_DATA;
        var headerRow = sheet.createRow(7);

        wrappedHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        wrappedHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        createMessLedgerHeader(cellHeader, wrappedHeaderStyle, row, sheet, headerStyle);
        performRowOneCellCreation(sheet, studentDetails, wrappedHeaderStyle);
        performRowTwoCellCreation(sheet, studentDetails, wrappedHeaderStyle);
        performRowThreeCellCreation(sheet, selectForm, commonResponseUtil.getMessage("message.label.expand.view").equalsIgnoreCase(view) ? ledgerList : filteredSortedList, wrappedHeaderStyle);
        performRowFourCellCreation(sheet, openingBal, wrappedHeaderStyle);
        performRowFiveCellCreation(sheet, studentDetails, wrappedHeaderStyle);

        sheet.createRow(6);
        IntStream.range(0, headers.length).forEach(i -> {
            var cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        });

        if (commonResponseUtil.getMessage("message.label.expand.view").equalsIgnoreCase(view)) {
            performExcelDetailsMapping(ledgerList, rowIndex, sheet, dataStyle);
        } else {
            performFilteredExcelDetailsMapping(filteredSortedList, rowIndex, sheet, dataStyle);
        }
        performTotalAmountRowCellCreation(sheet,rowIndex, headers, headerStyle, totalAmount);
        IntStream.range(0, columnWidths.length).forEach(i -> sheet.setColumnWidth(i, columnWidths[i]));
        return workbook;
    }

    private Map<String, Double> getTotalLedgerAmount(String view, List<LedgerReportDto> ledgerList, List<LedgerReportDto> filteredSortedList) {
        var listToUse = commonResponseUtil.getMessage("message.label.expand.view").equalsIgnoreCase(view)
                ? ledgerList : filteredSortedList;
        return Map.of(
                Constants.TOTAL_CREDIT, listToUse.stream()
                                .filter(dto -> Constants.CREDIT.equalsIgnoreCase(dto.getDebitOrCredit()))
                                .mapToDouble(LedgerReportDto::getAmount)
                                .sum(),
                Constants.TOTAL_DEBIT, listToUse.stream()
                                .filter(dto -> !Constants.CREDIT.equalsIgnoreCase(dto.getDebitOrCredit()))
                                .mapToDouble(LedgerReportDto::getAmount)
                                .sum(),
                Constants.TOTAL_CLOSING_BAL, listToUse.stream().toList().getLast().getClosingBalance()
        );
    }

    private void performExcelDetailsMapping(List<LedgerReportDto> ledgerList, AtomicInteger rowIndex, XSSFSheet sheet, XSSFCellStyle dataStyle) {
        ledgerList.forEach(dto -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            int col = 0;
            int colWidth = -1;
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(dto.getVoucherNo()), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(dto.getVoucherDate()).map(DateUtility::formatDate).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(dto.getDescription()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(dto.getRefNo()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Constants.CREDIT.equalsIgnoreCase(dto.getDebitOrCredit()) ?
                            String.format(ModelConstants.TWO_DECIMAL_POINT, dto.getAmount()) : "0.00", dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Constants.DEBIT.equalsIgnoreCase(dto.getDebitOrCredit()) ?
                            String.format(ModelConstants.TWO_DECIMAL_POINT, dto.getAmount()) : "0.00", dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col, colWidth, Optional.ofNullable(dto.getFormattedClosingBalance()).orElse(String.valueOf(0.0)), dataStyle);
        });
    }

    private void performFilteredExcelDetailsMapping(List<LedgerReportDto> filteredSortedList, AtomicInteger rowIndex, XSSFSheet sheet, XSSFCellStyle dataStyle) {
        filteredSortedList.forEach(dto -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            int col = 0;
            int colWidth = -1;
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, String.valueOf(dto.getVoucherNo()), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(dto.getVoucherDate()).map(DateUtility::formatDate).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(dto.getDescription()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth, Optional.ofNullable(dto.getRefNo()).orElse(ModelConstants.NOT_APPLICABLE), dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Constants.CREDIT.equalsIgnoreCase(dto.getDebitOrCredit()) ?
                            String.format(ModelConstants.TWO_DECIMAL_POINT, dto.getAmount()) : "0.00", dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col++, colWidth,
                    Constants.DEBIT.equalsIgnoreCase(dto.getDebitOrCredit()) ?
                            String.format(ModelConstants.TWO_DECIMAL_POINT, dto.getAmount()) : "0.00", dataStyle);
            excelUtility.createAndSetColumn(sheet, row, col, colWidth, Optional.ofNullable(dto.getFormattedClosingBalance()).orElse(String.valueOf(0.0)), dataStyle);
        });
    }

    private void performRowOneCellCreation(XSSFSheet sheet, StudentChargesDto studentDetails, XSSFCellStyle wrappedHeaderStyle) {
        var row3 = sheet.createRow(1);
        var studentId = row3.createCell(0);
        studentId.setCellValue(commonResponseUtil.getMessage("message.label.student.id") + ModelConstants.SPACE +
                ModelConstants.COLAN + ModelConstants.SPACE + Optional.ofNullable(studentDetails.getStudentID()).orElse(ModelConstants.NOT_APPLICABLE));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
        var studentName = row3.createCell(3);
        studentName.setCellValue(commonResponseUtil.getMessage("message.label.student.name") + ModelConstants.SPACE +
                ModelConstants.COLAN + ModelConstants.SPACE + Optional.ofNullable(studentDetails.getStudentName()).orElse(ModelConstants.NOT_APPLICABLE));
        studentId.setCellStyle(wrappedHeaderStyle);
        studentName.setCellStyle(wrappedHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 6));
        IntStream.rangeClosed(4, 6).forEach(row3::createCell);
    }

    private void performRowTwoCellCreation(XSSFSheet sheet, StudentChargesDto studentDetails, XSSFCellStyle wrappedHeaderStyle) {
        var row4 = sheet.createRow(2);
        var roomNo = row4.createCell(0);
        var hostelName = row4.createCell(2);
        var hostelDeposit = row4.createCell(5);
        roomNo.setCellValue(commonResponseUtil.getMessage("message.label.room.no") + ModelConstants.SPACE +
                ModelConstants.COLAN + ModelConstants.SPACE + Optional.ofNullable(studentDetails.getRoomNo()).orElse(ModelConstants.NOT_APPLICABLE));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
        hostelName.setCellValue(commonResponseUtil.getMessage("message.label.hostel.name") + ModelConstants.SPACE +
                ModelConstants.COLAN  + ModelConstants.SPACE + Optional.ofNullable(studentDetails.getHostelName()).orElse(ModelConstants.NOT_APPLICABLE));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 4));
        hostelDeposit.setCellValue(
                commonResponseUtil.getMessage("message.noc.hostel.deposit") + ModelConstants.SPACE +
                        ModelConstants.COLAN + ModelConstants.SPACE +
                        String.format(ModelConstants.TWO_DECIMAL_POINT,
                                Optional.ofNullable(studentDetails.getLeadAmount())
                                        .map(Long::doubleValue)
                                        .orElse(0.00d)));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));
        roomNo.setCellStyle(wrappedHeaderStyle);
        hostelName.setCellStyle(wrappedHeaderStyle);
        hostelDeposit.setCellStyle(wrappedHeaderStyle);
    }

    private void performRowThreeCellCreation(XSSFSheet sheet, MessLedgerReportSelectForm selectForm, List<LedgerReportDto> ledgerReportDtoList, XSSFCellStyle wrappedHeaderStyle) {
        var row5 = sheet.createRow(3);
        var dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
        var fromDate = row5.createCell(0);
        var toDate = row5.createCell(2);
        var reportDate = row5.createCell(4);
        var sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
        var reportDateField = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
        fromDate.setCellValue(commonResponseUtil.getMessage("message.label.from.date") + ModelConstants.SPACE +
                ModelConstants.COLAN + ModelConstants.SPACE + (Objects.nonNull(selectForm.getFromDate()) ? selectForm.getFromDate().format(dateFormatter) : ledgerReportDtoList.getFirst().getVoucherDate().format(dateFormatter)));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
        toDate.setCellValue(commonResponseUtil.getMessage("message.label.to.date") + ModelConstants.SPACE +
                ModelConstants.COLAN + ModelConstants.SPACE + (Objects.nonNull(selectForm.getToDate()) ? selectForm.getToDate().format(dateFormatter) : ledgerReportDtoList.getLast().getVoucherDate().format(dateFormatter)));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));
        reportDate.setCellValue(reportDateField);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 6));
        fromDate.setCellStyle(wrappedHeaderStyle);
        toDate.setCellStyle(wrappedHeaderStyle);
        reportDate.setCellStyle(wrappedHeaderStyle);
    }

    private void performRowFourCellCreation(XSSFSheet sheet, Double openingBal, XSSFCellStyle wrappedHeaderStyle) {
        var row6 = sheet.createRow(4);
        var openBal = row6.createCell(4);
        openBal.setCellValue(commonResponseUtil.getMessage("message.label.opening.balance") + ModelConstants.SPACE +
                ModelConstants.COLAN + ModelConstants.SPACE + Optional.ofNullable(String.format(ModelConstants.TWO_DECIMAL_POINT, openingBal)).orElse(String.valueOf(0.00d)) + commonResponseUtil.getMessage("message.label.dr"));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 6));
        openBal.setCellStyle(wrappedHeaderStyle);
    }

    private void performRowFiveCellCreation(XSSFSheet sheet, StudentChargesDto studentDetails, XSSFCellStyle wrappedHeaderStyle) {
        var row7 = sheet.createRow(5);
        var initBal = row7.createCell(4);
        initBal.setCellValue(
                commonResponseUtil.getMessage("message.label.initial.opening.balance") + ModelConstants.SPACE +
                        ModelConstants.COLAN + ModelConstants.SPACE +
                        String.format(ModelConstants.TWO_DECIMAL_POINT,
                                Optional.ofNullable(studentDetails.getOpeningBalance())
                                        .map(Long::doubleValue)
                                        .orElse(0.00d)) +
                        commonResponseUtil.getMessage("message.label.dr"));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 4, 6));
        initBal.setCellStyle(wrappedHeaderStyle);
    }

    private void createMessLedgerHeader(XSSFCell cellHeader, XSSFCellStyle wrappedHeaderStyle, XSSFRow row, XSSFSheet sheet, XSSFCellStyle headerStyle) {
        String titleText = String.join(ModelConstants.NEW_LINE, ExcelConstants.MESS_LEDGER_REPORT_EXCEL_HEADER);
        cellHeader.setCellValue(titleText);
        wrappedHeaderStyle.cloneStyleFrom(headerStyle);
        wrappedHeaderStyle.setWrapText(true);
        cellHeader.setCellStyle(wrappedHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        row.setHeightInPoints(ExcelConstants.MESS_LEDGER_REPORT_EXCEL_HEADER.size() * sheet.getDefaultRowHeightInPoints());
    }

    private void performTotalAmountRowCellCreation(XSSFSheet sheet, AtomicInteger rowIndex, String[] headers, XSSFCellStyle headerStyle, Map<String, Double> totalAmount) {
        var totalRow = sheet.createRow(rowIndex.getAndIncrement());
        var formattedCredit = String.format(ModelConstants.TWO_DECIMAL_POINT, totalAmount.getOrDefault(Constants.TOTAL_CREDIT, 0.00d));
        var formattedDebit = String.format(ModelConstants.TWO_DECIMAL_POINT, totalAmount.getOrDefault(Constants.TOTAL_DEBIT, 0.00d));
        var formattedClosing = formatTransactionAmount(totalAmount.getOrDefault(Constants.TOTAL_CLOSING_BAL, 0.00d));
        totalRow.createCell(headers.length - 4).setCellValue(commonResponseUtil.getMessage("message.label.total") + ModelConstants.SPACE +
                Constants.DOLLER + ModelConstants.SPACE + ModelConstants.COLAN);
        totalRow.getCell(headers.length - 4).setCellStyle(headerStyle);

        totalRow.createCell(headers.length - 3).setCellValue(formattedCredit);
        totalRow.getCell(headers.length - 3).setCellStyle(headerStyle);

        totalRow.createCell(headers.length - 2).setCellValue(formattedDebit);
        totalRow.getCell(headers.length - 2).setCellStyle(headerStyle);

        totalRow.createCell(headers.length - 1).setCellValue(formattedClosing);
        totalRow.getCell(headers.length - 1).setCellStyle(headerStyle);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLedgerEntry(String voucherNo,String studentId,int slNo) throws RecordNotExistsException {
        Boolean a=false;
        Boolean b=false;
        Optional<MessLedgerAEntity> ledgerA = messLedgerARepository.findByIdVoucherNoAndAccheadAndActiveFlag(voucherNo, studentId,ModelConstants.STATUS_ACTIVE);
        if(ledgerA.isPresent()) {
             a = deleteMessLedgerA(ledgerA.get());

             b = messLedgerBRepository.findByIdVoucherNoAndActiveFlag(voucherNo, ModelConstants.STATUS_ACTIVE)
                    .map(this::deleteMessLedgerB)
                    .orElse(false);
        }else{
            Optional<MessLedgerBEntity> ledgerB = messLedgerBRepository.findByIdVoucherNoAndAccheadAndIdSlnoAndActiveFlag(voucherNo, studentId,slNo,ModelConstants.STATUS_ACTIVE);
            if(ledgerB.isPresent()) {
                b = deleteMessLedgerB(ledgerB.get());
                Optional<Integer> slNoSum = messLedgerBRepository.findTotalSlNo(voucherNo,ModelConstants.STATUS_ACTIVE);
                if(slNoSum.isPresent()) {
                    if(slNoSum.get()<=1) {
                        a = messLedgerARepository.findByIdVoucherNoAndActiveFlag(voucherNo, ModelConstants.STATUS_ACTIVE)
                                .map(this::deleteMessLedgerA)
                                .orElse(false);
                    }
                }
            }
        }
        if(a || b){
            return true;
        }
        else{
            throw new RecordNotExistsException("Voucher No: "+voucherNo+" does not exist");
        }
    }

    public  boolean deleteMessLedgerA(MessLedgerAEntity entity){
        entity.setCancelStatus(ModelConstants.STATUS_ACTIVE);
        entity.onUpdate();
        messLedgerARepository.save(entity);
        return true;
    }

    public  boolean deleteMessLedgerB(MessLedgerBEntity entity){
        entity.setCancelStatus(ModelConstants.STATUS_ACTIVE);
        entity.onUpdate();
        messLedgerBRepository.save(entity);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean undoSettlement(String studentId) throws Exception {
        boolean status=false;
        try {
            Optional<StudentDetailsInfoEntity> byStudentId = studentDetailsInfoRepository.findByStudentId(studentId.toUpperCase());
            if (byStudentId.isPresent()) {
                StudentDetailsInfoEntity studentDetailsInfoEntity = byStudentId.get();
                studentDetailsInfoEntity.setSettlementFlag(ModelConstants.STATUS_INACTIVE);
                studentDetailsInfoRepository.save(studentDetailsInfoEntity);
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
}
