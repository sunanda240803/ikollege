package com.iitm.hosteldine.service.reports;

public record MessPriorityRecord(
        String studentId,
        String studentName,
        String gender,
        String messPriority,
        String messName,
        String messOption,
        String lastModifiedTime,
        String joiningTime,
        String registrationType
) {
}
