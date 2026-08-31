package com.iitm.hosteldine.service.reports;

import java.time.LocalDate;

public record FeedbackCatererDetailsRecord(
        Long messMasterId,
        String messName,
        Integer feedQuesId,
        String feedQuesDesc,
        Integer feedbackWeightage,
        Long feedbackScore,
        Integer studentCount,
        Double overallWeightage,
        LocalDate diningFromDate,
        LocalDate diningToDate
) {}

