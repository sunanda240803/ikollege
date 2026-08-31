package com.iitm.hosteldine.service.OtherCandidate;

import java.time.LocalDate;

public record ExistingStayExtensionDetails(
        LocalDate stayFrom,
        LocalDate stayTo,
        Boolean stayDining,
        String stayDescription,
        LocalDate appointmentFrom,
        LocalDate appointmentTo,
        LocalDate accStayFrom,
        LocalDate accStayTo,
        String stayApprovalStatus,
        String stayStatusNotes,
        Double grossPay,
        String category,
        String categoryOthers,
        Boolean accomDining,
        String validatingAuthority,
        String validatingAuthorityEmail,
        String stayMessOption,
        String accomMessOption,
        Integer hostelId,
        Integer roomNo
) {
}
