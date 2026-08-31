package com.iitm.hosteldine.service.dashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.studentDashboard.LedgerReportDto;
import com.iitm.hosteldine.form.common.SelectForm;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessOpeningBalService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerReportService {

    private final MessLedgerARepository messLedgerARepository;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final MessMasterCommonService messMasterCommonService;
    private final MessOpeningBalService messOpeningBalService;
    private final Utility utility;
    private final CommonResponseUtil commonResponseUtil;

    public Map<String, Map<String, Object>> getLedgerReport(SelectForm selectForm) {
        var studentId = SecurityCtxUtil.userId().toUpperCase();
        List<String> accountHeads = new ArrayList<>();
        accountHeads.add(studentId);

        StudentDetailsInfoDto studentInfo = studentDetailsInfoService.getStudentInfoDetails(studentId);

        Optional.ofNullable(studentInfo)
                .map(StudentDetailsInfoDto::getPreviousId)
                .filter(id -> !id.isEmpty())
                .ifPresent(previousId -> accountHeads.addAll(Arrays.asList(previousId.split(","))));

        var runningBalance = new AtomicReference<>(messOpeningBalService.getOpeningBal());

        var ledgerReport = messLedgerARepository.getLedgerReport(selectForm.getSelectedCriteria().equals("Mess")?"MS":"CC", accountHeads)
                .stream()
                .map(this::mapToLedgerReportDto)
                .sorted(Comparator.comparing(LedgerReportDto::getVoucherDate).thenComparing(LedgerReportDto::getCreatedAt))
                .map(dto -> setClosingBalance(dto, runningBalance))
                .collect(Collectors.groupingBy(this::organizeBySemester, LinkedHashMap::new, Collectors.toList()));

        return ledgerReport.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateTransactions(entry.getValue()),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
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

    public List<LedgerReportDto> getFoodCourtReport() {
        Long messMasterId = Optional.ofNullable(messMasterCommonService.getCurrentMessPeriod())
                .map(MessMasterControllerDto::getId)
                .orElse(0L);

        var runningBalance = new AtomicReference<>(0.0);
        return messLedgerARepository.getFilteredFoodCourtReport(SecurityCtxUtil.userId().toUpperCase(), messMasterId)
                .map(list->list.stream()
                        .map(this::mapToFoodCourtReportDto)
                        .sorted(Comparator.comparing(LedgerReportDto::getVoucherDate))
                        .map(dto -> setClosingBalance(dto, runningBalance))
                        .toList())
                .orElse(Collections.emptyList());
    }

    private LedgerReportDto mapToLedgerReportDto(Object[] o) {
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
                .voucherDate(o[1] != null ? utility.convertToLocalDate(o[1].toString().split(" ")[0]) : null)
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

}
