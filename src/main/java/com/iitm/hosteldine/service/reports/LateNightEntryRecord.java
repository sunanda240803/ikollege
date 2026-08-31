package com.iitm.hosteldine.service.reports;

public record LateNightEntryRecord(
        String studentId,
        String studentName,
        String hostelName,
        String swipeDay,
        String swipeDate,
        String swipeTime
) {
}
