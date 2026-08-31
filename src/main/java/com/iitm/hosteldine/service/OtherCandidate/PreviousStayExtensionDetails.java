package com.iitm.hosteldine.service.OtherCandidate;

import java.time.LocalDate;

public record PreviousStayExtensionDetails(
        Long candidateId,
        Long requestId,
        Long stayId,
        LocalDate appointmentFrom,
        LocalDate appointmentTo,
        LocalDate stayFrom,
        LocalDate stayTo,
        String appStatus,
        String approvalNotes,
        LocalDate approvalDate,
        Boolean dining,
        String validatorName,
        String validatorEmail,
        String authorityType,
        String approvalStatus,
        String messOption,
        String hostelName,
        Integer roomNo) {
}
