package com.iitm.hosteldine.service.reports;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FeedbackWeightageViewRecord(
        Long messMasterId,
        String messName,
        Long studentCount,
        BigDecimal overallWeightage,
        LocalDate mmcDDiningFromDate,
        LocalDate mmcDDiningToDate
) {
}
