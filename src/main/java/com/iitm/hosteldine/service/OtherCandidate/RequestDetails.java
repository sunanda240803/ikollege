package com.iitm.hosteldine.service.OtherCandidate;

import java.time.LocalDate;

public record RequestDetails(
        Long stayId,
        LocalDate stayFrom,
        LocalDate stayTo,
        LocalDate accStayFrom,
        LocalDate accStayTo,
        Long requestId,
        String email,
        String gender,
        Long roomId,
        Long hostelId) {
}
