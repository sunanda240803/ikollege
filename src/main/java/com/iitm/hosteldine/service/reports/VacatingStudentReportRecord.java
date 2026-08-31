package com.iitm.hosteldine.service.reports;

import java.time.LocalDate;

public record VacatingStudentReportRecord (
        String studentName,
        String acountName,
        String studentId,
        Double creditAmt,
        Double debitAmt,
        Double netBal,
        String bankNameOne,
        String branchNameOne,
        String bankAccountNoOne,
        String ifsCodeOne,
        Boolean donationStatus,
        Long donationAmount,
        Long penalityAmount,
        String vacatingReason,
        String othersVacatingReason,
        LocalDate vacatingDate,
        String studentAddress,
        LocalDate exchangeProgPeriodFromDate,
        LocalDate exchangeProgPeriodToDate,
        String donatorType,
        String othersDescription,
        String placeOfVisit,
        String hostelName,
        String donationHostel,
        String settlementFlag,
        String accHead,
        Long mobNum,
        LocalDate settlementDate,
        String penaltyReason
        ) {
}
