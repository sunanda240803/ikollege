package com.iitm.hosteldine.controller.reports;

public record StudentRollChangeRecord(
    String requestedDate,
    String previousRollNo,
    String studentName,
    String changedRollNo,
    String fileName,
    Long id,
    String status
) {
}
