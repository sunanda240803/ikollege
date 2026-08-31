package com.iitm.hosteldine.service.warden;

import java.sql.Date;

public record WardenAwayRequestRecord(
        Long id,

        Long wardenId,
        String wardenName,
        String wardenEmail,

        Long inchargerId,
        String inchargerName,
        String inchargerEmail,

        Date awayFrom,
        Date awayTo,
        String awayDescription,

        String wardenHostels,
        String hostelOfficeEmails,
        String inchargeHostels
) {
}
