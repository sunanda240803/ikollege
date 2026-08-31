package com.iitm.hosteldine.service.OtherCandidate;

import com.iitm.hosteldine.constant.Constants;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CurrentStayExtensionDetails(
        @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
        LocalDate stayFrom,
        @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
        LocalDate stayTo,
        Boolean dining,
        String description,
        Long candidateId,
        Long appointmentId,
        LocalDateTime modifiedAt,
        Long workflowId,
        String approvalNotes,
        String rejectionDescription,
        String messOption,
        String hostelName,
        Integer roomNo) {
}
