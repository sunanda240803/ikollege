package com.iitm.hosteldine.service.hostel;

public record WardenFacultyRecord(
        String wardenEmail,
        String facultyEmail,
        String hostelName,
        String studentId,
        String wardenName,
        String hostelOfficeName
) {
}
